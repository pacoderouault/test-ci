package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.FileTableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AddCNVControlGroupDialog  extends DialogPane.Dialog<AddCNVControlGroupDialog.CNVControlGroupData> {
    private final Logger logger = LogManager.getLogger(AddVariantCommentaryDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TextField nameTf;
    @FXML private TextField matrixTf;
    @FXML private ComboBox<Panel> panelsCb;
    @FXML private ComboBox<TargetEnrichment> algorithmCb;
    @FXML private TableView<File> depthFilesTable;
    @FXML private TableColumn<File, File> depthFileNameCol;
    @FXML private TableColumn<File, Void> depthFileActionCol;
    @FXML private Label errorLabel;
    @FXML private Label matrixLb;
    @FXML private Label depthFilesLb;
    @FXML private Button matrixButton;
    @FXML private Button depthFilesBtn;

    private final SimpleObjectProperty<File> matrixFile = new SimpleObjectProperty<>();

    public AddCNVControlGroupDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AddCNVControlGroup.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("addcnvcontrolgroup.title"));
        setContent(dialogContainer);
        setValue(new CNVControlGroupData());
        setValid(false);

        nameTf.textProperty().addListener((obs,oldV, newV) -> validGroup());
        panelsCb.valueProperty().addListener((obs,oldV, newV) -> validGroup());
        algorithmCb.valueProperty().addListener((obs,oldV, newV) -> validGroup());
        matrixFile.addListener((obs,oldV, newV) -> validGroup());
        depthFilesTable.getItems().addListener((ListChangeListener<File>) change -> validGroup());

        initView();
    }

    private void initView() {
        nameTf.textProperty().bindBidirectional(getValue().nameProperty());
        panelsCb.valueProperty().bindBidirectional(getValue().panelProperty());
        algorithmCb.valueProperty().bindBidirectional(getValue().algorithmProperty());
        matrixFile.bindBidirectional(getValue().matrixFileProperty());
        matrixFile.addListener((obs, oldV, newV) -> {
            if (newV == null) {
                matrixTf.setText(null);
            } else {
                matrixTf.setText(matrixFile.get().getPath());
            }
        });
        depthFilesTable.setItems(getValue().getDepthFiles());

        try {
            panelsCb.setItems(DAOController.getPanelDAO().getPanels());
            algorithmCb.getItems().setAll(TargetEnrichment.values());
        } catch (SQLException e) {
            logger.error(e.getMessage());
            Message.error(e.getMessage(), e);
        }

        algorithmCb.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV.equals(TargetEnrichment.CAPTURE)) {
                showCaptureUI();
            } else {
                showAmpliconUI();
            }
        });
        algorithmCb.getSelectionModel().select(TargetEnrichment.CAPTURE);

        depthFileNameCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        depthFileNameCol.setCellFactory(data -> new FileTableCell<>(false));
        depthFileActionCol.setCellFactory(data -> new DepthFileActionCell());
    }


    @FXML
    private void loadMatrixFile() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            matrixFile.set(selectedFile);
        }
    }


    @FXML
    private void loadDepthFiles() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        List<File> selectedFiles = fc.showOpenMultipleDialog(App.getPrimaryStage());
        if (!selectedFiles.isEmpty()) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFiles.get(0)));
            user.savePreferences();
            depthFilesTable.getItems().addAll(selectedFiles);
        }
    }

    private void validGroup() {
        String error = checkError();
        if (error != null) {
            errorLabel.setText(error);
            setValid(false);
        } else {
            errorLabel.setText(null);
            setValid(true);
        }
    }

    private void showCaptureUI() {
        matrixTf.setDisable(true);
        matrixButton.setDisable(true);
        depthFilesTable.setDisable(false);
        depthFilesBtn.setDisable(false);
    }

    private void showAmpliconUI() {
        matrixTf.setDisable(false);
        matrixButton.setDisable(false);
        depthFilesTable.setDisable(true);
        depthFilesBtn.setDisable(true);
    }

    private String checkError() {
        if (StringUtils.isBlank(getValue().getName())) {
            return App.getBundle().getString("addcnvcontrolgroup.msg.err.emptyname");
        } else {
            try {
                if (DAOController.getCnvControlGroupsDAO().exists(getValue().getName())) {
                            return App.getBundle().getString("addcnvcontrolgroup.msg.err.nameexists");
                } else if (getValue().getPanel() == null) {
                    return App.getBundle().getString("addcnvcontrolgroup.msg.err.emptypanel");
                } else if (getValue().getAlgorithm() == null) {
                    return App.getBundle().getString("addcnvcontrolgroup.msg.err.emptyalgorithm");
                } else if (getValue().getAlgorithm().equals(TargetEnrichment.AMPLICON) && getValue().getMatrixFile() == null) {
                    return App.getBundle().getString("addcnvcontrolgroup.msg.err.emptymatrixfile");
                } else if (getValue().getAlgorithm().equals(TargetEnrichment.CAPTURE) && getValue().getDepthFiles().isEmpty()) {
                    return App.getBundle().getString("addcnvcontrolgroup.msg.err.emptydepthfiles");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class CNVControlGroupData {

        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleObjectProperty<Panel> panel = new SimpleObjectProperty<>();
        private final SimpleObjectProperty<TargetEnrichment> algorithm = new SimpleObjectProperty<>();
        private final SimpleObjectProperty<File> matrixFile = new SimpleObjectProperty<>();
        private final ObservableList<File> depthFiles = FXCollections.observableArrayList();

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public Panel getPanel() {
            return panel.get();
        }

        public SimpleObjectProperty<Panel> panelProperty() {
            return panel;
        }

        public void setPanel(Panel panel) {
            this.panel.set(panel);
        }

        public TargetEnrichment getAlgorithm() {
            return algorithm.get();
        }

        public SimpleObjectProperty<TargetEnrichment> algorithmProperty() {
            return algorithm;
        }

        public void setAlgorithm(TargetEnrichment algorithm) {
            this.algorithm.set(algorithm);
        }

        public File getMatrixFile() {
            return matrixFile.get();
        }

        public SimpleObjectProperty<File> matrixFileProperty() {
            return matrixFile;
        }

        public void setMatrixFile(File matrixFile) {
            this.matrixFile.set(matrixFile);
        }

        public ObservableList<File> getDepthFiles() {return depthFiles;}
    }


    private static class DepthFileActionCell extends TableCell<File, Void> {
        private final Button deleteButton = new Button("", new FontIcon("mdal-delete_forever"));

        public DepthFileActionCell() {
            deleteButton.getStyleClass().add("button-action-cell");
            deleteButton.setOnAction(e -> deleteGroup());
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (!empty) {
                setGraphic(deleteButton);
            } else {
                setGraphic(null);
            }
        }

        private void deleteGroup() {
            File file = getTableRow().getItem();
            if (file != null) {
                getTableView().getItems().remove(file);
            }
        }
    }
}
