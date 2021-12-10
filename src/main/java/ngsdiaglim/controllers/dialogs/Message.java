package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import ngsdiaglim.App;

public class Message {

    private final static DialogPane dialogPane = initDialogPane();

    private Message(){}

    public DialogPane getDialogPane() {
        return dialogPane;
    }

    private static DialogPane initDialogPane() {
//        DialogPane dialogPane = new DialogPane();
        DialogPane dialogPane = App.get().getAppController().getDialogPane();
        dialogPane.setAnimationDuration(Duration.ZERO);
        dialogPane.setAnimateDialogs(false);
        dialogPane.setShowCloseButton(false);
        dialogPane.setFadeInOut(false);
        return dialogPane;
    }

    public static void info(String message) {
        info(null, message);
    }

    public static void info(String title, String message) {
        dialogPane.showInformation(title, message);
    }

    public static void warning(String message) {
        warning(null, message);
    }

    public static void warning(String title, String message) {
        dialogPane.showWarning(title, message);
    }

    public static DialogPane.Dialog<ButtonType> confirm(String message) {
        return confirm(null, message);
    }

    public static DialogPane.Dialog<ButtonType> confirm(String title, String message) {
        return dialogPane.showConfirmation(title, message);
    }

    public static void error(String message) {
        error(null, message);
    }

    public static void error(String message, Throwable e) {
        error(null, message);
    }

    public static void error(String title, String message) {
        dialogPane.showError(title, message);
    }

    public static void error(String title, String message, Throwable e) {
        dialogPane.showError(title, message, e);
    }

    public static DialogPane.Dialog<String> showTextInput(String title, String text) {
        return showTextInput(title, null, null, text, false);
    }

    public static DialogPane.Dialog<String> showTextInput(String title, String text, boolean multiline) {
        return showTextInput(title, null, null, text, multiline);
    }

    public static DialogPane.Dialog<String> showTextInput(String title, String message, String text, boolean multiline) {
        return showTextInput(title, message, null, text, multiline);
    }

    public static DialogPane.Dialog<String> showTextInput(String title, String message, String prompt, String text, boolean multiline) {
        return dialogPane.showTextInput(title, message, prompt, text, multiline);
    }

    public static <T> void showDialog(DialogPane.Dialog<T> dialog) {
        dialogPane.showDialog(dialog);
    }
    public static <T> void hideDialog(DialogPane.Dialog<T> dialog) {
        dialogPane.hideDialog(dialog);
    }
}
