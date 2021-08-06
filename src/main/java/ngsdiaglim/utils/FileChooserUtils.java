package ngsdiaglim.utils;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;

import java.io.File;

public class FileChooserUtils {

    public static FileChooser getFileChooser() {
        FileChooser fc  = new FileChooser();
        String initialPath = App.get().getLoggedUser().getPreferences().getProperty(DefaultPreferencesEnum.INITIAL_DIR.name());
        File initialDir = new File(initialPath);
        if (initialDir.exists() && initialDir.isDirectory()) {
            fc.setInitialDirectory(initialDir);
        }

        return fc;
    }

    public static DirectoryChooser getDirectoryChooser() {
        DirectoryChooser dc  = new DirectoryChooser();
        String initialPath = App.get().getLoggedUser().getPreferences().getProperty(DefaultPreferencesEnum.INITIAL_DIR.name());
        File initialDir = new File(initialPath);
        if (initialDir.exists() && initialDir.isDirectory()) {
            dc.setInitialDirectory(initialDir);
        }
        return dc;
    }

}
