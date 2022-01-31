package ngsdiaglim.modeles.biofeatures;

public class SpecificCoverageRegion {

    private final SpecificCoverage specificCoverage;
    private CoverageRegion coverageRegion;

    public SpecificCoverageRegion(SpecificCoverage specificCoverage) {
        this.specificCoverage = specificCoverage;
    }

    public SpecificCoverage getSpecificCoverage() {return specificCoverage;}

    public CoverageRegion getCoverageRegion() {return coverageRegion;}

    public void setCoverageRegion(CoverageRegion coverageRegion) {
        this.coverageRegion = coverageRegion;
    }
}
