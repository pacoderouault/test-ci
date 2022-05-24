package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.specificcoverageCells.SpecificCoverageSetActionsCell;
import ngsdiaglim.controllers.dialogs.AddSpecificCoverageDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageSet;
import ngsdiaglim.modeles.parsers.HotspotsParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.HotspotsSet;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AnalysisParametersSpecificCoverageController extends HBox {

    private final static Logger logger = LogManager.getLogger(AnalysisParametersSpecificCoverageController.class);

    @FXML private TableView<SpecificCoverageSet> specificCoverageSetTable;
    @FXML private TableColumn<SpecificCoverageSet, String> specCovNameCol;
    @FXML private TableColumn<SpecificCoverageSet, Integer> specCovNbCol;
    @FXML private TableColumn<SpecificCoverageSet, Boolean> specCovActiveCol;
    @FXML private TableColumn<SpecificCoverageSet, Void> specCovDeleteCol;
    @FXML private TableColumn<SpecificCoverageSet, Void> specCovActionsCol;
    @FXML private TableView<SpecificCoverage> regionsTable;
    @FXML private TableColumn<SpecificCoverage, String> regionNameCol;
    @FXML private TableColumn<SpecificCoverage, String> regionContigCol;
    @FXML private TableColumn<SpecificCoverage, Integer> regionStartCol;
    @FXML private TableColumn<SpecificCoverage, Integer> regionEndCol;
    @FXML private TableColumn<SpecificCoverage, Integer> regionMinCovCol;

    public AnalysisParametersSpecificCoverageController() {
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisParametersSpecificCoverage.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();

        try {
            loadSpecificCoverageSets();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage(), e);
        }
    }

    private void initView() {
        initSpecificCoverageTable();
        initRegionsTable();
    }

    private void initRegionsTable() {
        regionNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        regionContigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        regionStartCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        regionEndCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        regionMinCovCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMinCov()).asObject());
    }

    private void initSpecificCoverageTable() {
        specCovNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        specCovNbCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSpecificCoverageList().size()).asObject());
        specCovActiveCol.setCellValueFactory(data -> data.getValue().activeProperty());
        specCovActiveCol.setCellFactory(data -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<SpecificCoverageSet, Boolean> tableCell = new TableCell<>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (empty || item == null)
                        setGraphic(null);
                    else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                        User user = App.get().getLoggedUser();
                        checkBox.setDisable(!user.isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
                    }
                }
            };
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validateSpecificCoverageSetActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
        specCovActionsCol.setCellFactory(data -> new SpecificCoverageSetActionsCell());

        specificCoverageSetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillRegionsTable(newV));
    }

    private void fillRegionsTable(SpecificCoverageSet specificCoverageSet) {
        if (specificCoverageSet == null) {
            regionsTable.getItems().clear();
        } else {
            regionsTable.getItems().setAll(specificCoverageSet.getSpecificCoverageList());
        }
    }


    private void validateSpecificCoverageSetActivation(CheckBox checkBox, SpecificCoverageSet item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getName()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.module.specificcov.msg.confirm.inactiveCoverageSet", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.module.specificcov.msg.confirm.reactiveCoverageSet", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.getSpecificCoverageSetDAO().activeSpecificCoverageSet(item.getId(), !item.isActive());
                item.setActive(!checkBox.isSelected());
                checkBox.setSelected(!checkBox.isSelected());
                loadSpecificCoverageSets();
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                Message.error(ex.getMessage(), ex);
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }


    private void loadSpecificCoverageSets() throws SQLException {
        specificCoverageSetTable.getItems().setAll(DAOController.getSpecificCoverageSetDAO().getSpecificCoverageSets());
    }

    @FXML
    private void addNewSpecificCoverageSet() {
        DialogPane.Dialog<AddSpecificCoverageDialog.SpecificCoverageCreationData> dialog = new AddSpecificCoverageDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (dialog.isValid() && dialog.getValue() != null) {
                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addGeneTranscriptSetDialog.msg.loading"));
                wid.addTaskEndNotification(r -> {
                    if (r == 0) {
                        try {
                            loadSpecificCoverageSets();
                        } catch (SQLException ex) {
                            logger.error(ex.getMessage(), ex);
                            Message.error(ex.getMessage(), ex);
                        }
                        Message.hideDialog(dialog);
                    }
                });
                wid.exec("addHotspots", inputParams -> {
                    String specificCoverageSetName = dialog.getValue().getName();
                    List<SpecificCoverage> regions = dialog.getValue().getSpecificCoverages();
                    long id = -1;
                    try {
                        id = DAOController.getSpecificCoverageSetDAO().addSpecificCoverageSet(specificCoverageSetName);
                        for (SpecificCoverage region : regions) {
                            DAOController.getSpecificCoverageDAO().addSpecificCoverage(id, region, region.getMinCov());
                        }
                    } catch (Exception  ex) {
                        logger.error(ex.getMessage(), ex);
                        Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                        if (id >= 0) {
                            try {
                                DAOController.getSpecificCoverageSetDAO().deleteSpecificCoverageSet(id);
                            } catch (SQLException exc) {
                                logger.error(exc);
                            }
                        }
                        return 1;
                    }

                    return 0;
                });
            }
        });
    }

    public TableView<SpecificCoverageSet> getSpecificCoverageSetTable() {return specificCoverageSetTable;}
}
