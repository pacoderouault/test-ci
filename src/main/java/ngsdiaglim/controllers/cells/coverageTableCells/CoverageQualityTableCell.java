package ngsdiaglim.controllers.cells.coverageTableCells;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import org.kordamp.ikonli.javafx.FontIcon;

public class CoverageQualityTableCell extends TableCell<CoverageRegion, CoverageQuality> {

    @Override
    protected void updateItem(CoverageQuality coverageQuality, boolean empty) {
        super.updateItem(coverageQuality, empty);
        if (coverageQuality != null && !empty) {
            FontIcon icon;
            if (coverageQuality.equals(CoverageQuality.NO_COVERED)) {
                icon = new FontIcon("mdal-error");
                icon.setIconColor(Color.RED);
                setText(App.getBundle().getString("coverage.lb.nocoveredregion"));
            } else {
                icon = new FontIcon("mdmz-warning");
                icon.setIconColor(Color.ORANGE);
                setText(App.getBundle().getString("coverage.lb.lowcoveredregion"));
            }
            icon.setIconSize(18);
            setGraphic(icon);
        } else {
            setText(null);
            setGraphic(null);
        }
    }

}
