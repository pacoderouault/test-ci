package ngsdiaglim.controllers.cells;

import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import org.kordamp.ikonli.javafx.FontIcon;

public class ImportAnalysisNameTableCell extends TextFieldTableCell<AnalysisInputData,String> {


    public ImportAnalysisNameTableCell() {
        super(new DefaultStringConverter());
    }

    @Override
    public void updateItem(String item, boolean empty) {
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
            getStyleClass().remove("error-tablerow");
        }
        else {
            AnalysisInputData analysisInputData = getTableRow().getItem();
            if (analysisInputData != null) {
                setText(analysisInputData.getAnalysisName());
                if (analysisInputData.getState() != AnalysisInputData.AnalysisInputState.VALID) {
                    FontIcon errorIcon = new FontIcon("mdal-error");
                    Tooltip errTp = new Tooltip(analysisInputData.getState().getMessage());
                    Tooltip.install(errorIcon, errTp);
                    setGraphic(errorIcon);
                    getStyleClass().add("error-tablerow");
                }
                else {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().remove("error-tablerow");
                }
            }
            else {
                setText(null);
                setGraphic(null);
                getStyleClass().remove("error-tablerow");
            }
        }
    }
}
