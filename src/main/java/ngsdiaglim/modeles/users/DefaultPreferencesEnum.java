package ngsdiaglim.modeles.users;

import ngsdiaglim.enumerations.CNVChartHeight;

public enum DefaultPreferencesEnum {

    FULL_SCREEN("false"),
    INITIAL_DIR("."),
    SELECT_TRANSCRIPT_FOR_ALL_VARIANTS("false"),
    HIDE_INTRONIC_VARIANT("false"),
    HIDE_SYNONYMOUS_VARIANT("false"),
    HIDE_UTR_VARIANT("false"),
    HIDE_INTERGENIC_VARIANT("false"),
    HIDE_NON_CODING_VARIANT("false"),
    HIDE_FALSE_POSITIVE_VARIANT("false"),
    COLOR_UNIQUE_VARIANTS("true"),
    USE_SMOOTH_SCROLLING("true"),
    VARIANT_TABLE_THEME("THEME2"),
//    IGV_PATH("D:\\syschu\\app_exe_xen\\IGV_Xen.exe")
    IGV_PATH("/home/paco/Téléchargements/IGV_Linux_2.11.1_WithJava/IGV_Linux_2.11.1/igv.sh"),
    CNV_AUTO_DETECTION("False"),
    CNV_MIN_AMPLICONS("3"),
    CNV_DEL_THRESHOLD("0.7"),
    CNV_DUP_THRESHOLD("1.3"),
    CNV_NUMBER_SAMPLE_PER_PAGE("3"),
    CNV_SHOW_LOESS("True"),
    CNV_SHOW_GENE_AVERAGE("False"),
    CNV_SHOW_CUSUM("True"),
    CNV_CHART_HEIGHT(CNVChartHeight.MEDIUM.name()),
    VARIANT_EXPORT_FALSE_POSITIVE("False"),
    VARIANT_EXPORT_SYNONYMOUS("False"),
    VARIANT_EXPORT_NON_CODING("False"),
    VARIANT_EXPORT_VAF("True"),
    VARIANT_EXPORT_VAF_MIN("0.1"),
    VARIANT_EXPORT_DEPTH("True"),
    VARIANT_EXPORT_DEPTH_MIN("50"),
    VARIANT_EXPORT_OCC("True"),
    VARIANT_EXPORT_OCC_MAX("5"),
    VARIANT_EXPORT_PATHOGENICITY("True"),
    VARIANT_EXPORT_PATHOGENICITY_MIN("4"),
    VARIANT_EXPORT_GNOMAD("True"),
    VARIANT_EXPORT_GNOMAD_MAX("0.01")
    ;

    private final String value;

    DefaultPreferencesEnum(String value) {
        this.value = value;
    }

    public String getValue() { return value; }
}
