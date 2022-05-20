package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class AnalysisParametersPanelDeleteCell  extends TableCell<Panel, Void> {

    private final Logger logger = LogManager.getLogger(AnalysisParametersPanelDeleteCell.class);

    private final HBox box = new HBox();
    private final Button deletePanelBtn = new Button("", new FontIcon("mdal-delete_forever"));

    public AnalysisParametersPanelDeleteCell() {
        box.getStyleClass().add("box-action-cell");
        deletePanelBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(deletePanelBtn);
        deletePanelBtn.setOnAction(e -> deletePanelHandler());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        deletePanelBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
        if(empty) {
            setGraphic(null);
        }
        else {
            setGraphic(box);
        }
    }

    private void deletePanelHandler() {
        Panel panel = getTableRow().getItem();

        if (panel != null) {
            try {
                if (DAOController.getPanelDAO().isUsed(panel.getId())) {
                    Message.error(App.getBundle().getString("createAnalasisParameters.module.panels.msg.err.parametersUsed"));
                }
                else {
                    Object[] arguments = {panel.getName()};
                    DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("createAnalasisParameters.msg.confirm.deletePanel", arguments));
                    dialog.getButton(ButtonType.YES).setOnAction(event -> {
                        try {
                            DAOController.getPanelDAO().deletePanel(panel.getId());
                            getTableView().getItems().remove(panel);
                            Message.hideDialog(dialog);
                        } catch (SQLException e) {
                            logger.error("Error when deleting panel", e);
                            Message.error(e.getMessage(), e);
                        }
                    });
                }
            } catch (SQLException e) {
                logger.error("Error when deleting panel", e);
                Message.error(e.getMessage(), e);
            }
        }
    }
}