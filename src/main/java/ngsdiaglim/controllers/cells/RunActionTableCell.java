package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.ImportAnalysisDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.dialogs.RunInfoDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class RunActionTableCell extends TableCell<Run, Void> {

    private final  Logger logger = LogManager.getLogger(RunActionTableCell.class);

    private final HBox box = new HBox();
    private final Button deleteRoleBtn = new Button("", new FontIcon("mdal-delete_forever"));
    private final Button infoRoleBtn = new Button("", new FontIcon("mdal-info"));
    private final Button addAnalysesRoleBtn = new Button(App.getBundle().getString("home.module.runslist.table.actions.btn.addAnalyses"), new FontIcon("mdal-add_box"));

    public RunActionTableCell() {
        box.getStyleClass().add("box-action-cell");
        deleteRoleBtn.getStyleClass().add("button-action-cell");
        infoRoleBtn.getStyleClass().add("button-action-cell");
        addAnalysesRoleBtn.getStyleClass().add("button-action-cell");

        box.getChildren().addAll(infoRoleBtn, deleteRoleBtn, addAnalysesRoleBtn);

        deleteRoleBtn.setOnAction(e -> deleteRunHandler());
        infoRoleBtn.setOnAction(e -> showRunInfoDialog());
        addAnalysesRoleBtn.setOnAction(e -> showImportAnalysesDialog());

    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        deleteRoleBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.REMOVE_RUN));
        addAnalysesRoleBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_ANALYSE));
        if(empty) {
            setGraphic(null);
        }
        else {
            setGraphic(box);
        }
    }


    private void deleteRunHandler() {
        Run run = getTableRow().getItem();
        if (run != null) {
            Object[] arguments = {run.getName()};
            DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("home.module.runslist.table.actions.msg.deleteRunConfirm", arguments));
            dialog.getButton(ButtonType.YES).setOnAction(event -> {
                try {
                    DAOController.get().getRunsDAO().deleteRun(run.getId());
                    ModuleManager.getHomeController().loadRuns();
                    Message.hideDialog(dialog);
                } catch (SQLException e) {
                    logger.error("Error when deleting run", e);
                    Message.error(e.getMessage(), e);
                }
            });
        }
    }


    private void showRunInfoDialog() {
        Run run = getTableRow().getItem();
        if (run != null) {
            RunInfoDialog dialog = new RunInfoDialog(App.get().getAppController().getDialogPane());
            dialog.setValue(run);
            Message.showDialog(dialog);
        }
    }


    private void showImportAnalysesDialog() {
        Run run = getTableRow().getItem();
        if (run != null) {
            ImportAnalysisDialog dialog = new ImportAnalysisDialog(App.get().getAppController().getDialogPane(), run);
            Message.showDialog(dialog);
        }
    }
}
