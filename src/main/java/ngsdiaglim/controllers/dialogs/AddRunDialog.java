package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddRunDialog extends DialogPane.Dialog<AddRunDialog.RunCreattionData> {

    private final Logger logger = LogManager.getLogger(AddRunDialog.class);

    private final GridPane gridPane = new GridPane();
    private final TextField runNameTf = new TextField();
    private final Label errorLabel = new Label();
    private final DatePicker runDateDp = new DatePicker(LocalDate.now());

    public AddRunDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);

        setTitle(App.getBundle().getString("addrundialog.title"));
        setContent(gridPane);
        initView();

        setValue(new RunCreattionData());
        getValue().runNameProperty().bind(runNameTf.textProperty());
        getValue().runDateProperty().bind(runDateDp.valueProperty());
        getValue().runNameProperty().addListener((obs, oldV, newV) -> {
            changeFormEvent();
        });
        getValue().runDateProperty().addListener((obs, oldV, newV) -> {
            changeFormEvent();
        });
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
        if (getValue().getRunName() == null || StringUtils.isBlank(getValue().getRunName())) {
            return App.getBundle().getString("addrundialog.msg.err.emptyRunName");
        }
        else if (getValue().getRunDate() == null) {
            return App.getBundle().getString("addrundialog.msg.err.emptyRunDate");
        }
        else {
            try {
                if (DAOController.getRunsDAO().runExists(getValue().getRunName())) {
                    return App.getBundle().getString("addrundialog.msg.err.runNameExists");
                }
            } catch (SQLException e) {
                logger.error("Error when checking if panel exists", e);
                return e.getMessage();
            }
        }
        return null;
    }




    private void initView() {
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label runNameLb = new Label(App.getBundle().getString("addrundialog.lb.runname"));
        Label runDateLb = new Label(App.getBundle().getString("addrundialog.lb.rundate"));
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(runNameLb, 0, ++rowIdx);
        gridPane.add(runNameTf, 1, rowIdx);
        gridPane.add(runDateLb, 0, ++rowIdx);
        gridPane.add(runDateDp, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);
    }

    public static class RunCreattionData {
        private final SimpleStringProperty runName = new SimpleStringProperty();
        private final SimpleObjectProperty<LocalDate> runDate = new SimpleObjectProperty<>();

        public String getRunName() {
            return runName.get();
        }

        public SimpleStringProperty runNameProperty() {
            return runName;
        }

        public void setRunName(String runName) {
            this.runName.set(runName);
        }

        public LocalDate getRunDate() {
            return runDate.get();
        }

        public SimpleObjectProperty<LocalDate> runDateProperty() {
            return runDate;
        }

        public void setRunDate(LocalDate runDate) {
            this.runDate.set(runDate);
        }
    }
}
