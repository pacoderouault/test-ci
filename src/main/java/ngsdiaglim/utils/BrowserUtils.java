package ngsdiaglim.utils;

import ngsdiaglim.App;

public class BrowserUtils {

    public static void openURL(String url) {
        if (url != null) {
            App.get().getHostServices().showDocument(url);
        }
    }
}
