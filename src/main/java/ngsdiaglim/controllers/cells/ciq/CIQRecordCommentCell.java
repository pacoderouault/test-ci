package ngsdiaglim.controllers.cells.ciq;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.dialogs.TextDialog;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

public class CIQRecordCommentCell extends TableCell<CIQVariantRecord, CIQRecordHistory> {

    private final Button showCommentBtn = new Button("", new FontIcon("mdmz-open_in_new"));
    private final static PopOver commentPopOver = new PopOver();
    private final static TextArea commentTa = new TextArea();

    public CIQRecordCommentCell() {
        commentTa.setEditable(false);
        commentPopOver.setContentNode(commentTa);
        commentPopOver.setAnimated(false);
        commentPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);

        showCommentBtn.getStyleClass().add("button-action-cell");
        showCommentBtn.setOnAction(e -> showComment());
        setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    protected void updateItem(CIQRecordHistory item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty || StringUtils.isBlank(item.getComment())) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getComment());
            setGraphic(showCommentBtn);
        }
    }

    private void showComment() {
        CIQRecordHistory h = getItem();
        if (h != null) {
            String comment = h.getComment();
            if (StringUtils.isNotBlank(comment)) {
                commentTa.setText(comment);
                commentPopOver.show(showCommentBtn);
            }
        }

    }
}
