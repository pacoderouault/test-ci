package ngsdiaglim.skins;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.TextField;

public class TextFieldSkin extends TextFieldWithButtonSkin {
    public TextFieldSkin(TextField textField) {
        super(textField);

        textField.skinProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                textField.applyCss();
                textField.skinProperty().removeListener(this);
            }
        });
    }

    protected void onRightButtonPressed()
    {
        getSkinnable().setText("");
    }

}
