package ngsdiaglim.skins;


import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PasswordFieldSkin extends TextFieldWithButtonSkin {
    private static final char BULLET = '\u25cf';

    private boolean isMaskTextDisabled = false;

    public PasswordFieldSkin(TextField textField) {
        super(textField);
    }

    @Override
    protected void onRightButtonPressed() {
        TextField textField = getSkinnable();
        isMaskTextDisabled = true;
        textField.setText(textField.getText());
        isMaskTextDisabled = false;
    }

    @Override
    protected  void onRightButtonReleased()
    {
        TextField textField = getSkinnable();
        textField.setText(textField.getText());
        textField.end();
    }

    @Override protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField && !isMaskTextDisabled) {
            int n = txt.length();
            StringBuilder passwordBuilder = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                passwordBuilder.append(BULLET);
            }

            return passwordBuilder.toString();
        } else {
            return txt;
        }
    }
}
