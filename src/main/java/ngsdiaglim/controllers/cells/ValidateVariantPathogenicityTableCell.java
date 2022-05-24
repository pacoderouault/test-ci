package ngsdiaglim.controllers.cells;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.VariantPathogenicity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class ValidateVariantPathogenicityTableCell extends TableCell<VariantPathogenicity, Void> {

    private final static Logger logger = LogManager.getLogger(ValidateVariantPathogenicityTableCell.class);

    @Override
    protected void updateItem(Void unused, boolean empty) {
        super.updateItem(unused, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(null);
            User user = App.get().getLoggedUser();
            if (user.isPermitted(PermissionsEnum.VALIDATE_VARIANT_PATHOGENICITY)) {
                VariantPathogenicity vp = getTableRow().getItem();
                if (vp != null) {
                    try {
                        Variant v = DAOController.getVariantsDAO().getVariant(vp.getVariantId());
                        if (!v.isPathogenicityConfirmed()) {
                            if (vp.equals(v.getPathogenicityHistory().getLastVariantPathogenicity())) {
                                Button btn = new Button(App.getBundle().getString("editpathogenicitydialog.btn.validatepathogenicity"));
                                btn.setOnAction(e -> {
                                    vp.setVerifiedUsername(user.getUsername());
                                    vp.setVerifiedUserId(user.getId());vp.setVerifiedDateTime(LocalDateTime.now());
                                    v.setPathogenicityConfirmed(true);
                                    try {
                                        DAOController.getVariantPathogenicityDAO().updateVariantPathogenicity(vp);
                                        DAOController.getVariantsDAO().updateVariant(v);
                                        getTableView().refresh();
                                    } catch (SQLException ex) {
                                        logger.error(ex.getMessage(), ex);
                                        Message.error(ex.getMessage(), ex);
                                    }
                                });
                                setGraphic(btn);
                            }
                        }
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
