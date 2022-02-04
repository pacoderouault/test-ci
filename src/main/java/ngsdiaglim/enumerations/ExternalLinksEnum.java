package ngsdiaglim.enumerations;

public enum ExternalLinksEnum {

    IGV ("IGV"),
    GNOMAD("GnomAD"),
    CLINVAR("Clinvar"),
    DBSNP("dbSNP"),
    ENSEMBL("Ensembl"),
    COSMIC("Cosmic"),
    COSMIC_GENEVIEW("Cosmic GeneView"),
    VARSOME("Varsome"),
    LOVD("LOVD"),
    ONCOKB("OncoKB"),
    INTOGEN("intOGen"),
    THE_GENCC("The GenCC"),
    ALAMUT("Alamut");

    private final String linkName;

    ExternalLinksEnum(String linkName) {
        this.linkName = linkName;
    }

    public String getLinkName() {return linkName;}
}
