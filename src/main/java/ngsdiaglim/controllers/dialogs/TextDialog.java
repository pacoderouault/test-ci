package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import com.dlsc.gemsfx.ResizableTextArea;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class TextDialog extends DialogPane.Dialog<String> {

    public TextDialog(DialogPane pane, String title, String message, String prompt, String text, boolean multiline) {
        super(pane, DialogPane.Type.INFORMATION);

        TextInputControl textInputControl;
        Node node;

        if (multiline) {
            ResizableTextArea textArea = new ResizableTextArea(text);
            textArea.getEditor().setPromptText(prompt);
            textArea.getEditor().setWrapText(true);
            textArea.getEditor().setPrefRowCount(6);
            textArea.setResizeVertical(true);
            textArea.setResizeHorizontal(true);
//            textArea.getEditor().skinProperty().addListener(it -> Platform.runLater(() -> textArea.getEditor().requestFocus()));
            textInputControl = textArea.getEditor();
            node = textArea;
        } else {
            TextField textField = new TextField(text);
            textField.setPromptText(prompt);
            textField.setPrefColumnCount(20);
//            textField.skinProperty().addListener(it -> Platform.runLater(() -> textField.requestFocus()));
            textInputControl = textField;
            node = textField;
        }

        VBox box = new VBox();
        box.getStyleClass().add("prompt-node-wrapper");

        if (StringUtils.isNotBlank(message)) {
            Label promptLabel = new Label(message);
            box.getChildren().add(promptLabel);
        }

        box.getChildren().add(node);

        textInputControl.setEditable(false);

        setTitle(title);
        setContent(box);
    }
}
