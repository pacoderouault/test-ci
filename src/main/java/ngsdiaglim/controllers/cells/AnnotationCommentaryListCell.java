package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddVariantCommentaryDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.variants.AnnotationCommentary;
import ngsdiaglim.modeles.variants.VariantCommentary;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class AnnotationCommentaryListCell extends ListCell<AnnotationCommentary> {
    private final Logger logger = LogManager.getLogger(VariantCommentaryListCell.class);
    private AnnotationCommentary vc;
    private FXMLLoader mLLoader;

    @FXML private Label usernameLb;
    @FXML private Label dateLb;
    @FXML private Label commentLb;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    @Override
    protected void updateItem(AnnotationCommentary item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            getStyleClass().add("transparent-list-cell");
            vc = item;
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/VariantCommentaryListCell.fxml"), App.getBundle());
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            usernameLb.setText(item.getUsername());
            dateLb.setText(DateFormatterUtils.formatLocalDateTime(item.getDatetime(), "dd/MM/yyyy HH:ss"));
            commentLb.setText(item.getComment());
            setGraphic(mLLoader.getRoot());

            boolean isAdmin = App.get().getLoggedUser().hasRole(DefaultRolesEnum.ADMIN);

            editBtn.setDisable(!isUsersComment(item) || isAdmin);
            deleteBtn.setDisable(!isUsersComment(item) || isAdmin);

        } else {
            getStyleClass().remove("transparent-list-cell");
            setGraphic(null);
        }
    }

    private boolean isUsersComment(AnnotationCommentary item) {
        return App.get().getLoggedUser().getId() == item.getUserID();
    }

    @FXML
    private void editComment() {
        AddVariantCommentaryDialog addVariantCommentaryDialog = new AddVariantCommentaryDialog(App.get().getAppController().getDialogPane());
        addVariantCommentaryDialog.getValue().setId(getItem().getId());
        addVariantCommentaryDialog.getValue().setCommentary(getItem().getComment());
        Message.showDialog(addVariantCommentaryDialog);
        Button b = addVariantCommentaryDialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (addVariantCommentaryDialog.isValid() && addVariantCommentaryDialog.getValue() != null) {
                String comment = addVariantCommentaryDialog.getValue().getCommentary();
                try {
                    DAOController.get().getAnnotationCommentaryDAO().updateAnnotationCommentary(getItem().getId(), comment);
                    ModuleManager.getAnalysisViewController().getVariantsViewController().getVariantDetailController().loadAnnotationCommentaries();
                    Message.hideDialog(addVariantCommentaryDialog);
                } catch (SQLException ex) {
                    logger.error(ex);
                    Message.error(ex.getMessage(), ex);
                }

            }
        });
    }

    @FXML
    private void deleteComment() {
        DialogPane.Dialog<ButtonType> d =  Message.confirm(App.getBundle().getString("analysisview.variantdetail.msg.deletecomment"));
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.get().getAnnotationCommentaryDAO().deleteAnnotationCommentary(getItem().getId());
                ModuleManager.getAnalysisViewController().getVariantsViewController().getVariantDetailController().loadAnnotationCommentaries();
                Message.hideDialog(d);
            } catch (SQLException ex) {
                logger.error(ex);
                Message.error(ex.getMessage(), ex);
            }
        });
    }
}
