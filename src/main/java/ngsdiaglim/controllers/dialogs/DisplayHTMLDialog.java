package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.net.URL;

public class DisplayHTMLDialog extends DialogPane.Dialog<File> {

    private final WebView browser = new WebView();
//    private final WebEngine webEngine = browser.getEngine();

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
//        URL url = getValue().
//        System.out.println(url);
//        if (url != null) {
        browser.setPrefSize(800, 400);
        System.out.println(getValue().toURI().getPath());
//            webEngine.load(getValue().toURI().getPath());
        browser.getEngine().load(" <html lang=\"en\">\n" +
                    "<head><title>Hello World</title></head>\n" +
                    "<body><h1>Hello world!</h1></body>\n" +
                    "</html>");
//        }
//        System.out.println(browser.getEngine().getDocument().toString());
    }
}
