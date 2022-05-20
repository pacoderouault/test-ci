package ngsdiaglim.comparators;

import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomAD;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.modeles.variants.predictions.PathogenicScoreCalculator;

import java.util.Comparator;

public class AnnotationComparator implements Comparator<Annotation> {

    private static final EnsemblConsequenceComparator ensemblConsequenceComparator = new EnsemblConsequenceComparator();
    private static final NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    @Override
    public int compare(Annotation o1, Annotation o2) {

        // First move false positive at the bottom
        int seqErrorComp = Boolean.compare(o1.getVariant().isFalsePositive(), o2.getVariant().isFalsePositive());
        if (seqErrorComp != 0) return seqErrorComp;

        // Second variant defined pathologic by users
        Integer patho1 = o1.getVariant().getAcmg().getPathogenicityValue();
        Integer patho2 = o2.getVariant().getAcmg().getPathogenicityValue();

        // pathologic or probably patho
        int pathoComp = -patho1.compareTo(patho2);
        if (pathoComp != 0) return pathoComp;

        Double score1 = PathogenicScoreCalculator.getPathogenicScore(o1);
        Double score2 = PathogenicScoreCalculator.getPathogenicScore(o2);
        int scoreComp = score1.compareTo(score2);
        if (scoreComp != 0) return scoreComp;

        // Then move variant with gnomad value <0.01 to the top
        Integer freq1 = 1;
        int freq2 = 1;
        GnomadPopulationFreq maxPop1 = o1.getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME);
        GnomadPopulationFreq maxPop2 = o2.getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME);
        if (maxPop1 == null || maxPop1.getAf() < 0.01) {
            freq1 = 0;
        }
        if (maxPop2 == null || maxPop2.getAf() < 0.01) {
            freq2 = 0;
        }
        int freqComp = freq1.compareTo(freq2);
        if (freqComp != 0) return freqComp;

        // Then move exonic or splicing variant at the top
        int consequenceComp = ensemblConsequenceComparator.compare(o1.getTranscriptConsequence().getConsequence(), o2.getTranscriptConsequence().getConsequence());
        if (consequenceComp != 0) return consequenceComp;

        // Finally sort by genomic position
        int contigComp = naturalSortComparator.compare(o1.getGenomicVariant().getContig(), o2.getGenomicVariant().getContig());
        if (contigComp != 0) return contigComp;

        return Integer.compare(o1.getGenomicVariant().getStart(), o2.getGenomicVariant().getStart());
    }
}
