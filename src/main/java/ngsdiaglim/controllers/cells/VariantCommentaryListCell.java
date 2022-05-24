package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import com.dlsc.gemsfx.ExpandingTextArea;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddVariantCommentaryDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.variants.VariantCommentary;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.tools.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class VariantCommentaryListCell extends ListCell<VariantCommentary> {

    private final static Logger logger = LogManager.getLogger(VariantCommentaryListCell.class);
    private FXMLLoader mLLoader;

    @FXML private Label usernameLb;
    @FXML private Label dateLb;
    @FXML private TextArea commentLb;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    @Override
    protected void updateItem(VariantCommentary item, boolean empty) {
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
//            commentLb.setText(item.getComment());
//            commentTa.setText(item.getComment());
////            commentLb.setWrapText(true);
////            commentLb.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
////            commentLb.minWidthProperty().bind(getListView().widthProperty());
//            commentTa.setWrapText(true);
//            commentTa.setMaxSize(TextArea.USE_COMPUTED_SIZE, TextArea.USE_COMPUTED_SIZE);
//            commentTf.getChildren().add(new Text(item.getComment()));
            commentLb.setText(item.getComment().trim());
//            expandingTa.requestFocus();
//            expandingTa.autosize();

//            Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/Ubuntu-Regular.ttf"), 16);

//            System.out.println(computeTextHeight(font, item.getComment().trim(), 0));
//            double textHeight = computeTextHeight(font, item.getComment().trim(), 0);
//            commentLb.setPrefHeight(textHeight);

            setGraphic(mLLoader.getRoot());

            boolean isAdmin = App.get().getLoggedUser().hasRole(DefaultRolesEnum.ADMIN);

            editBtn.setDisable(!isUsersComment(item) || isAdmin);
            deleteBtn.setDisable(!isUsersComment(item) || isAdmin);
            Platform.runLater(() -> {
                commentLb.applyCss();
                double textHeight = computeTextHeight(commentLb.getFont(), item.getComment().trim(), 0);
                commentLb.setPrefHeight(textHeight + 20); // padding
            });
        } else {
            getStyleClass().remove("transparent-list-cell");
            setGraphic(null);
        }
    }

    private boolean isUsersComment(VariantCommentary item) {
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
                    DAOController.getVariantCommentaryDAO().updateVariantCommentary(getItem().getId(), comment);
                    ModuleManager.getAnalysisViewController().getVariantsViewController().getVariantDetailController().loadVariantCommentaries();
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
                DAOController.getVariantCommentaryDAO().deleteVariantCommentary(getItem().getId());
                ModuleManager.getAnalysisViewController().getVariantsViewController().getVariantDetailController().loadVariantCommentaries();
                Message.hideDialog(d);
            } catch (SQLException ex) {
                logger.error(ex);
                Message.error(ex.getMessage(), ex);
            }
        });
    }

    static double computeTextHeight(Font font, String text, double wrappingWidth) {
        Text helper = new Text();

        helper.setText(text);

        helper.setFont(font);

        helper.setWrappingWidth((int)wrappingWidth);

        return helper.getLayoutBounds().getHeight();

    }
}
