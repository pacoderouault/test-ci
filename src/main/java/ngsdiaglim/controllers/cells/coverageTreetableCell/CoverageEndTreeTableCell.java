package ngsdiaglim.controllers.cells.coverageTreetableCell;

import javafx.scene.control.TreeTableCell;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;

public class CoverageEndTreeTableCell extends TreeTableCell<SpecificCoverageRegion, SpecificCoverageRegion> {

    @Override
    protected void updateItem(SpecificCoverageRegion item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (item.getCoverageRegion() == null) {
                setText(String.valueOf(item.getSpecificCoverage().getEnd()));
            } else {
                setText(String.valueOf(item.getCoverageRegion().getEnd()));
            }
        }
    }
}