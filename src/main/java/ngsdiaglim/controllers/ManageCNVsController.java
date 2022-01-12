package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVControl;
import ngsdiaglim.cnv.CNVControlGroup;
import ngsdiaglim.cnv.parsers.AmpliconMatrixParser;
import ngsdiaglim.controllers.dialogs.AddCNVControlGroupDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.exceptions.FileFormatException;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.parsers.SamtoolsDepthParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class ManageCNVsController  extends Module {

    private static final Logger logger = LogManager.getLogger(UsersManageController.class);

    @FXML private TableView<CNVControlGroup> controlGroupTable;
    @FXML private TableColumn<CNVControlGroup, String> groupNameCol;
    @FXML private TableColumn<CNVControlGroup, Panel> groupPanelCol;
    @FXML private TableColumn<CNVControlGroup, TargetEnrichment> groupAlgorithmCol;
    @FXML private TableColumn<CNVControlGroup, Integer> groupControlSizeCol;
    @FXML private TableColumn<CNVControlGroup, Void> groupActionsCol;
    @FXML private TableView<CNVControl> controlsTable;
    @FXML private TableColumn<CNVControl, String> controlNameCol;
    @FXML private TableColumn<CNVControl, Gender> controlGenderCol;
    @FXML private TableColumn<CNVControl, Void> controlActionsCol;

    public ManageCNVsController() {

        super(App.getBundle().getString("cnvsmanage.title"));
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_CNVS_PARAMETERS)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
            return;
        }
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ManageCNVs.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Problem when loading the manage user panel", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
    }


    @FXML
    private void initialize() {
        initCNVGroupTable();
        initCNVControlsTable();

        fillControlGroupsTable();
    }

    private void initCNVGroupTable() {
        groupNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        groupPanelCol.setCellValueFactory(data -> data.getValue().panelProperty());
        groupAlgorithmCol.setCellValueFactory(data -> data.getValue().algorithmProperty());
        groupControlSizeCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getControlsList().size()).asObject());
        groupActionsCol.setCellFactory(data -> new GroupActionsCell());

        controlGroupTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                controlsTable.setItems(null);
            } else {
                controlsTable.setItems(newV.getControlsList());
            }
        });
    }


    private void initCNVControlsTable() {
        controlNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        controlGenderCol.setCellValueFactory(data -> data.getValue().genderProperty());
        controlGenderCol.setCellFactory(data -> new GenderTableCell());
        controlActionsCol.setCellFactory(data -> new ControlActionsCell());
    }


    private void fillControlGroupsTable() {
        try {
            controlGroupTable.setItems(DAOController.getCnvControlGroupsDAO().getCNVControlGroups());
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    @FXML
    private void addControlGroup() {
        AddCNVControlGroupDialog dialog = new AddCNVControlGroupDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {

            // check files
            if (dialog.getValue().getAlgorithm().equals(TargetEnrichment.AMPLICON)) {
                try {
                    AmpliconMatrixParser.checkFile(dialog.getValue().getPanel(), dialog.getValue().getMatrixFile());

                    // create control group dir
                    File targetDirectory = Paths.get(App.getCNVControlsDataPath().toString(), dialog.getValue().getName()).toFile();
                    if (!targetDirectory.exists()) {
                        Files.createDirectories(targetDirectory.toPath());

                    }

                    // copy matrix file
                    File destFile = new File(targetDirectory, dialog.getValue().getMatrixFile().getName());
                    FileUtils.copyFile(dialog.getValue().getMatrixFile(), destFile);


                    CNVControlGroup cnvControlGroup = DAOController.getCnvControlGroupsDAO().addGroup(
                            dialog.getValue().getPanel(),
                            dialog.getValue().getName(),
                            dialog.getValue().getAlgorithm(),
                            destFile,
                            targetDirectory
                    );
                    if (cnvControlGroup != null) {
                        for (String controlName : AmpliconMatrixParser.getSampleNames(destFile)) {
                            DAOController.getCnvControlsDAO().addCNVControl(cnvControlGroup, controlName, null, Gender.FEMALE);
                        }
                    }
                    fillControlGroupsTable();
                    Message.hideDialog(dialog);

                } catch (FileFormatException | SQLException | IOException ex) {
                    logger.error(ex);
                    Message.error(ex.getMessage(), ex);
                }
            } else if (dialog.getValue().getAlgorithm().equals(TargetEnrichment.CAPTURE)) {
                try {

                    // check depth files
                    for (File file : dialog.getValue().getDepthFiles()) {
                        if (!SamtoolsDepthParser.isDepthFile(file)) {
                            throw new FileFormatException("Invalid depth file : " + file);
                        }
                    }

                    // create control group dir
                    File targetDirectory = Paths.get(App.getCNVControlsDataPath().toString(), dialog.getValue().getName()).toFile();
                    if (!targetDirectory.exists()) {
                        Files.createDirectories(targetDirectory.toPath());
                    }

                    CNVControlGroup cnvControlGroup = DAOController.getCnvControlGroupsDAO().addGroup(
                            dialog.getValue().getPanel(),
                            dialog.getValue().getName(),
                            dialog.getValue().getAlgorithm(),
                            null,
                            targetDirectory
                    );
                    if (cnvControlGroup != null) {
                        // copy depth files
                        for (File file : dialog.getValue().getDepthFiles()) {
                            File destFile = new File(targetDirectory, file.getName());
                            FileUtils.copyFile(file, destFile);

                            String controlName = FilenameUtils.getBaseName(destFile.getPath());

                            DAOController.getCnvControlsDAO().addCNVControl(
                                    cnvControlGroup,
                                    controlName,
                                    destFile,
                                    Gender.FEMALE
                            );
                        }
                    }
                    fillControlGroupsTable();
                    Message.hideDialog(dialog);
                } catch (IOException | FileFormatException | SQLException ex) {
                    logger.error(ex);
                    Message.error(ex.getMessage(), ex);
                }
            }

        });

    }

    private static class GroupActionsCell extends TableCell<CNVControlGroup, Void> {
        private final Button deleteButton = new Button("", new FontIcon("mdal-delete_forever"));

        public GroupActionsCell() {
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
            CNVControlGroup group = getTableRow().getItem();
            if (group != null) {
                Object[] arguments = {group.getName()};
                DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("cnvsmanage.msg.confirmdeletegroup", arguments));
                dialog.getButton(ButtonType.YES).setOnAction(event -> {
                    try {
                        DAOController.getCnvControlGroupsDAO().removeCNVControlGroup(group.getId());
                        FileUtils.deleteDirectory(group.getPath());
                        getTableView().getItems().remove(group);
                        getTableView().refresh();
                        Message.hideDialog(dialog);
                    } catch (SQLException | IOException e) {
                        logger.error(e);
                        Message.error(e.getMessage(), e);
                    }
                });
            }
        }
    }


    private static class ControlActionsCell extends TableCell<CNVControl, Void> {
        private final Button deleteButton = new Button("", new FontIcon("mdal-delete_forever"));

        public ControlActionsCell() {
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
            CNVControl control = getTableRow().getItem();
            if (control != null) {
                Object[] arguments = {control.getName()};
                DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("cnvsmanage.msg.confirmdeletecontrol", arguments));
                dialog.getButton(ButtonType.YES).setOnAction(event -> {
                    try {
                        DAOController.getCnvControlsDAO().removeCNVControl(control.getId());
                        getTableView().getItems().remove(control);
                        getTableView().refresh();
                        Message.hideDialog(dialog);
                    } catch (SQLException e) {
                        logger.error(e);
                        Message.error(e.getMessage(), e);
                    }
                });
            }
        }
    }

    private static class GenderTableCell extends TableCell<CNVControl, Gender> {
        private final Button btn = new Button("XX");

        public GenderTableCell() {
            btn.getStyleClass().add("button-action-cell");
            btn.setOnAction(e -> switchGender());
        }

        @Override
        protected void updateItem(Gender item, boolean empty) {
            super.updateItem(item, empty);
            setText("");
            if (item == null || empty) {
                setGraphic(null);
            } else {
                setGraphic(btn);
                itemProperty().addListener((obs, oldV, newV) -> {
                    if (newV != null) {
                        if (newV.equals(Gender.MALE)) {
                            btn.setText("XY");
                        } else {
                            btn.setText("XX");
                        }
                    }
                });
            }
        }

        private void switchGender() {
            CNVControl control = getTableRow().getItem();
            if (control != null) {
                if (getItem().equals(Gender.MALE)) {
                    control.setGender(Gender.FEMALE);
                } else {
                    control.setGender(Gender.MALE);
                }
                try {
                    DAOController.getCnvControlsDAO().updateControl(control);
                } catch (SQLException e) {
                    logger.error(e);
                    Message.error(e.getMessage(), e);
                }
            }
        }
    }
}
