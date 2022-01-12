package ngsdiaglim.cnv.caller;


import ngsdiaglim.cnv.CNV;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.enumerations.CNVTypes;
import ngsdiaglim.stats.MAD;
import ngsdiaglim.utils.MathUtils;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CNVDetectionRobustZScore extends CNVDetection {

//    private static final NormalDistribution nd = new NormalDistribution(0, 1);
    private double alpha = 0.99;
    private int minConsecutiveAmplicons = 3;
    private boolean mergeCNV = true;
    public static final double delThreshold = -2.5;
    public static final double dupThreshold = 2.5;

    private CNVDetectionRobustZScore(Builder builder) {

        super.cnvData = builder.cnvData;
        setAlpha(builder.alpha);
        setMinConsecutiveAmplicons(builder.minConsecutiveAmplicons);
        setMergeCNV(builder.mergeCNV);
        amplicons = super.cnvData.getAllCovcopRegionsAsList();
    }

    public void callCNVs() {
        clearOldCVNs();
        computeZScores();
        getCNVs();
    }

    private void computeZScores() {

        int sampleIndex = 0;
        for (String ignored : cnvData.getSamples().keySet()) {

            List<Double> sampleValues = new ArrayList<>();

            for (String poolName : cnvData.getCovcopRegions().keySet()) {
                for (CovCopRegion a : cnvData.getCovcopRegions().get(poolName)) {
                    if (a.getNormalized_values().get(sampleIndex) != null) {
                        sampleValues.add(a.getNormalized_values().get(sampleIndex));
                    }
                }
            }
            Double median = MathUtils.median(sampleValues);
            Double mad = MAD.getMAD(sampleValues);

            for (String poolName : cnvData.getCovcopRegions().keySet()) {
                for (CovCopRegion a : cnvData.getCovcopRegions().get(poolName)) {
                    if (a.getNormalized_values().get(sampleIndex) != null && mad != null && median != null) {
//                        Double zScore = (madCoeff * (a.getNormalized_value().get(sampleIndex) - median)) / mad;
                        Double zScore = MAD.getZScore(median, mad, a.getNormalized_values().get(sampleIndex));
                        a.addZScore(zScore, sampleIndex);
                    } else {
                        a.addZScore(null, sampleIndex);
                    }
                }
            }

            sampleIndex++;
        }

    }


    protected void getCNVs() {

        int sampleIndex = 0;
        for (String sampleName : cnvData.getSamples().keySet()) {

            CNVSample sample = cnvData.getSamples().get(sampleName);

            String contig = "";
            List<CovCopRegion> dupAmplicons = new ArrayList<>();
            List<CovCopRegion> delAmplicons = new ArrayList<>();

            for (int ampIndex = 0; ampIndex < amplicons.size(); ampIndex++) {

                CovCopRegion a = amplicons.get(ampIndex);

                if (!a.getContig().equals(contig)) {
                    setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, ampIndex);
                    setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, ampIndex);
                    delAmplicons.clear();
                    dupAmplicons.clear();
                }


                Double zScore = a.getzScores().get(sampleIndex);
                if (zScore == null) {
                    dupAmplicons.add(a);
                    delAmplicons.add(a);
                }
                else {
                    if (zScore < delThreshold) {
                        delAmplicons.add(a);
                        setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, ampIndex);
                        dupAmplicons.clear();
                    }
                    else if (zScore > dupThreshold) {
                        dupAmplicons.add(a);
                        setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, ampIndex);
                        delAmplicons.clear();
                    }
                    else {
                        setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, ampIndex);
                        setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, ampIndex);
                        delAmplicons.clear();
                        dupAmplicons.clear();
                    }
                }

                contig = a.getContig();
            }
            setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, amplicons.size());
            setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, amplicons.size());

            mergeCNV(sample, amplicons);
            cleanShortCNVs(sample);
            sampleIndex++;
        }
    }


    private void mergeCNV(CNVSample sample, List<CovCopRegion> allAmplicons) {
        ListIterator<CNV> iter = sample.getCNV().listIterator();
        NormalDistribution nd = new NormalDistribution(0, 1);
        double lowDelScore = nd.inverseCumulativeProbability(1 - 0.95);
        double lowDupScore = nd.inverseCumulativeProbability(0.95);
        int sampleIndex = cnvData.getSampleIndex(sample.getBarcode());
        while (iter.hasNext()) {

            if (iter.hasPrevious()) {

                CNV actualCNV = iter.next();
                iter.previous();
                CNV prevCNV = iter.previous();
                iter.next();

                if (actualCNV.getAmpliconsList().size() >= minConsecutiveAmplicons || prevCNV.getAmpliconsList().size() >= minConsecutiveAmplicons) {
                    if (prevCNV.getContig().equals(actualCNV.getContig()) && prevCNV.getCnvTypes().equals(actualCNV.getCnvTypes())) {
                        if (actualCNV.getFirstAmpliconIndex() - prevCNV.getLastAmpliconIndex() <= 2) {
                            Double zScore = allAmplicons.get(prevCNV.getLastAmpliconIndex() + 1).getNormalized_values().get(sampleIndex);
                            if ((actualCNV.getCnvTypes().equals(CNVTypes.DELETION) && zScore < lowDelScore)
                                    || (actualCNV.getCnvTypes().equals(CNVTypes.DUPLICATION) && zScore > lowDupScore)) {
                                CNV mergedCNV = new CNV(actualCNV.getCnvTypes(),
                                        actualCNV.getContig(),
                                        prevCNV.getStart(),
                                        actualCNV.getEnd(),
                                        allAmplicons.subList(prevCNV.getFirstAmpliconIndex(), actualCNV.getLastAmpliconIndex()),
                                        actualCNV.getLastAmpliconIndex());
                                iter.previous();
                                iter.remove();
                                iter.next();
                                iter.remove();
                                iter.add(mergedCNV);
                                iter.previous();
                            }
                        }
                    }
                }
            }
            iter.next();
        }
    }

    private void cleanShortCNVs(CNVSample sample) {
        sample.getCNV().removeIf(cnv -> cnv.getAmpliconsList().size() < minConsecutiveAmplicons);
    }

    private void setCNV(CNVSample sample, int sampleIndex, List<CovCopRegion> amplicons, CNVTypes cnvType, int lastAmpIndex) {
        if (!amplicons.isEmpty()) {
            // trim null values from each ends
            Iterator<CovCopRegion> iter = amplicons.iterator();
            while (iter.hasNext() && iter.next().getNormalized_values().get(sampleIndex) == null) {
                iter.remove();
            }
            ReverseListIterator<CovCopRegion> reverseIter =  new ReverseListIterator<>(amplicons);
            while (reverseIter.hasNext() && reverseIter.next().getNormalized_values().get(sampleIndex) == null) {
                reverseIter.remove();
            }
            if (!amplicons.isEmpty()) {
                CNV cnv = new CNV(
                        cnvType,
                        amplicons.get(0).getContig(),
                        amplicons.get(0).getStart(),
                        amplicons.get(amplicons.size() - 1).getEnd(),
                        amplicons,
                        lastAmpIndex
                );
                sample.addCNV(cnv);
            }
        }
    }

    public double getAlpha() { return alpha; }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public int getMinConsecutiveAmplicons() { return minConsecutiveAmplicons; }

    public void setMinConsecutiveAmplicons(int minConsecutiveAmplicons) {
        this.minConsecutiveAmplicons = minConsecutiveAmplicons;
    }

    public boolean isMergeCNV() { return mergeCNV; }

    public void setMergeCNV(boolean mergeCNV) {
        this.mergeCNV = mergeCNV;
    }


    public static final class Builder {
        private final CovCopCNVData cnvData;
        private double alpha;
        private int minConsecutiveAmplicons;
        private boolean mergeCNV;

        public Builder(CovCopCNVData cnvData) {
            this.cnvData = cnvData;
        }

        public Builder alpha(double val) {
            alpha = val;
            return this;
        }

        public Builder minConsecutiveAmplicons(int val) {
            minConsecutiveAmplicons = val;
            return this;
        }

        public Builder mergeCNV(boolean val) {
            mergeCNV = val;
            return this;
        }

        public CNVDetectionRobustZScore build() {
            return new CNVDetectionRobustZScore(this);
        }
    }
}