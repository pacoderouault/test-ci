package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AddVariantCommentaryDialog extends DialogPane.Dialog<AddVariantCommentaryDialog.VariantCommentaryData> {

    private final Logger logger = LogManager.getLogger(AddVariantCommentaryDialog.class);
    @FXML private VBox dialogContainer;
    @FXML private Label variantLb;
    @FXML private Label errorLb;
    @FXML private TextArea commentTa;

    public AddVariantCommentaryDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AddVariantCommentaryDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }


        setTitle(App.getBundle().getString("addvariantcommentary.title"));
        setContent(dialogContainer);
        setValue(new VariantCommentaryData());
        setValid(false);

        initView();

        commentTa.textProperty().addListener((obs,oldV, newV) -> {
            validComment();
        });
    }

    private void validComment() {
        String error = checkError();
        if (error != null) {
            errorLb.setText(error);
            setValid(false);
        } else {
            errorLb.setText(null);
            setValid(true);
        }
    }

    private void initView() {
        commentTa.textProperty().bindBidirectional(getValue().commentaryProperty());
    }

    private String checkError() {
        if (StringUtils.isBlank(commentTa.getText())) {
            return App.getBundle().getString("addvariantcommentary.msg.err.emptycomment");
        }
        return null;
    }

    public class VariantCommentaryData {
        private long id;
        private final SimpleStringProperty commentary = new SimpleStringProperty();

        public long getId() {return id;}

        public void setId(long id) {
            this.id = id;
        }

        public String getCommentary() {
            return commentary.get();
        }

        public SimpleStringProperty commentaryProperty() {
            return commentary;
        }

        public void setCommentary(String commentary) {
            this.commentary.set(commentary);
        }
    }
}
