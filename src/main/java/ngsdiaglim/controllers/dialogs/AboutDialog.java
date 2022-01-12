package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AboutDialog extends DialogPane.Dialog<Void> {

    private static final Logger logger = LogManager.getLogger(AboutDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private Label appNameLabel;
    @FXML private Label appVersionLabel;

    public AboutDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);

        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AboutDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }


        setTitle(App.getBundle().getString("addvariantcommentary.title"));
        setContent(dialogContainer);

        String appName = App.getAppName();
        String appVersion = App.getVersion();

        Object[] messageArgumentsTitle = {appName};
        Object[] messageArgumentsVersion = {appVersion};
        String title = BundleFormatter.format("aboutdialog.title", messageArgumentsTitle);

        appNameLabel.setText(BundleFormatter.format("aboutdialog.appName", messageArgumentsTitle));
        appVersionLabel.setText(BundleFormatter.format("aboutdialog.version", messageArgumentsVersion));

        setTitle(title);

    }
}
