package ngsdiaglim.modeles.biofeatures;

public class SpecificCoverage extends Region {

    private long id;
    private long specificCoverageSetId;
    private final int minCov;

    public SpecificCoverage(long id, long specificCoverageSetId, String name, String contig, int start, int end, int minCov) {
        this(name, contig, start, end, minCov);
        this.id = id;
        this.specificCoverageSetId = specificCoverageSetId;
    }

    public SpecificCoverage(String name, String contig, int start, int end, int minCov) {
        super(contig, start, end, name);
        this.minCov = minCov;
    }

    public long getId() {return id;}

    public long getSpecificCoverageSetId() {return specificCoverageSetId;}

    public int getMinCov() {return minCov;}

}
