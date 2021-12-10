package ngsdiaglim.controllers.cells.report;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.EditReportCommentDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class OtherCommentActionTableCell extends TableCell<ReportCommentary, Void> {

    private final static Logger logger = LogManager.getLogger(OtherCommentActionTableCell.class);
    private final HBox box = new HBox();

    public OtherCommentActionTableCell() {

        final Button showCommentBtn = new Button("", new FontIcon("mdoal-comment"));
        final Button editCommentBtn = new Button("", new FontIcon("mdal-edit"));
        final Button deleteCommentBtn = new Button("", new FontIcon("mdal-delete_forever"));
        final Button addCommentBtn = new Button("", new FontIcon("mdoal-add_comment"));
        final Tooltip showCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.showcomment"));
        final Tooltip editCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.editcomment"));
        final Tooltip deleteCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.deletecomment"));
        final Tooltip addCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.addcomment"));

        box.getStyleClass().add("box-action-cell");
        showCommentBtn.getStyleClass().add("button-action-cell");
        editCommentBtn.getStyleClass().add("button-action-cell");
        deleteCommentBtn.getStyleClass().add("button-action-cell");
        addCommentBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(showCommentBtn, editCommentBtn, deleteCommentBtn, addCommentBtn);

        showCommentTooltip.setShowDelay(Duration.ZERO);
        editCommentTooltip.setShowDelay(Duration.ZERO);
        deleteCommentTooltip.setShowDelay(Duration.ZERO);
        addCommentTooltip.setShowDelay(Duration.ZERO);

        showCommentBtn.setTooltip(showCommentTooltip);
        editCommentBtn.setTooltip(editCommentTooltip);
        deleteCommentBtn.setTooltip(deleteCommentTooltip);
        addCommentBtn.setTooltip(addCommentTooltip);

        showCommentBtn.setOnAction(e -> showComment());
        editCommentBtn.setOnAction(e -> editComment());
        deleteCommentBtn.setOnAction(e -> deleteComment());
        addCommentBtn.setOnAction(e -> addComment());
    }


    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(box);
        }
    }


    private void showComment() {
        ReportCommentary commentary = getTableRow().getItem();
        if (commentary != null) {
            EditReportCommentDialog dialog = new EditReportCommentDialog();
            dialog.setValue(commentary);
            Message.showDialog(dialog);
            dialog.getButton(ButtonType.OK).setOnAction(event -> {
                Message.hideDialog(dialog);
            });
        }
    }


    private void editComment() {
        User user = App.get().getLoggedUser();
        if (user.isPermitted(PermissionsEnum.EDIT_REPORT_COMMENT)) {
            ReportCommentary commentary = getTableRow().getItem();
            if (commentary != null) {
                EditReportCommentDialog dialog = new EditReportCommentDialog();
                dialog.setValue(commentary);
                dialog.setEditable(true);
                Message.showDialog(dialog);
                dialog.getButton(ButtonType.OK).setOnAction(event -> {
                    try {
                        DAOController.getReportCommentaryDAO().editReportCommentary(
                                commentary.getId(),
                                dialog.getValue().getTitle(),
                                dialog.getValue().getComment()
                        );
                        commentary.setTitle(dialog.getValue().getTitle());
                        commentary.setComment(dialog.getValue().getComment());
                        getTableView().refresh();
                        Message.hideDialog(dialog);
                    } catch (SQLException e) {
                        logger.error(e);
                        Message.error(e.getMessage(), e);
                    }
                });
            }
        }
    }


    private void deleteComment() {
        User user = App.get().getLoggedUser();
        if (user.isPermitted(PermissionsEnum.EDIT_REPORT_COMMENT)) {
            ReportCommentary commentary = getTableRow().getItem();
            if (commentary != null) {
                DialogPane.Dialog<ButtonType> dialog = Message.confirm(App.getBundle().getString("analysisviewreports.reportcomments.msg.confirm.confirmDelete"));
                dialog.getButton(ButtonType.YES).setOnAction(event -> {
                    try {
                        DAOController.getReportCommentaryDAO().deleteReportCommentary(commentary.getId());
                        getTableView().getItems().remove(commentary);
                        Message.hideDialog(dialog);
                    } catch (SQLException e) {
                        logger.error(e);
                        Message.error(e.getMessage(), e);
                    }
                });
            }
        }
    }


    private void addComment() {
        ReportCommentary commentary = getTableRow().getItem();
        if (commentary != null) {
            ModuleManager.getAnalysisViewController().getReportBGMController().getReportComments().addCommentToReport(commentary);
        }
    }
}
