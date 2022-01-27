package ngsdiaglim.controllers.cells.ciq;

import javafx.scene.control.*;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

public class CIQRecordHistoryCommentCell extends TableCell<CIQRecordHistory, String> {

    private final Button showCommentBtn = new Button("", new FontIcon("mdmz-open_in_new"));
    private final static PopOver commentPopOver = new PopOver();
    private final static TextArea commentTa = new TextArea();

    public CIQRecordHistoryCommentCell() {
        commentTa.setEditable(false);
        commentPopOver.setContentNode(commentTa);
        commentPopOver.setAnimated(false);
        commentPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);

        showCommentBtn.getStyleClass().add("button-action-cell");
        showCommentBtn.setOnAction(e -> showComment());
        setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty || StringUtils.isBlank(item)) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item);
            setGraphic(showCommentBtn);
        }
    }

    private void showComment() {
        String item = getItem();
        if (StringUtils.isNotBlank(item)) {
            commentTa.setText(item);
            commentPopOver.show(showCommentBtn);
        }
    }

}
