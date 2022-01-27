package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.ciq.CIQModelActionCell;
import ngsdiaglim.controllers.dialogs.AddCIQDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class AnalysisParametersCIQController extends HBox {

    private final static Logger logger = LogManager.getLogger(AnalysisParametersCIQController.class);

    @FXML private TableView<CIQModel> ciqModelTableView;
    @FXML private TableColumn<CIQModel, String> ciqModelNameCol;
    @FXML private TableColumn<CIQModel, Integer> ciqModelAnalysesNbCol;
    @FXML private TableColumn<CIQModel, Boolean> ciqModelActiveCol;
    @FXML private TableColumn<CIQModel, Void> ciqModelActionCol;
    @FXML private TableView<CIQHotspot> ciqHotspotTableView;
    @FXML private TableColumn<CIQHotspot, String> ciqHotspotNameCol;
    @FXML private TableColumn<CIQHotspot, String> ciqHotspotContigCol;
    @FXML private TableColumn<CIQHotspot, Integer> ciqHotspotPositionCol;
    @FXML private TableColumn<CIQHotspot, String> ciqHotspotRefCol;
    @FXML private TableColumn<CIQHotspot, String> ciqHotspotAltCol;

    public AnalysisParametersCIQController(CreateAnalysisParametersController createAnalysisParametersController) {
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisParametersCIQ.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();

        try {
            loadCIQs();
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    public void initView() {
        initCIQModelTable();
        initHotspotTable();
    }

    private void initCIQModelTable() {
        ciqModelNameCol.setCellValueFactory(data -> data.getValue().barcodeProperty());
        ciqModelAnalysesNbCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getHotspots().size()).asObject());
        ciqModelActiveCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActive()));
        ciqModelActiveCol.setCellFactory(p -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<CIQModel, Boolean> tableCell = new TableCell<>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    setText(null);
                    if (empty || item == null)
                        setGraphic(null);
                    else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                    }
                }
            };
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validateCIQActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
        ciqModelActionCol.setCellFactory(c -> new CIQModelActionCell());

        ciqModelTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillHotspotsTable(newV));
        }

    private void fillHotspotsTable(CIQModel selectedCIQModel) {
        if (selectedCIQModel == null) {
            ciqHotspotTableView.getItems().clear();
        } else {
            ciqHotspotTableView.getItems().setAll(selectedCIQModel.getHotspots());
        }
    }

    private void initHotspotTable() {
        ciqHotspotNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        ciqHotspotContigCol.setCellValueFactory(data -> data.getValue().contigProperty());
        ciqHotspotPositionCol.setCellValueFactory(data -> data.getValue().positionProperty().asObject());
        ciqHotspotRefCol.setCellValueFactory(data -> data.getValue().refProperty());
        ciqHotspotAltCol.setCellValueFactory(data -> data.getValue().altProperty());
    }

    private void validateCIQActivation(CheckBox checkBox, CIQModel item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getBarcode()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.module.ciq.msg.inactiveciq", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.module.ciq.msg.activeciq", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                item.setActive(!checkBox.isSelected());
                checkBox.setSelected(!checkBox.isSelected());
                DAOController.getCiqModelDAO().updateCIQModel(item);
                loadCIQs();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }


    private void loadCIQs() throws SQLException {
        ciqModelTableView.getItems().setAll(DAOController.getCiqModelDAO().getCIQModels());
    }


    @FXML
    private void addCIQHandler() {
        AddCIQDialog dialog = new AddCIQDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (dialog.isValid() && dialog.getValue() != null) {
                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addpaneldialog.msg.loading"));
                wid.addTaskEndNotification(r -> {
                    if (r == 0) {
                        try {
                            loadCIQs();
                            Message.hideDialog(dialog);
                        } catch (SQLException ex) {
                            logger.error("Error when adding panel", ex);
                            Message.error(ex.getMessage(), ex);
                        }
                    }
                });
                wid.exec("AddCIQ", inputParam -> {
                    String CIQname = dialog.getValue().getName();
                    String CIQBarcode = dialog.getValue().getBarcode();
                    List<CIQHotspot> hotspots = dialog.getValue().getHotspots();
                    long ciqModelId = -1;
                    try {
                        ciqModelId = DAOController.getCiqModelDAO().addCIQModel(CIQname, CIQBarcode);
                        for (CIQHotspot h : hotspots) {
                            DAOController.getCiqHotspotDAO().addCIQHotspot(h, ciqModelId);
                        }
                    } catch (SQLException ex) {
                        logger.error(ex);
                        Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                        if (ciqModelId >= 0) {
                            try {
                                DAOController.getCiqModelDAO().deleteCIQModel(ciqModelId);
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

    public TableView<CIQModel> getCiqModelTableView() {return ciqModelTableView;}
}
