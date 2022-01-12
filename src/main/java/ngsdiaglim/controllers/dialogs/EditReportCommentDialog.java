package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class EditReportCommentDialog  extends DialogPane.Dialog<EditReportCommentDialog.ReportCommentData> {

    private final static Logger logger = LogManager.getLogger(EditGeneReportCommentDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TextField title;
    @FXML private TextArea comment;
    @FXML private Label errorLb;
    private final SimpleBooleanProperty editable = new SimpleBooleanProperty(false);

    public EditReportCommentDialog() {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INPUT);

        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/EditReportCommentDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("editReportCommentdialog.title"));
        setContent(dialogContainer);

        setValue(new EditReportCommentDialog.ReportCommentData());
        title.textProperty().bindBidirectional(getValue().titleProperty());
        comment.textProperty().bindBidirectional(getValue().commentProperty());

        title.editableProperty().bind(editable);
        comment.editableProperty().bind(editable);
        setValid(false);
        title.textProperty().addListener((obs, oldV, newV) -> setValidDialogState());
        comment.textProperty().addListener((obs, oldV, newV) -> setValidDialogState());
    }

    public boolean isEditable() {
        return editable.get();
    }

    public SimpleBooleanProperty editableProperty() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public void setValue(ReportCommentary reportGeneCommentary) {
        getValue().setTitle(reportGeneCommentary.getTitle());
        getValue().setComment(reportGeneCommentary.getComment());
    }

    private String checkError() {
        if (title.getText() == null || title.getText().isEmpty()) {
            return App.getBundle().getString("editGeneReportCommentdialog.msg.err.emptyTitle");
        } else if (comment.getText() == null || comment.getText().isEmpty()) {
            return App.getBundle().getString("editGeneReportCommentdialog.msg.err.emptyComment");
        }
        return null;
    }

    private void setValidDialogState() {
        String error = checkError();
        errorLb.setText(error);
        setValid(error == null);
    }

    public static class ReportCommentData {

        private final SimpleStringProperty title = new SimpleStringProperty();
        private final SimpleStringProperty comment = new SimpleStringProperty();


        public String getTitle() {
            return title.get();
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public void setTitle(String title) {
            this.title.set(title);
        }

        public String getComment() {
            return comment.get();
        }

        public SimpleStringProperty commentProperty() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment.set(comment);
        }
    }
}