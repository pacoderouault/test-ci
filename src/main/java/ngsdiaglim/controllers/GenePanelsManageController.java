package ngsdiaglim.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.genespanel.GenesPanelActionsCell;
import ngsdiaglim.controllers.dialogs.AddGenePanelDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GenePanel;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GenePanelsManageController extends Module {
    private final Logger logger = LogManager.getLogger(GenePanelsManageController.class);

    @FXML private TableView<GenePanel> genePanelTable;
    @FXML private TableColumn<GenePanel, String> genePanelNameCol;
    @FXML private TableColumn<GenePanel, Integer> genePanelGeneNbCol;
    @FXML private TableColumn<GenePanel, Void> genePanelActionsCol;
    @FXML private ListView<Gene> genesListView;

    public GenePanelsManageController() {
        super(App.getBundle().getString("usersmanage.lb.title"));
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/GenePanelsManage.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Problem when loading the manage user panel", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
    }

    @FXML
    public void initialize() {
        initGanesPanelTable();
        initGenesTable();
        try {
            loadGenesPanels();
        } catch (SQLException e) {
            logger.error("Error when loading panel", e);
            Message.error(e.getMessage(), e);
        }
    }

    private void initGanesPanelTable() {
        genePanelNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        genePanelGeneNbCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getGenes().size()).asObject());
        genePanelActionsCol.setCellFactory(data -> new GenesPanelActionsCell());
        genePanelTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                genesListView.getItems().clear();
            } else {
                genesListView.getItems().setAll(newV.getGenes());
            }
        });
    }


    private void initGenesTable() {
//        genesListView.setCellFactory(data -> data);
    }

    public void loadGenesPanels() throws SQLException {
//        try {
            genePanelTable.getItems().setAll(DAOController.getGenesPanelDAO().getGenesPanels());
//        } catch (SQLException e) {
//            logger.error(e);
//            Message.error(e.getMessage(), e);
//        }
    }

    @FXML
    private void addGenePanelHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_GENEPANELS)) {
            AddGenePanelDialog dialog = new AddGenePanelDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.isValid() && dialog.getValue() != null) {
                    WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addpaneldialog.msg.loading"));
                    wid.addTaskEndNotification(r -> {
                        if (r == 0) {
                            try {
                                loadGenesPanels();
                                Message.hideDialog(dialog);
                            } catch (SQLException ex) {
                                logger.error("Error when adding panel", ex);
                                Message.error(ex.getMessage(), ex);
                            }
                        }
                    });
                    wid.exec("LoadPanels", inputParam -> {
                        final long panelId;
                        try {
                            panelId = DAOController.getGenesPanelDAO().addGenesPanel(dialog.getValue().getName(), dialog.getValue().getSelectedGenes());
                        } catch (Exception ex) {
                            logger.error("Error when adding panel", ex);
                            Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                            return 1;
                        }
                        return 0;
                    });
                }
            });
        }
    }

}
