package ngsdiaglim.enumerations;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum VepTag {
    CONSEQUENCE("Consequence"),
    SYMBOL("SYMBOL"),
    Feature("Feature"),
    EXON("EXON"),
    INTRON("INTRON"),
    HGVSc("HGVSc"),
    HGVSp("HGVSp"),
    cDNA_position("cDNA_position"),
    CDS_position("CDS_position"),
    Protein_position("Protein_position"),
    Codons("Codons"),
    Amino_acids("Amino_acids"),
    Existing_variation("Existing_variation"),
    DISTANCE("DISTANCE"),
    ENSP("ENSP"),
    SIFT("SIFT"),
    SIFT_PRED("SIFT_pred"),
    SIFT_SCORE("SIFT_score"),
    POLYPHEN("Polyphen"),
    POLYPHEN_2HVAR_PRED("Polyphen2_HVAR_pred"),
    POLYPHEN_2HVAR_SCORE("Polyphen2_HVAR_score"),
    POLYPHEN_2HDIV_PRED("Polyphen2_HDIV_pred"),
    POLYPHEN_2HDIV_SCORE("Polyphen2_HDIV_score"),
    gnomAD_AF("gnomAD_AF"),
    gnomAD_AC("gnomAD_AC"),
    gnomAD_AFR_AF("gnomAD_exomes_AFR_AF"),
    gnomAD_AMR_AF("gnomAD_exomes_AMR_AF"),
    gnomAD_ASJ_AF("gnomAD_exomes_ASJ_AF"),
    gnomAD_EAS_AF("gnomAD_exomes_EAS_AF"),
    gnomAD_FIN_AF("gnomAD_exomes_FIN_AF"),
    gnomAD_NFE_AF("gnomAD_exomes_NFE_AF"),
    gnomAD_SAS_AF("gnomAD_exomes_SAS_AF"),
    gnomAD_AFR_AC("gnomAD_exomes_AFR_AC"),
    gnomAD_AMR_AC("gnomAD_exomes_AMR_AC"),
    gnomAD_ASJ_AC("gnomAD_exomes_ASJ_AC"),
    gnomAD_EAS_AC("gnomAD_exomes_EAS_AC"),
    gnomAD_FIN_AC("gnomAD_exomes_FIN_AC"),
    gnomAD_NFE_AC("gnomAD_exomes_NFE_AC"),
    gnomAD_SAS_AC("gnomAD_exomes_SAS_AC"),
    gnomAD_AFR_AN("gnomAD_exomes_AFR_AN"),
    gnomAD_AMR_AN("gnomAD_exomes_AMR_AN"),
    gnomAD_ASJ_AN("gnomAD_exomes_ASJ_AN"),
    gnomAD_EAS_AN("gnomAD_exomes_EAS_AN"),
    gnomAD_FIN_AN("gnomAD_exomes_FIN_AN"),
    gnomAD_NFE_AN("gnomAD_exomes_NFE_AN"),
    gnomAD_SAS_AN("gnomAD_exomes_SAS_AN"),
    CLIN_SIG("CLIN_SIG"),
    clinvar_id("clinvar_id"),
    PUBMED("PUBMED"),
    CADD_RAW("CADD_RAW"),
    CADD_PHRED("CADD_PHRED"),
    SpliceAI_pred_DP_AG("SpliceAI_pred_DP_AG"),
    SpliceAI_pred_DP_AL("SpliceAI_pred_DP_AL"),
    SpliceAI_pred_DP_DG("SpliceAI_pred_DP_DG"),
    SpliceAI_pred_DP_DL("SpliceAI_pred_DP_DL"),
    SpliceAI_pred_DS_AG("SpliceAI_pred_DS_AG"),
    SpliceAI_pred_DS_AL("SpliceAI_pred_DS_AL"),
    SpliceAI_pred_DS_DG("SpliceAI_pred_DS_DG"),
    SpliceAI_pred_DS_DL("SpliceAI_pred_DS_DL"),
    GERP_RS("GERP++_RS"),
    REVEL_score("REVEL_score"),
    MVP_score("MVP_score"),
    MCAP_score("M-CAP_score"),
    MCAP_pred("M-CAP_pred"),
    FATHMM_score("FATHMM_score"),
    FATHMM_pred("FATHMM_pred"),
    VEST4_score("VEST4_score"),
    phastCons100way_vertebrate("phastCons100way_vertebrate"),
    phastCons30way_mammalian("phastCons30way_mammalian"),
    phyloP100way_vertebrate("phyloP100way_vertebrate"),
    phyloP30way_mammalian("phyloP30way_mammalian"),
    SiPhy_29way_logOdds("SiPhy_29way_logOdds"),
    MetaLR_score("MetaLR_score"),
    MetaLR_pred("MetaLR_pred"),
    MetaSVM_score("MetaSVM_score"),
    MetaSVM_pred("MetaSVM_pred"),
    dbscSNV_ADA("ada_score"),
    dbscSNV_RF("rf_score");

    private final String columnName;
    private static final Map<String, VepTag> NAME_ENUM_MAP;


    VepTag(String columnName) {
        this.columnName = columnName;
    }

    static {
        Map<String,VepTag> map = new ConcurrentHashMap<>();
        for (VepTag instance : VepTag.values()) {
            map.put(instance.getColumnName(), instance);
        }
        NAME_ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public String getColumnName() {return columnName;}

    public static VepTag getFromColumnName(String columnName) {
        return NAME_ENUM_MAP.get(columnName);
    }

}
