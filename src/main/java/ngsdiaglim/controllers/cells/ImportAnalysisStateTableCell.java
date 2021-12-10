package ngsdiaglim.controllers.cells;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import org.kordamp.ikonli.javafx.FontIcon;

public class ImportAnalysisStateTableCell extends TableCell<AnalysisInputData, AnalysisInputData.AnalysisInputState> {

    @Override
    protected void updateItem(AnalysisInputData.AnalysisInputState item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        setGraphic(null);
        getStyleClass().remove("error-tablerow");

        if(!empty && item != null) {
            if (item != AnalysisInputData.AnalysisInputState.VALID) {
                FontIcon errorIcon = new FontIcon("mdomz-warning");
                errorIcon.setIconSize(24);
                errorIcon.setIconColor(Color.RED);
                Tooltip errTp = new Tooltip(item.getMessage());
                errTp.setShowDelay(Duration.ZERO);
                Tooltip.install(errorIcon, errTp);
                setGraphic(errorIcon);
                errTp.setStyle("-fx-font-size: 13px !important;");
                getStyleClass().add("error-tablerow");
            } else {
                setGraphic(null);
            }
        }
    }
}
