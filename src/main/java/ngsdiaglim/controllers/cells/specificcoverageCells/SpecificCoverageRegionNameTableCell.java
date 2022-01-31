package ngsdiaglim.controllers.cells.specificcoverageCells;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;
import org.kordamp.ikonli.javafx.FontIcon;

public class SpecificCoverageRegionNameTableCell extends TableCell<SpecificCoverageRegion, String> {

    @Override
    protected void updateItem(String name, boolean empty) {
        super.updateItem(name, empty);
        if (name == null  || empty) {
            setText(null);
            setGraphic(null);
        } else {
            FontIcon icon = new FontIcon("mdal-error");
            icon.setIconColor(Color.RED);
            icon.setIconSize(18);
            setGraphic(icon);
            setText(name);
        }
    }
}
