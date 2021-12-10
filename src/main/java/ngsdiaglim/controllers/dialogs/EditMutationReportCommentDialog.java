package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.modeles.reports.ReportMutationCommentary;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class EditMutationReportCommentDialog  extends DialogPane.Dialog<EditMutationReportCommentDialog.MutationReportData> {

    private final static Logger logger = LogManager.getLogger(EditMutationReportCommentDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private ComboBox<Annotation> variantCb;
    @FXML private TextField title;
    @FXML private TextArea comment;
    @FXML private Label errorLb;
    private final List<Annotation> variantList;
    private final SimpleBooleanProperty editable = new SimpleBooleanProperty(false);

    public EditMutationReportCommentDialog(List<Annotation> variantList) {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INPUT);
        this.variantList = variantList;
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/EditMutationReportCommentDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("editMutationReportCommentdialog.title"));
        setContent(dialogContainer);

        setValue(new MutationReportData());
        variantCb.valueProperty().bindBidirectional(getValue().variantProperty());
        title.textProperty().bindBidirectional(getValue().titleProperty());
        comment.textProperty().bindBidirectional(getValue().commentProperty());

        variantCb.disableProperty().bind(editable.not());
        title.editableProperty().bind(editable);
        comment.editableProperty().bind(editable);

        setValid(false);
        variantCb.getItems().setAll(variantList);

        variantCb.valueProperty().addListener((obs, oldV, newV) -> setValidDialogState());
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

    public void setValue(ReportMutationCommentary reportMutationCommentary) {
        getValue().setVariant(reportMutationCommentary.getAnnotation());
        getValue().setTitle(reportMutationCommentary.getTitle());
        getValue().setComment(reportMutationCommentary.getComment());
    }

    private String checkError() {
        if (title.getText() == null || title.getText().isEmpty()) {
            return App.getBundle().getString("editMutationReportCommentdialog.msg.err.emptyTitle");
        } else if (comment.getText() == null || comment.getText().isEmpty()) {
            return App.getBundle().getString("editMutationReportCommentdialog.msg.err.emptyComment");
        }
        return null;
    }

    private void setValidDialogState() {
        String error = checkError();
        errorLb.setText(error);
        setValid(error == null);
    }

    public static class MutationReportData {

        private final ObjectProperty<Annotation> variant = new SimpleObjectProperty<>();
        private final SimpleStringProperty title = new SimpleStringProperty();
        private final SimpleStringProperty comment = new SimpleStringProperty();

        public Annotation getVariant() {
            return variant.get();
        }

        public ObjectProperty<Annotation> variantProperty() {
            return variant;
        }

        public void setVariant(Annotation variant) {
            this.variant.set(variant);
        }

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