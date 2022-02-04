package ngsdiaglim.modeles.biofeatures;

import java.util.ArrayList;
import java.util.List;

public class SpecificCoverageRegion {

    private final SpecificCoverage specificCoverage;
    private List<CoverageRegion> coverageRegions;

    public SpecificCoverageRegion(SpecificCoverage specificCoverage) {
        this.specificCoverage = specificCoverage;
    }

    public SpecificCoverage getSpecificCoverage() {return specificCoverage;}

    public List<CoverageRegion> getCoverageRegions() {return coverageRegions;}

    public void addCoverageRegions(CoverageRegion coverageRegion) {
        if (this.coverageRegions == null) {
            this.coverageRegions = new ArrayList<>();
        }
        this.coverageRegions.add(coverageRegion);
    }
}
