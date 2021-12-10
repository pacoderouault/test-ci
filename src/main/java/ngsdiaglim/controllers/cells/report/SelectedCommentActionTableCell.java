package ngsdiaglim.controllers.cells.report;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.EditReportCommentDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

public class SelectedCommentActionTableCell extends TableCell<ReportCommentary, Void> {

    private final static Logger logger = LogManager.getLogger(OtherCommentActionTableCell.class);
    private final HBox box = new HBox();

    public SelectedCommentActionTableCell() {

        final Button upwardBtn = new Button("", new FontIcon("mdal-arrow_upward"));
        final Button downwardBtn = new Button("", new FontIcon("mdal-arrow_downward"));
        final Button showCommentBtn = new Button("", new FontIcon("mdoal-comment"));
        final Button deleteCommentBtn = new Button("", new FontIcon("mdmz-remove_circle_outline"));
        final Tooltip upwardTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.upwardcomment"));
        final Tooltip downwardTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.downwardcomment"));
        final Tooltip showCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.showcomment"));
        final Tooltip deleteCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.deletecomment"));
        final Tooltip addCommentTooltip = new Tooltip(App.getBundle().getString("analysisviewreports.reportcomments.tooltip.addcomment"));

        box.getStyleClass().add("box-action-cell");
        upwardBtn.getStyleClass().add("button-action-cell");
        downwardBtn.getStyleClass().add("button-action-cell");
        showCommentBtn.getStyleClass().add("button-action-cell");
        deleteCommentBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(upwardBtn, downwardBtn, showCommentBtn, deleteCommentBtn);

        upwardTooltip.setShowDelay(Duration.ZERO);
        downwardTooltip.setShowDelay(Duration.ZERO);
        showCommentTooltip.setShowDelay(Duration.ZERO);
        deleteCommentTooltip.setShowDelay(Duration.ZERO);
        addCommentTooltip.setShowDelay(Duration.ZERO);

        upwardBtn.setTooltip(upwardTooltip);
        downwardBtn.setTooltip(downwardTooltip);
        showCommentBtn.setTooltip(showCommentTooltip);
        deleteCommentBtn.setTooltip(deleteCommentTooltip);

        upwardBtn.setOnAction(e -> moveUp());
        downwardBtn.setOnAction(e -> moveDown());
        showCommentBtn.setOnAction(e -> showComment());
        deleteCommentBtn.setOnAction(e -> removeComment());
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


    private void moveUp() {
        int rowIdx = getIndex();
        ReportCommentary commentary = getTableRow().getItem();
        if (rowIdx > 0 && commentary != null) {
            getTableView().getItems().remove(rowIdx);
            int newIdx = rowIdx - 1;
            getTableView().getItems().add(newIdx, commentary);
            getTableView().getSelectionModel().select(newIdx);
        }
    }


    private void moveDown() {
        int rowIdx = getIndex();
        ReportCommentary commentary = getTableRow().getItem();
        if (rowIdx > getTableView().getItems().size() -1 && commentary != null) {
            getTableView().getItems().remove(rowIdx);
            int newIdx = rowIdx + 1;
            getTableView().getItems().add(newIdx, commentary);
            getTableView().getSelectionModel().select(newIdx);
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


    private void removeComment() {
        ReportCommentary commentary = getTableRow().getItem();
        if (commentary != null) {
            getTableView().getItems().remove(commentary);
        }
    }

}
