package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddPanelDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.parsers.PanelParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.FilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class AnalysisParametersPanelsController extends HBox {

    private static final Logger logger = LogManager.getLogger(AnalysisParametersPanelsController.class);

    @FXML private TableView<Panel> panelsTable;
    @FXML private TableColumn<Panel, String> panelsNameCol;
    @FXML private TableColumn<Panel, Integer> panelsSizeCol;
    @FXML private TableColumn<Panel, Boolean> panelsActiveCol;

    @FXML private TableView<PanelRegion> regionsTable;
    @FXML private TableColumn<PanelRegion, String> regionsContigCol;
    @FXML private TableColumn<PanelRegion, Integer> regionsStartCol;
    @FXML private TableColumn<PanelRegion, Integer> regionsEndCol;
    @FXML private TableColumn<PanelRegion, String> regionsNameCol;

    public AnalysisParametersPanelsController(CreateAnalysisParametersController createAnalysisParametersController) {
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisParametersPanels.fxml"), App.getBundle());
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
        initPanelsTable();
        initRegionsTable();

        try {
            loadPanels();
        } catch (SQLException e) {
            logger.error("Error when loading panel", e);
            Message.error(e.getMessage(), e);
        }
    }

    private void  initPanelsTable(){
        panelsTable.setEditable(true);
        panelsNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        panelsSizeCol.setCellValueFactory(data -> {
            try {
                return new SimpleIntegerProperty(data.getValue().getSize()).asObject();
            } catch (SQLException e) {
                logger.error("Error when getting panel size");
                return new SimpleIntegerProperty(-1).asObject();
            }

        });

        panelsActiveCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActive()));
        panelsActiveCol.setCellFactory(p -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<Panel, Boolean> tableCell = new TableCell<>() {

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
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validatePanelActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
    }

    private void initRegionsTable() {
        regionsContigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        regionsStartCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        regionsEndCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        regionsNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        panelsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                try {
                    regionsTable.getItems().setAll(newV.getRegions());
                } catch (SQLException e) {
                    logger.error("Error when load panel regions", e);
                    Message.error(e.getMessage(), e);
                }
            }
        });
    }


    @FXML
    private void addPanelHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
            AddPanelDialog dialog = new AddPanelDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.isValid() && dialog.getValue() != null) {
                    WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addpaneldialog.msg.loading"));
                    wid.addTaskEndNotification(r -> {
                        if (r == 0) {
                            try {
                                loadPanels();
                                Message.hideDialog(dialog);
                            } catch (SQLException ex) {
                                logger.error("Error when adding panel", ex);
                                Message.error(ex.getMessage(), ex);
                            }
                        }
                    });
                    wid.exec("LoadPanels", inputParam -> {
                        long panelId = -1;
                        try {
                            List<PanelRegion> regions = PanelParser.parsePanel(dialog.getValue().getBedFile());
                            // save the panel in file
                            Path panelDataPath = App.getPanelsDataPath();
                            if (!Files.exists(panelDataPath)) {
                                Files.createDirectories(panelDataPath);
                            }
                            File panelFile = Paths.get(panelDataPath.toString(), dialog.getValue().getName() + ".bed.gz").toFile();
                            PanelParser.writePanel(regions, panelFile);
                            Path panelPath = FilesUtils.convertAbsolutePathToRelative(panelFile.toPath());
                            panelId = DAOController.getPanelDAO().addPanel(dialog.getValue().getName(), panelPath.toString());
                            for (PanelRegion region : regions) {
                                DAOController.getPanelRegionDAO().addRegion(region, panelId);
                            }

                        } catch (Exception ex) {
                            logger.error("Error when adding panel", ex);
                            Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                            try {
                                DAOController.getPanelDAO().deletePanel(panelId);
                            } catch (SQLException exc) {
                                logger.error("Error when deleting panel", exc);
                                Platform.runLater(() -> Message.error(exc.getMessage(), exc));
                            }
                            return 1;
                        }
                        return 0;
                    });
                }
            });
        }
    }

    private void validatePanelActivation(CheckBox checkBox, Panel item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getName()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.inactivePanel", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.reactivePanel", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.getPanelDAO().updatePanel(item, !item.isActive());
                item.setActive(!checkBox.isSelected());
                checkBox.setSelected(!checkBox.isSelected());
                loadPanels();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }


    private void loadPanels() throws SQLException {
        panelsTable.getItems().setAll(DAOController.getPanelDAO().getPanels());
    }

    public TableView<Panel> getPanelsTable() {return panelsTable;}
}
