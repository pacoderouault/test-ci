package ngsdiaglim.controllers.cells.ciq;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class CIQModelActionCell extends TableCell<CIQModel, Void> {

    private static final Logger logger = LogManager.getLogger(CIQModelActionCell.class);

    private final HBox box = new HBox();
    private final Button deleteHotspotBtn = new Button("", new FontIcon("mdal-delete_forever"));
    public CIQModelActionCell() {
        box.getStyleClass().add("box-action-cell");
        deleteHotspotBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(deleteHotspotBtn);
        deleteHotspotBtn.setOnAction(e -> deleteCIQHandler());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(box);
            User user = App.get().getLoggedUser();
            deleteHotspotBtn.setDisable(!user.isPermitted(PermissionsEnum.ADD_EDIT_CIQ));
        }
    }

    private void deleteCIQHandler() {
        User user = App.get().getLoggedUser();
        if(user.isPermitted(PermissionsEnum.ADD_EDIT_CIQ)) {
            CIQModel ciq = getTableRow().getItem();
            if (ciq != null) {
                Object[] arguments = {ciq.getBarcode()};
                DialogPane.Dialog<ButtonType> dialog = Message.confirm(
                        BundleFormatter.format("createAnalasisParameters.module.ciq.msg.activeciqdeleteCIQ", arguments));
                dialog.getButton(ButtonType.YES).setOnAction(event -> {
                    try {
                        boolean ciqIsUsed = DAOController.getCiqAnalysisDAO().CIQisUsed(ciq.getId());
                        if (ciqIsUsed) {
                            Message.error(App.getBundle().getString("createAnalasisParameters.module.ciq.msg.err.deletingUsedCIQModel"));
                        } else {
                            DAOController.getCiqModelDAO().deleteCIQModel(ciq.getId());
                            getTableView().getItems().remove(ciq);
                            getTableView().refresh();
                            Message.hideDialog(dialog);
                        }
                    } catch (SQLException e) {
                        logger.error(e);
                        Message.error(e.getMessage());
                    }
                });
            }
        }
    }
}
