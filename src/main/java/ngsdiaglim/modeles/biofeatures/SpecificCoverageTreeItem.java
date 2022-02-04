package ngsdiaglim.modeles.biofeatures;

import javafx.scene.control.TreeItem;

public class SpecificCoverageTreeItem {

    private final SpecificCoverage specificCoverage;
    private final CoverageRegion coverageRegion;
    private boolean hasNoCovRegion = false;

    public SpecificCoverageTreeItem(SpecificCoverage specificCoverageRegion) {
        this.specificCoverage = specificCoverageRegion;
        this.coverageRegion = null;
    }

    public SpecificCoverageTreeItem(SpecificCoverage specificCoverageRegion, CoverageRegion coverageRegion) {
        this.specificCoverage = specificCoverageRegion;
        this.coverageRegion = coverageRegion;
    }

    public SpecificCoverage getSpecificCoverage() {return specificCoverage;}

    public CoverageRegion getCoverageRegion() {return coverageRegion;}

    public boolean hasNoCovRegion() {return hasNoCovRegion;}

    public void setNoCovRegion(boolean hasNoCovRegion) {
        this.hasNoCovRegion = hasNoCovRegion;
    }
}
