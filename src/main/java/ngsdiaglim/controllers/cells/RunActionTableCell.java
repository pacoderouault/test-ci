package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.dialogs.RunInfoDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class RunActionTableCell extends TableCell<Run, Void> {

    private final  Logger logger = LogManager.getLogger(RunActionTableCell.class);

    private final HBox box = new HBox();
    private final Button deleteRoleBtn = new Button("", new FontIcon("mdal-delete_forever"));
    private final Button infoRoleBtn = new Button("", new FontIcon("mdal-info"));
    private static final Tooltip runDetailTp = new Tooltip(App.getBundle().getString("home.module.runslist.table.actions.tp.runInfoTp"));
    private static final Tooltip deleteRunTp = new Tooltip(App.getBundle().getString("home.module.runslist.table.actions.tp.deleteRunTp"));

    public RunActionTableCell() {
        box.getStyleClass().add("box-action-cell");
        deleteRoleBtn.getStyleClass().add("button-action-cell");
        infoRoleBtn.getStyleClass().add("button-action-cell");
//        addAnalysesRoleBtn.getStyleClass().add("button-action-cell");

        box.getChildren().addAll(infoRoleBtn, deleteRoleBtn);

        runDetailTp.setShowDelay(Duration.ZERO);
        deleteRunTp.setShowDelay(Duration.ZERO);
        infoRoleBtn.setTooltip(runDetailTp);
        deleteRoleBtn.setTooltip(deleteRunTp);
        deleteRoleBtn.setOnAction(e -> deleteRunHandler());
        infoRoleBtn.setOnAction(e -> showRunInfoDialog());
//        addAnalysesRoleBtn.setOnAction(e -> showImportAnalysesDialog());

    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        deleteRoleBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.REMOVE_RUN));
//        addAnalysesRoleBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_ANALYSE));
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
                    DAOController.getRunsDAO().deleteRun(run.getId());
                    FileUtils.deleteDirectory(new File(run.getPath()));
                    ModuleManager.getHomeController().loadRuns();
                    Message.hideDialog(dialog);
                } catch (SQLException | IOException e) {
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



}
