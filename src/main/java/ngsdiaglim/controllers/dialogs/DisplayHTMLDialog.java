package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.web.WebView;

import java.io.File;

public class DisplayHTMLDialog extends DialogPane.Dialog<File> {

    private final WebView browser = new WebView();

    public DisplayHTMLDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);
        setContent(browser);
        valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                initView();
            }
        });
    }

    private void initView() {
        setTitle(getValue().getName());
        browser.setPrefSize(800, 400);
        browser.getEngine().load(" <html lang=\"en\">\n" +
                    "<head><title>Hello World</title></head>\n" +
                    "<body><h1>Hello world!</h1></body>\n" +
                    "</html>");
    }
}
