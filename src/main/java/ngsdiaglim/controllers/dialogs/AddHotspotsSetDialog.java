package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.SQLException;

public class AddHotspotsSetDialog extends DialogPane.Dialog<AddHotspotsSetDialog.AddHotspotSetData> {

    private final static Logger logger = LogManager.getLogger(AddGeneTranscriptSetDialog.class);

    private final HBox box = new HBox();
    private final GridPane gridPane = new GridPane();
    private final TextField nameTf = new TextField();
    private final Label hotspotsFileLb = new Label();
    private final Button loadFileBtn = new Button(App.getBundle().getString("addhotspotssetdialog.btn.filebtn"));
    private final Label errorLabel = new Label();
    private final Label formatLb = new Label();

    public AddHotspotsSetDialog() {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INPUT);

        setTitle(App.getBundle().getString("addhotspotssetdialog.title"));
        setContent(box);
        initView();

        setValue(new AddHotspotSetData());
        getValue().nameProperty().bind(nameTf.textProperty());
        getValue().nameProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        getValue().fileProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        setValid(false);
    }

    private void initView() {
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label nameLb = new Label(App.getBundle().getString("addhotspotssetdialog.lb.panelname"));
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(nameLb, 0, ++rowIdx);
        gridPane.add(nameTf, 1, rowIdx);
        gridPane.add(loadFileBtn, 1, ++rowIdx);
        gridPane.add(hotspotsFileLb, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);

        formatLb.setText(App.getBundle().getString("help.format.hotspots"));
        formatLb.setWrapText(false);
        formatLb.getStyleClass().add("font-mono");

        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        box.getChildren().addAll(gridPane, sep, formatLb);
        box.setSpacing(20);


        loadFileBtn.setOnAction(e -> {
            FileChooser fc = FileChooserUtils.getFileChooser();
            File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
                user.savePreferences();
                hotspotsFileLb.setText(selectedFile.getName());
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
            return App.getBundle().getString("addhotspotssetdialog.msg.err.emptyName");
        }
        else if (getValue().getFile() == null || !getValue().getFile().exists()) {
            return App.getBundle().getString("addhotspotssetdialog.msg.err.emptyFile");
        }
        else {
            try {
                if (DAOController.getGeneSetDAO().geneSetExists(getValue().getName())) {
                    return App.getBundle().getString("addhotspotssetdialog.msg.err.nameExists");
                }
            } catch (SQLException e) {
                logger.error("Error when checking if geneTranscriptSet exists", e);
                return e.getMessage();
            }
        }
        return null;
    }

    public static class AddHotspotSetData {

        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleObjectProperty<File> file = new SimpleObjectProperty<>();

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public File getFile() {
            return file.get();
        }

        public SimpleObjectProperty<File> fileProperty() {
            return file;
        }

        public void setBedFile(File file) {
            this.file.set(file);
        }
    }
}
