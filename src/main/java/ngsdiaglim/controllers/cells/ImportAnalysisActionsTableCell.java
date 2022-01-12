package ngsdiaglim.controllers.cells;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import org.kordamp.ikonli.javafx.FontIcon;

public class ImportAnalysisActionsTableCell extends TableCell<AnalysisInputData, Void> {
    private final HBox box = new HBox();
    private final static Tooltip deleteTp = new Tooltip(App.getBundle().getString("importanalysesdialog.tp.deleteAnalysis"));

    public ImportAnalysisActionsTableCell() {
        box.getStyleClass().add("box-action-cell");
        deleteTp.setShowDelay(Duration.ZERO);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if(empty) {
            setGraphic(null);
        }
        else {
            AnalysisInputData analysisInputData = getTableRow().getItem();
            if (analysisInputData != null) {
                Button deleteBtn = new Button("", new FontIcon("mdal-delete_forever"));
                deleteBtn.setOnAction(e -> getTableView().getItems().remove(analysisInputData));
                Tooltip.install(deleteBtn, deleteTp);
                box.getChildren().setAll(deleteBtn);

                setGraphic(box);
            }
            else {
                setGraphic(null);
            }
        }
    }
}
