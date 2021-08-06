package ngsdiaglim.enumerations;

public enum Genome {

    GRCh37("GrCH37/hg19"),
    GRCh38("GrCH38/hg38");

    private final String name;

    Genome(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    public String toString() {
        return name;
    }
}
