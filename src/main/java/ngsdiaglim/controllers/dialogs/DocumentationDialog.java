package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import ngsdiaglim.App;
import ngsdiaglim.utils.BrowserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class DocumentationDialog  extends DialogPane.Dialog<Void>{

    private static final Logger logger = LogManager.getLogger(DocumentationDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private WebView webview;
    private final WebEngine webEngine;

    public DocumentationDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);

        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/DocumentationDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("importanalysesdialog.title"));
        setContent(dialogContainer);

        webEngine = webview.getEngine();

        loadDocumentation();
    }

    private void loadDocumentation() {
        URL is = getClass().getClassLoader().getResource("documentation.html");
        if (is != null) {
            BrowserUtils.openURL(is.toExternalForm());
            webEngine.load(is.toExternalForm());
        }

    }
}
