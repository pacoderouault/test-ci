package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.AnalysisParametersHotspotDeleteCell;
import ngsdiaglim.controllers.dialogs.AddHotspotsSetDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.HotspotType;
import ngsdiaglim.modeles.parsers.HotspotsParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.HotspotsSet;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AnalysisParametersHotspotsController extends HBox {

    private static final Logger logger = LogManager.getLogger(AnalysisParametersHotspotsController.class);

    @FXML private TableView<HotspotsSet> hotspotsSetTable;
    @FXML private TableColumn<HotspotsSet, String> hotspotsSetNameCol;
    @FXML private TableColumn<HotspotsSet, Integer> hotspotsSetCountCol;
    @FXML private TableColumn<HotspotsSet, Boolean> hotspotsSetActiveCol;
    @FXML private TableColumn<HotspotsSet, Void> hotspotsSetDeleteCol;

    @FXML private TableView<Hotspot> hotspotsTable;
    @FXML private TableColumn<Hotspot, String> hotspotsIdCol;
    @FXML private TableColumn<Hotspot, String> hotspotsContigCol;
    @FXML private TableColumn<Hotspot, Integer> hotspotsStartCol;
    @FXML private TableColumn<Hotspot, Integer> hotspotsEndCol;
    @FXML private TableColumn<Hotspot, String> hotspotsRefCol;
    @FXML private TableColumn<Hotspot, String> hotspotsAltCol;
    @FXML private TableColumn<Hotspot, String> hotspotsGeneCol;
    @FXML private TableColumn<Hotspot, String> hotspotsCodingMutCol;
    @FXML private TableColumn<Hotspot, String> hotspotsProteinMutCol;
    @FXML private TableColumn<Hotspot, HotspotType> hotspotsTypeMutCol;

    public AnalysisParametersHotspotsController(CreateAnalysisParametersController createAnalysisParametersController) {
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisParametersHotspots.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();
    }
    
    
    private void initView() {
        initHotspotsSetTable();
        initHotspotsTable();

        try {
            loadHotspotsSets();
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    private void initHotspotsSetTable() {

        hotspotsSetNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        hotspotsSetCountCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getHotspots().size()).asObject());
        hotspotsSetActiveCol.setCellValueFactory(data -> data.getValue().activeProperty());
        hotspotsSetActiveCol.setCellFactory(data -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<HotspotsSet, Boolean> tableCell = new TableCell<>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (empty || item == null)
                        setGraphic(null);
                    else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                    }
                }
            };
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validateHostpotsSetActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
        hotspotsSetDeleteCol.setCellFactory(c -> new AnalysisParametersHotspotDeleteCell());
    }


    private void initHotspotsTable() {

        hotspotsIdCol.setCellValueFactory(data -> data.getValue().hotspotIdProperty());
        hotspotsContigCol.setCellValueFactory(data -> data.getValue().contigProperty());
        hotspotsStartCol.setCellValueFactory(data -> data.getValue().startProperty().asObject());
        hotspotsEndCol.setCellValueFactory(data -> data.getValue().endProperty().asObject());
        hotspotsRefCol.setCellValueFactory(data -> data.getValue().refProperty());
        hotspotsAltCol.setCellValueFactory(data -> data.getValue().altProperty());
        hotspotsGeneCol.setCellValueFactory(data -> data.getValue().geneProperty());
        hotspotsCodingMutCol.setCellValueFactory(data -> data.getValue().codingMutProperty());
        hotspotsProteinMutCol.setCellValueFactory(data -> data.getValue().proteinMutProperty());
        hotspotsTypeMutCol.setCellValueFactory(data -> data.getValue().typeProperty());
        hotspotsTypeMutCol.setCellFactory(data -> new TableCell<>() {
            @Override
            protected void updateItem(HotspotType item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });
        hotspotsSetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                hotspotsTable.getItems().clear();
            } else {
                hotspotsTable.getItems().setAll(newV.getHotspots());
            }
        });
    }

    private void validateHostpotsSetActivation(CheckBox checkBox, HotspotsSet item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getName()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.module.hotspots.msg.confirm.inactiveHotspotSet", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.module.hotspots.msg.confirm.reactiveHotspotSet", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.getHotspotsSetDAO().updateHotspotsSet(item, item.getName(), !item.isActive());
                item.setActive(!checkBox.isSelected());
                checkBox.setSelected(!checkBox.isSelected());
                loadHotspotsSets();
            } catch (SQLException ex) {
                logger.error(ex);
                Message.error(ex.getMessage(), ex);
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }

    private void loadHotspotsSets() throws SQLException {
        hotspotsSetTable.getItems().setAll(DAOController.getHotspotsSetDAO().getHotspotsSets());
    }

    @FXML
    private void addHotspotsSetHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
            AddHotspotsSetDialog addHotspotsSetDialog = new AddHotspotsSetDialog();
            Message.showDialog(addHotspotsSetDialog);
            Button b = addHotspotsSetDialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (addHotspotsSetDialog.isValid() && addHotspotsSetDialog.getValue() != null) {
                    WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addGeneTranscriptSetDialog.msg.loading"));
                    wid.addTaskEndNotification(r -> {
                        if (r == 0) {
                            try {
                                loadHotspotsSets();
                            } catch (SQLException ex) {
                                logger.error(ex);
                                Message.error(ex.getMessage(), ex);
                            }
                            Message.hideDialog(addHotspotsSetDialog);
                        }
                    });
                    wid.exec("addHotspots", inputParams -> {
                        String hotspotsSetName = addHotspotsSetDialog.getValue().getName();
                        File hotspotsFile = addHotspotsSetDialog.getValue().getFile();
                        long id = -1;
                        try {
                            List<Hotspot> hotspots = HotspotsParser.parseHotspotFile(hotspotsFile);
                            id = DAOController.getHotspotsSetDAO().addHotspotsSet(hotspotsSetName);
                            for (Hotspot hotspot : hotspots) {
                                DAOController.getHotspotDAO().addHotspot(id, hotspot);
                            }
                        } catch (Exception  ex) {
                            logger.error(ex);
                            Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                            if (id >= 0) {
                                try {
                                    DAOController.getHotspotsSetDAO().deleteHotspotsSet(id);
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
    }

    public TableView<HotspotsSet> getHotspotsSetTable() {return hotspotsSetTable;}
}
