package ngsdiaglim.controllers.cells.genespanel;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.AddGenePanelDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.GenePanel;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class GenesPanelActionsCell extends TableCell<GenePanel, Void> {

    private final Logger logger = LogManager.getLogger(GenesPanelActionsCell.class);

    private final HBox box = new HBox();
    private final Button editBtn = new Button("", new FontIcon("mdal-edit"));
    private final Button deleteBtn = new Button("", new FontIcon("mdal-delete_forever"));

    public GenesPanelActionsCell() {
        box.getStyleClass().add("box-action-cell");
        editBtn.getStyleClass().add("button-action-cell");
        deleteBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(editBtn, deleteBtn);

        editBtn.setOnAction(e -> editGenePanelHandler());
        deleteBtn.setOnAction(e -> deleteGenePanelHandler());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        editBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_EDIT_GENEPANEL));
        deleteBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.REMOVE_GENEPANEL));
        if(empty) {
            setGraphic(null);
        }
        else {
            setGraphic(box);
        }
    }


    private void editGenePanelHandler() {
        AddGenePanelDialog dialog = new AddGenePanelDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        GenePanel genePanel = getTableRow().getItem();
        dialog.editGenesPanel(genePanel);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (dialog.isValid() && dialog.getValue() != null) {
                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addpaneldialog.msg.loading"));
                wid.addTaskEndNotification(r -> {
                    if (r == 0) {
                        Message.hideDialog(dialog);
                    }
                });
                wid.exec("LoadPanels", inputParam -> {
                    try {
                        DAOController.getGenesPanelDAO().updateGenesPanel(genePanel.getId(), dialog.getValue().getName(), dialog.getValue().getSelectedGenes());
                        genePanel.setName(dialog.getValue().getName());
                        genePanel.setGenes(dialog.getValue().getSelectedGenes());
                        getTableView().refresh();
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


    private void deleteGenePanelHandler() {
        GenePanel genePanel = getTableRow().getItem();
        Object[] arguments = {genePanel.getName()};
        DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("addgenepaneldialog.msg.conf.deleteGenePanel", arguments));
        dialog.getButton(ButtonType.YES).setOnAction(event -> {
            try {
                DAOController.getGenesPanelDAO().removeGenesPanel(genePanel.getId());
                getTableView().getItems().remove(genePanel);
                getTableView().refresh();
            } catch (SQLException e) {
                logger.error("Error when deleting analysis", e);
                Message.error(e.getMessage(), e);
            }
            Message.hideDialog(dialog);
            try {
                ModuleManager.getHomeController().fillAnalysesTable();
            } catch (SQLException e) {
                logger.error("Error when getting analysis from db", e);
            }
        });

    }
}
