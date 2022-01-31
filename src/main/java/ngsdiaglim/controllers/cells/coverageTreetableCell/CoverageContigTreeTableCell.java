package ngsdiaglim.controllers.cells.coverageTreetableCell;

import javafx.scene.control.TreeTableCell;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;

public class CoverageContigTreeTableCell extends TreeTableCell<SpecificCoverageRegion, SpecificCoverageRegion> {

    @Override
    protected void updateItem(SpecificCoverageRegion item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (item.getCoverageRegion() == null) {
                setText(item.getSpecificCoverage().getContig());
            } else {
                setText(item.getSpecificCoverage().getContig());
            }
        }
    }
}