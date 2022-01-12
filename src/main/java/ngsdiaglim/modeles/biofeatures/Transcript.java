package ngsdiaglim.modeles.biofeatures;

public class Transcript {

    private long id;
    private long geneId;
    private final String name;
    private final Gene gene;
    private boolean isPreferred;

    public Transcript(String name) {
        this.name = name;
        this.gene = null;
        this.isPreferred = false;
    }

    public Transcript(long id, long geneId, String name, Gene gene) {
        this.name = name;
        this.id = id;
        this.geneId = geneId;
        this.gene = gene;
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public long getGeneId() {return geneId;}

    public String getName() {return name;}

    public String getNameWithoutVersion() {
        return getNameWithoutVersion(name);
    }

    public static String getNameWithoutVersion(String transcriptName) {
        return transcriptName.split("\\.")[0];
    }

    public String getVersion() {
        String[] tokens = name.split("\\.");
        if (tokens.length > 1) {
            return tokens[tokens.length - 1];
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transcript that = (Transcript) o;

        return getNameWithoutVersion().equals(that.getNameWithoutVersion());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public Gene getGene() {return gene;}

    public boolean isPreferred() {return isPreferred;}

    public void setPreferred(boolean preferred) {
        isPreferred = preferred;
    }
}
