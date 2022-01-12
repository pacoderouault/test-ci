package ngsdiaglim.controllers.dialogs;


import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.modeles.reports.ReportGeneCommentary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Set;

public class EditGeneReportCommentDialog extends DialogPane.Dialog<EditGeneReportCommentDialog.GeneReportData> {

    private final static Logger logger = LogManager.getLogger(EditGeneReportCommentDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private ComboBox<String> gene;
    @FXML private TextField title;
    @FXML private TextArea comment;
    @FXML private Label errorLb;
    private final SimpleBooleanProperty editable = new SimpleBooleanProperty(false);

    public EditGeneReportCommentDialog(Set<String> targetGenes) {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/EditGeneReportCommentDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("editGeneReportCommentdialog.title"));
        setContent(dialogContainer);

        setValue(new GeneReportData());
        gene.valueProperty().bindBidirectional(getValue().geneNameProperty());
        title.textProperty().bindBidirectional(getValue().titleProperty());
        comment.textProperty().bindBidirectional(getValue().commentProperty());

        gene.editableProperty().bind(editable);
        title.editableProperty().bind(editable);
        comment.editableProperty().bind(editable);

        gene.getItems().setAll(targetGenes);
        setValid(false);

        gene.valueProperty().addListener((obs, oldV, newV) -> setValidDialogState());
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

    public void setValue(ReportGeneCommentary reportGeneCommentary) {
        getValue().setGeneName(reportGeneCommentary.getGeneName());
        getValue().setTitle(reportGeneCommentary.getTitle());
        getValue().setComment(reportGeneCommentary.getComment());
    }

    private String checkError() {
        if (gene.getValue() == null || gene.getValue().isEmpty()) {
            return App.getBundle().getString("editGeneReportCommentdialog.msg.err.emptyGene");
        } else if (title.getText() == null || title.getText().isEmpty()) {
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

    public static class GeneReportData {

        private final SimpleStringProperty geneName = new SimpleStringProperty();
        private final SimpleStringProperty title = new SimpleStringProperty();
        private final SimpleStringProperty comment = new SimpleStringProperty();

        public String getGeneName() {
            return geneName.get();
        }

        public SimpleStringProperty geneNameProperty() {
            return geneName;
        }

        public void setGeneName(String geneName) {
            this.geneName.set(geneName);
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
