package ngsdiaglim.enumerations;

import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum EnsemblConsequence {

    TRANSCRIPT_ABLATION("Transcript ablation", Color.web("#ff0000"), "A feature ablation whereby the deleted region includes a transcript feature", "HIGH", 1, 1),
    SPLICE_ACCEPTOR_VARIANT("Splice acceptor variant", Color.web("#FF581A"), "A splice variant that changes the 2 base region at the 3' end of an intron", "HIGH", 2, 1),
    SPLICE_DONOR_VARIANT("Splice donor variant", Color.web("#FF581A"), "A splice variant that changes the 2 base region at the 5' end of an intron", "HIGH", 3 ,1),
    STOP_GAINED("Stop gained", Color.web("#ff0000"), "A sequence variant whereby at least one base of a codon is changed, resulting in a premature stop codon, leading to a shortened transcript", "HIGH", 5, 1),
    FRAMSHIFT_VARIANT("Framshift variant", Color.web("#9400D3"), "A sequence variant which causes a disruption of the translational reading frame, because the number of nucleotides inserted or deleted is not a multiple of three", "HIGH", 6, 1),
    STOP_LOST("Stop lost", Color.web("#ff0000"), "A sequence variant where at least one base of the terminator codon (stop) is changed, resulting in an elongated transcript", "HIGH", 7, 1),
    START_LOST("Start lost", Color.web("#ffd700"), "A codon variant that changes at least one base of the canonical start codon", "HIGH", 8, 1),
    TRANSCRIPT_AMPLIFICATION("Transcript amplification", Color.web("#ff69b4"), "A feature amplification of a region containing a transcript", "HIGH", 9, 1),
    INFRAME_INSERTION("Inframe insertion", Color.web("#ff69b4"), "An inframe non synonymous variant that inserts bases into in the coding sequence", "MODERATE", 10, 2),
    INFRAME_DELETION("Inframe deletion", Color.web("#ff69b4"), "An inframe non synonymous variant that deletes bases from the coding sequence", "MODERATE", 11, 2),
    MISSENSE_VARIANT("Missense variant", Color.web("#ffd700"), "A sequence variant, that changes one or more bases, resulting in a different amino acid sequence but where the length is preserved", "MODERATE", 12, 2),
    PROTEIN_ALTERING_VARIANT("Protein altering variant", Color.web("#FF0080"), "A sequence_variant which is predicted to change the protein encoded in the coding sequence", "MODERATE", 13, 2),
    SPLICE_REGION_VARIANT("Splice region variant", Color.web("#ff7f50"), "A sequence variant in which a change has occurred within the region of the splice site, either within 1-3 bases of the exon or 3-8 bases of the intron", "LOW", 14, 3),
    INCOMPLETE_TERMINAL_CODON_VARIANT("Incomplete terminal codon variant", Color.web("#ff00ff"), "A sequence variant where at least one base of the final codon of an incompletely annotated transcript is changed", "LOW", 15, 3),
    START_RETAINED_VARIANT("Start retained variant", Color.web("#76ee00"), "A sequence variant where at least one base in the start codon is changed, but the start remains", "LOW", 16, 3),
    STOP_RETAINED_VARIANT("Stop retained variant", Color.web("#76ee00"), "A sequence variant where at least one base in the terminator codon is changed, but the terminator remains", "LOW", 17, 3),
    SYNONYMOUS_VARIANT("Synonymous variant", Color.web("#76ee00"), "A sequence variant where there is no resulting change to the encoded amino acid", "LOW", 18, 3),
    CODING_SEQUENCE_VARIANT("Coding sequence variant", Color.web("#458b00"), "A sequence variant that changes the coding sequence", "MODIFIER", 19, 4),
    MATURE_MIRNA_VARIANT("Mature miRNA variant", Color.web("#458b00"), "A transcript variant located with the sequence of the mature miRNA", "MODIFIER", 20, 4),
    PRIME_5_UTR_VARIANT("5' UTR variant", Color.web("#7ac5cd"), "A UTR variant of the 5' UTR", "MODIFIER", 21, 4),
    PRIME_3_UTR_VARIANT("3' UTR variant", Color.web("#7ac5cd"), "A UTR variant of the 3' UTR", "MODIFIER", 22, 4),
    NON_CODING_TRANSCRIPT_EXON_VARIANT("Non coding transcript exon variant", Color.web("#32cd32"), "A sequence variant that changes non-coding exon sequence in a non-coding transcript", "MODIFIER", 23, 4),
    INTRON_VARIANT("Intron variant", Color.web("#02599c"), "A transcript variant occurring within an intron", "MODIFIER", 24, 4),
    NMD_TRANSCRIPT_VARIANT("NMD transcript variant", Color.web("#ff4500"), "A variant in a transcript that is the target of NMD", "MODIFIER", 25, 4),
    NON_CODING_TRANSCRIPT_VARIANT("Non coding transcript variant", Color.web("#32cd32"), "A transcript variant of a non coding RNA gene", "MODIFIER", 26, 4),
    UPSTREAM_GENE_VARIANT("Upstream gene variant", Color.web("#a2b5cd"), "A sequence variant located 5' of a gene", "MODIFIER", 27, 4),
    DOWNSTREAM_GENE_VARIANT("Downstream gene variant", Color.web("#a2b5cd"), "A sequence variant located 3' of a gene", "MODIFIER", 28, 4),
    TFBS_ABLATION("TFBS ablation", Color.web("#a52a2a"), "A feature ablation whereby the deleted region includes a transcription factor binding site", "MODIFIER", 29, 4),
    TFBS_AMPLIFICATION("TFBS amplification", Color.web("#a52a2a"), "A feature amplification of a region containing a transcription factor binding site", "MODIFIER", 30, 4),
    TF_BINDING_SITE_VARIANT("TF binding site variant", Color.web("#a52a2a"), "A sequence variant located within a transcription factor binding site", "MODIFIER", 31, 4),
    REGULATORY_REGION_ABLATION("Regulatory region ablation", Color.web("#a52a2a"), "A feature ablation whereby the deleted region includes a regulatory region", "MODERATE", 32, 4),
    REGULATORY_REGION_AMPLIFICATION("Regulatory region amplification", Color.web("#a52a2a"), "A feature amplification of a region containing a regulatory region", "MODIFIER", 33, 4),
    FEATURE_ELONGATION("Feature elongation", Color.web("#7f7f7f"), "A sequence variant that causes the extension of a genomic feature, with regard to the reference sequence", "MODIFIER", 34, 4),
    REGULATORY_REGION_VARIANT("Regulatory region variant", Color.web("#a52a2a"), "A sequence variant located within a regulatory region", "MODIFIER", 35, 4),
    FEATURE_TRUNCATION("Feature truncation", Color.web("#7f7f7f"), "A sequence variant that causes the reduction of a genomic feature, with regard to the reference sequence", "MODIFIER", 36, 4),
    INTERGENIC_VARIANT("Intergenic variant", Color.web("#636363"), "A sequence variant located in the intergenic region, between genes", "MODIFIER", 37, 4);

    private final static Logger logger = LogManager.getLogger(EnsemblConsequence.class);
    private final String name;
    private final Color color;
    private final String desc;
    private final String impact;
    private final int rank;
    private final Integer weight;

    EnsemblConsequence(String n, Color c, String d, String i, int r, int w) {
        name = n;
        color = c;
        desc = d;
        impact = i;
        rank = r;
        weight = w;
    }

    public String getName() { return name; }

    public Color getColor() { return color; }

    public String getImpact() { return impact; }

    public String getDesc() {
        return desc;
    }

    public int getRank() { return rank; }

    public Integer getWeight() { return weight; }

    public String toString() { return name; }

    public static EnsemblConsequence fromString(String s) {
        try {
            return EnsemblConsequence.valueOf(s);
        } catch (IllegalArgumentException e) {
            logger.error(e);
            return null;
        }
    }
}
