package ngsdiaglim.controllers.cells.coverageTreetableCell;

import javafx.scene.control.TreeTableCell;
import javafx.scene.paint.Color;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageTreeItem;
import org.kordamp.ikonli.javafx.FontIcon;

public class CoverageNameTreeTableCell extends TreeTableCell<SpecificCoverageTreeItem, SpecificCoverageTreeItem> {

    @Override
    protected void updateItem(SpecificCoverageTreeItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (item.getSpecificCoverage() == null) {
                setText(null);
                setGraphic(null);
            } else {
                if (item.getCoverageRegion() == null) {
                    setText(item.getSpecificCoverage().getName());
                    FontIcon icon;
                    if (item.hasNoCovRegion()) {
                        icon = new FontIcon("mdal-error");
                        icon.setIconColor(Color.RED);
                        icon.setIconSize(18);
                    } else {
                        icon = new FontIcon("mdal-check_circle");
                        icon.setIconColor(Color.GREEN);
                        icon.setIconSize(18);
                    }
                    setGraphic(icon);
                } else {
                    setText(item.getCoverageRegion().getName());
                    setGraphic(null);
                }
            }
        }
    }
}
