package ngsdiaglim.controllers.cells;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.modeles.analyse.Analysis;

public class AnalysisStateTableCell extends TableCell<Analysis, AnalysisStatus> {
    private final Label label = new Label();

    public AnalysisStateTableCell() {
        label.getStyleClass().add("analysis-state-label");
    }

    @Override
    protected void updateItem(AnalysisStatus item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (item == null) {
            setGraphic(null);
        } else {
            label.getStyleClass().removeAll(    "analysis-state-label-complete", "analysis-state-label-inprogress");
            if (item.equals(AnalysisStatus.DONE)) {
                label.setText(App.getBundle().getString("home.module.analyseslist.table.stateComplete"));
                label.getStyleClass().add("analysis-state-label-complete");
            }
            else {
                label.setText(App.getBundle().getString("home.module.analyseslist.table.stateInProgress"));
                label.getStyleClass().add("analysis-state-label-inprogress");
            }
            setGraphic(label);
        }
    }
}
