package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.modeles.analyse.Analysis;

public class ChangeAnalysisStateDialog extends DialogPane.Dialog<AnalysisStatus> {

    private final GridPane gridPane = new GridPane();
    private final ComboBox<AnalysisStatus> statusCb = new ComboBox<>();
    private final Label errorLabel = new Label();
    private final Analysis analysis;

    public ChangeAnalysisStateDialog(Analysis analysis) {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INPUT);
        this.analysis = analysis;

        setTitle(App.getBundle().getString("changeanalysisstatusdialog.title"));
        setContent(gridPane);
        initView();
        valueProperty().addListener(l -> changeFormEvent());
        setValid(false);
    }


    private void changeFormEvent() {
        errorLabel.setText("");
        String error = checkErrorForm();
        if (error != null) {
            errorLabel.setText(error);
            setValid(false);
        }
        else {
            setValid(true);
        }
    }


    private String checkErrorForm() {
        if (getValue() == null ) {
            return App.getBundle().getString("changeanalysisstatusdialog.err.valueEmpty");
        }
        return null;
    }


    private void initView() {
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label runNameLb = new Label(App.getBundle().getString("changeanalysisstatusdialog.lb.status"));
        errorLabel.getStyleClass().add("error-label");

        statusCb.getItems().setAll(AnalysisStatus.values());
        valueProperty().bindBidirectional(statusCb.valueProperty());
        statusCb.getSelectionModel().select(analysis.getStatus());

        int rowIdx = 0;
        gridPane.add(runNameLb, 0, ++rowIdx);
        gridPane.add(statusCb, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);
    }

}
