package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddAnalysisCommentaryDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.AnalysisCommentary;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class AnalysisCommentaryListCell  extends ListCell<AnalysisCommentary> {

    private final Logger logger = LogManager.getLogger(VariantCommentaryListCell.class);
    private FXMLLoader mLLoader;

    @FXML private Label usernameLb;
    @FXML private Label dateLb;
    @FXML private TextArea commentLb;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    @Override
    protected void updateItem(AnalysisCommentary item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            getStyleClass().add("transparent-list-cell");
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

    private boolean isUsersComment(AnalysisCommentary item) {
        return App.get().getLoggedUser().getId() == item.getUserID();
    }

    @FXML
    private void editComment() {
        AddAnalysisCommentaryDialog addVariantCommentaryDialog = new AddAnalysisCommentaryDialog(App.get().getAppController().getDialogPane());
        addVariantCommentaryDialog.getValue().setId(getItem().getId());
        addVariantCommentaryDialog.getValue().setCommentary(getItem().getComment());
        Message.showDialog(addVariantCommentaryDialog);
        Button b = addVariantCommentaryDialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (addVariantCommentaryDialog.isValid() && addVariantCommentaryDialog.getValue() != null) {
                String comment = addVariantCommentaryDialog.getValue().getCommentary();
                try {
                    DAOController.getAnalysisCommentaryDAO().updateAnalysisCommentary(getItem().getId(), comment);
                    ModuleManager.getAnalysisViewController().getAdditionalDataController().loadAnalysisCommentaries();
                    Message.hideDialog(addVariantCommentaryDialog);
                } catch (SQLException ex) {
                    logger.error(ex.getMessage(), ex);
                    Message.error(ex.getMessage(), ex);
                }

            }
        });
    }

    @FXML
    private void deleteComment() {
        DialogPane.Dialog<ButtonType> d = Message.confirm(App.getBundle().getString("analysisview.variantdetail.msg.deletecomment"));
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.getAnalysisCommentaryDAO().deleteAnalysisCommentary(getItem().getId());
                ModuleManager.getAnalysisViewController().getAdditionalDataController().loadAnalysisCommentaries();
                Message.hideDialog(d);
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                Message.error(ex.getMessage(), ex);
            }
        });
    }
}