package ngsdiaglim.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.RunActionTableCell;
import ngsdiaglim.controllers.cells.RunStateTableCell;
import ngsdiaglim.controllers.dialogs.AddRunDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class HomeController extends Module {

    Logger logger = LogManager.getLogger(HomeController.class);

    @FXML private TableView<Run> runsTable;
    @FXML private TableColumn<Run, String> runNameCol;
    @FXML private TableColumn<Run, LocalDate> runDateCol;
    @FXML private TableColumn<Run, Void> runStateCol;
    @FXML private TableColumn<Run, Void> runActionsCol;
    @FXML private Button addRunBtn;

    public HomeController() {
        super(App.getBundle().getString("home.title"));
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {

        initRunTable();
        try {
            loadRuns();
        } catch (SQLException e) {
            logger.error("Error when getting runs", e);
            Message.error(e.getMessage(), e);
        }

        addRunBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_RUN));
    }


    private void initRunTable() {

        runNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        runDateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        runStateCol.setCellFactory(data -> new RunStateTableCell());
        runActionsCol.setCellFactory(data -> new RunActionTableCell());

        runsTable.prefWidthProperty().bind(
                runNameCol.widthProperty()
                        .subtract(runDateCol.widthProperty())
                        .subtract(runStateCol.widthProperty())
                        .subtract(runActionsCol.widthProperty())
                        .subtract(2)  // a border stroke?
        );
    }


    @FXML
    private void addRunHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_RUN)) {
            AddRunDialog dialog = new AddRunDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.isValid() && dialog.getValue() != null) {
                    long runId = -1;
                    try {
                        runId = DAOController.get().getRunsDAO().addRun(dialog.getValue().getRunName(), dialog.getValue().getRunDate());
                        loadRuns();
                        Message.hideDialog(dialog);
                    } catch (SQLException ex) {
                        try {
                            DAOController.get().getRunsDAO().deleteRun(runId);
                        } catch (SQLException exc) {
                            logger.error("Error when deleting run", exc);
                        }
                        logger.error("Error when adding run", ex);
                        Message.error(ex.getMessage(), ex);
                    }
                }

            });
        }
    }


    public void loadRuns() throws SQLException {
        runsTable.getItems().setAll(DAOController.get().getRunsDAO().getRuns());
    }
}
