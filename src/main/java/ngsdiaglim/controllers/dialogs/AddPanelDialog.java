package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.utils.FileChooserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.SQLException;

public class AddPanelDialog extends DialogPane.Dialog<AddPanelDialog.PanelCreationData> {

    private final Logger logger = LogManager.getLogger(AddPanelDialog.class);

    private final GridPane gridPane = new GridPane();
    private final TextField panelNameTf = new TextField();
    private final Label bedFileLb = new Label();
    private final Button loadBedBtn = new Button(App.getBundle().getString("addpaneldialog.btn.bedfilebtn"));
    private final Label errorLabel = new Label();


    public AddPanelDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);

        setTitle(App.getBundle().getString("addpaneldialog.title"));
        setContent(gridPane);
        initView();

        setValue(new PanelCreationData());
        getValue().nameProperty().bind(panelNameTf.textProperty());
        getValue().nameProperty().addListener((obs, oldV, newV) -> {
            changeFormEvent();
        });
        getValue().bedFileProperty().addListener((obs, oldV, newV) -> {
            changeFormEvent();
        });
        setValid(false);
    }

    private void initView() {
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label panelNameLb = new Label(App.getBundle().getString("addpaneldialog.lb.panelname"));
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(panelNameLb, 0, ++rowIdx);
        gridPane.add(panelNameTf, 1, rowIdx);
        gridPane.add(loadBedBtn, 0, ++rowIdx);
        gridPane.add(bedFileLb, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);

//        panelNaleTf.textProperty().addListener((obs, oldV, newV) -> {
//            getValue().setName(newV);
//        });
//        panelNaleTf.textProperty().bind(getV);

        loadBedBtn.setOnAction(e -> {
            FileChooser fc = FileChooserUtils.getFileChooser();
            File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                bedFileLb.setText(selectedFile.getName());
                getValue().setBedFile(selectedFile);
            }
        });
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
        if (getValue().getName() == null || StringUtils.isBlank(getValue().getName())) {
            return App.getBundle().getString("addpaneldialog.msg.err.emptyName");
        }
        else if (getValue().getBedFile() == null || !getValue().getBedFile().exists()) {
            return App.getBundle().getString("addpaneldialog.msg.err.emptyFile");
        }
        else {
            try {
                if (DAOController.getPanelDAO().panelExists(getValue().getName())) {
                    return App.getBundle().getString("addpaneldialog.msg.err.nameExists");
                }
            } catch (SQLException e) {
                logger.error("Error when checking if panel exists", e);
                return e.getMessage();
            }
        }
        return null;
    }

    public static class PanelCreationData {
        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleObjectProperty<File> bedFile = new SimpleObjectProperty<>();

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public File getBedFile() {
            return bedFile.get();
        }

        public SimpleObjectProperty<File> bedFileProperty() {
            return bedFile;
        }

        public void setBedFile(File bedFile) {
            this.bedFile.set(bedFile);
        }
    }
}



