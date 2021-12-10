package ngsdiaglim.enumerations;

public enum PredictionTools {
    SIFT("Sift", "Scores range from 0 to 1.\n" +
            "If score is smaller than 0.05 the corresponding nsSNV is predicted as \"D(amaging)\"; otherwise it is predicted as \"T(olerated)\"."),
    POLYPHEN2_HVAR("Polyphen2_hvar", "Polyphen2 prediction based on HumVar,\n\"D\" (\"probably damaging\", HVAR score in [0.909,1]),\n" +
            "\n\"P\" (\"possibly damaging\", HVAR score in [0.447,0.908]) and \n\"B\" (\"benign\", HVAR score in [0,0.446])."),
    POLYPHEN2_HDIV("Polyphen2_hdiv", "Polyphen2 prediction based on HumDiv, \n\"D\" (\"probably damaging\", HDIV score in [0.957,1]),\n" +
            "\n\"P\" (\"possibly damaging\", HDIV score in [0.454,0.956]) and \n\"B\" (\"benign\", HDIV score in [0,0.452])."),
    CADD_RAW("CADD raw", "Scores range from -6.458163 to 18.301497. The larger the score the more likely the SNP has damaging effect."),
    CADD_PHRED("CADD phred", "CADD phred-like score. This is phred-like rank score based on whole genome CADD raw scores.\n" +
            "The larger the score the more likely the SNP has damaging effect."),
    REVEL("Revel", "REVEL is an ensemble score based on 13 individual scores for predicting the pathogenicity of missense variants. Scores range from 0 to 1.\n" +
            "The larger the score the more likely the SNP has damaging effect."),
    MVP("MVP", "A pathogenicity prediction score for missense variants using deep learning approach.\n" +
            "The range of MVP score is from 0 to 1. The larger the score, the more likely the variant is pathogenic.\n" +
            "The authors suggest thresholds of 0.7 and 0.75 for separating damaging vs tolerant variants in constrained genes (ExAC pLI >=0.5)\nand non-constrained genes (ExAC pLI<0.5), respectively."),
    GERP("GERP_RS++", "Scores range from -12.3 to 6.17. The larger the score, the more conserved the site."),
    SPLICE_AI("SpliceAI", "SpliceAI is a deep neural network, developed by Illumina, that predicts splice junctions from an arbitrary pre-mRNA transcript sequence.\n" +
            "Delta score of a variant, defined as the maximum of (DS_AG, DS_AL, DS_DG, DS_DL), ranges from 0 to 1 and can be interpreted as the probability of the variant being splice-altering.\n" +
            "The author-suggested cutoffs are:\n" +
            "0.2 (high recall)\n" +
            "0.5 (recommended)\n" +
            "0.8 (high precision)"),
    MCAP("M-CAP", "Scores range from 0 to 1. The larger the score the more likely the SNP has damaging effect.\n" +
            "The score cutoff between \"D(amaging)\" and \"T(olerated)\" is 0.025."),
    FATHMM("FATHMM", "Scores range from -16.13 to 10.64. The smaller the score the more likely the SNP has damaging effect.\n" +
            "If a score is <=-1.5 the corresponding nsSNV is predicted as \"D(AMAGING)\" otherwise it is predicted as \"T(OLERATED)\"."),
    VEST4("VEST4", "Score ranges from 0 to 1. The larger the score the more likely the mutation may cause functional change."),
    MetaSVM("MetaSVM", "support vector machine (SVM) based ensemble prediction score, which incorporated 10 scores\n" +
            "(SIFT, PolyPhen-2 HDIV, PolyPhen-2 HVAR, GERP++, MutationTaster, Mutation Assessor, FATHMM, LRT, SiPhy, PhyloP)\n" +
            "and the maximum frequency observed in the 1000 genomes populations. Larger value means the SNV is more likely to be damaging. Scores range from -2 to 3.\n" +
            "\"D(amaging)\" if score > 0; otherwise \"T(olerated)\""),
    MetaLR("MetaSVM", "logistic regression (LR) based ensemble prediction score, which incorporated 10 scores\n" +
            "(SIFT, PolyPhen-2 HDIV, PolyPhen-2 HVAR, GERP++, MutationTaster, Mutation Assessor, FATHMM, LRT, SiPhy, PhyloP)\n" +
            "and the maximum frequency observed in the 1000 genomes populations. Larger value means the SNV is more likely to be damaging. Scores range from 0 to 1.\n" +
            "\"D(amaging)\" if score > 0.5; otherwise \"T(olerated)\""),
    PHYLOP100WAY_VERTEBRATE("phyloP100way_vertebrate", "Conservation score based on the multiple alignments of 100 vertebrate genomes (including human).\n" +
            "The larger the score, the more conserved the site. Scores range from -20.0 to 10.003"),
    PHYLOP30WAY_MAMMALIAN("phyloP30way_mammalian", "Conservation score based on the multiple alignments of 30 mammalian genomes (including human).\n" +
            "The larger the score, the more conserved the site. Scores range from -20 to 1.312"),
    PHASTCONS100WAY_VERTEBRATE("phastCons100way_vertebrate", "Conservation score based on the multiple alignments of 100 vertebrate genomes (including human).\n" +
            "The larger the score, the more conserved the site. Scores range from 0 to 1."),
    PHASTCONS30WAY_MAMMALIAN("phastCons30way_mammalian", "Conservation score based on the multiple alignments of 30 mammalian genomes (including human).\n" +
            "The larger the score, the more conserved the site. Scores range from 0 to 1"),
    SIPHY("SiPhy", "The estimated stationary distribution of A, C, G and T at the site, using SiPhy algorithm based on 29 mammals genomes.\n" +
            "The larger the score, the more conserved the site. Scores range from 0 to 37.9718"),
    dbscSNV_ADA("splicing_consensus_ada_score", "splicing-change prediction for splicing consensus SNPs based on adaboost.\n" +
            "If the score >0.6, it predicts that the splicing will be changed, otherwise it predicts the splicing will not be changed."),
    dbscSNV_RF("splicing_consensus_rf_score", "splicing-change prediction for splicing consensus SNPs based on random forest.\n" +
            "If the score >0.6, it predicts that the splicing will be changed, otherwise it predicts the splicing will not be changed."),
    ;

    private final String name;
    private final String desc;

    PredictionTools(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {return name;}

    public String getDesc() {return desc;}
}
