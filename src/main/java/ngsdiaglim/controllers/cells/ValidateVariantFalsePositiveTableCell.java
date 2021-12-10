package ngsdiaglim.controllers.cells;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.VariantFalsePositive;
import ngsdiaglim.modeles.variants.VariantPathogenicity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class ValidateVariantFalsePositiveTableCell extends TableCell<VariantFalsePositive, Void> {

    private final Logger logger = LogManager.getLogger(ValidateVariantFalsePositiveTableCell.class);

    @Override
    protected void updateItem(Void unused, boolean empty) {
        super.updateItem(unused, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(null);
            User user = App.get().getLoggedUser();
            if (user.isPermitted(PermissionsEnum.VALIDATE_VARIANT_FALSE_POSITIVE)) {
                VariantFalsePositive vfp = getTableRow().getItem();
                if (vfp != null) {
                    try {
                        Variant v = DAOController.get().getVariantsDAO().getVariant(vfp.getVariantId());
                        if (!v.isFalsePositiveConfirmed()) {
                            if (vfp.equals(v.getFalsePositiveHistory().getLastVariantFalsePositive())) {
                                Button btn = new Button(App.getBundle().getString("editfalsepositivedialog.btn.validate"));
                                btn.setOnAction(e -> {
                                    vfp.setVerifiedUsername(user.getUsername());
                                    vfp.setVerifiedUserId(user.getId());
                                    vfp.setVerifiedDateTime(LocalDateTime.now());
                                    v.setFalsePositiveConfirmed(true);
                                    try {
                                        DAOController.get().getVariantFalsePositiveDAO().updateVariantFalsePositive(vfp);
                                        DAOController.get().getVariantsDAO().updateVariant(v);
                                        getTableView().refresh();
                                    } catch (SQLException ex) {
                                        logger.error(ex);
                                        Message.error(ex.getMessage(), ex);
                                    }
                                });
                                setGraphic(btn);
                            }
                        }
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
