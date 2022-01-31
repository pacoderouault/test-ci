package ngsdiaglim.controllers.cells.coverageTreetableCell;

import javafx.scene.control.TreeTableCell;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;

public class CoverageTargetDepthTreeTableCell extends TreeTableCell<SpecificCoverageRegion, SpecificCoverageRegion> {

    @Override
    protected void updateItem(SpecificCoverageRegion item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(String.valueOf(item.getSpecificCoverage().getMinCov()));
        }
    }
}