package ngsdiaglim.controllers.cells.coverageTreetableCell;

import javafx.scene.control.TreeTableCell;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageTreeItem;

public class CoverageEndTreeTableCell extends TreeTableCell<SpecificCoverageTreeItem, SpecificCoverageTreeItem> {

    @Override
    protected void updateItem(SpecificCoverageTreeItem item, boolean empty) {
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