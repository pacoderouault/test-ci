package ngsdiaglim.enumerations;

public enum GnomadPopulation {

    GLOBAL("Gnomad global"),
    MAX_POP("Gnomad max pop "),
    AFR("African/African American"),
    AMR("American Admixed/Latino"),
    ASJ("Ashkenazi Jewish"),
    EAS("East Asian"),
    FIN("Finnish"),
    NFE("Non-Finnish European"),
    SAS("South Asian");

    public final String population;

    GnomadPopulation(String population) {
        this.population = population;
    }

    public String getPopulation() {return population;}
}