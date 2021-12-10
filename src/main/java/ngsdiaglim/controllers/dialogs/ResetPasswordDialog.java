package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class ResetPasswordDialog extends DialogPane.Dialog<String> {

    private final Logger logger = LogManager.getLogger(ResetPasswordDialog.class);

    private final GridPane gridPane = new GridPane();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField passwordFieldconfirm = new PasswordField();
    private final Label errorLabel = new Label();

    public ResetPasswordDialog(User user) {

        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INPUT);
        Object[] arguments = {user.getUsername()};
        setTitle(BundleFormatter.format("resetpassworddialog.title", arguments));
        setContent(gridPane);
        initView();

        passwordField.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        passwordFieldconfirm.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());

        setValid(false);

    }

    private void initView() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        Label passwordLb = new Label(App.getBundle().getString("adduserdialog.lb.password"));
        Label passwordConfirmLb = new Label(App.getBundle().getString("adduserdialog.lb.passwordconfirm"));
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(passwordLb, 0, ++rowIdx);
        gridPane.add(passwordField, 1, rowIdx);
        gridPane.add(passwordConfirmLb, 0, ++rowIdx);
        gridPane.add(passwordFieldconfirm, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);

    }

    private String checkErrorForm() {
        if (StringUtils.isBlank(passwordField.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.emptypassword");
        }
        else if (!passwordField.getText().equals(passwordFieldconfirm.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.passwordsmatch");
        }
        return null;
    }

    private void changeFormEvent() {
        errorLabel.setText("");
        setValue(null);

        String error = checkErrorForm();
        if (error != null) {
            errorLabel.setText(error);
            setValid(false);
        }
        else {
            setValue(passwordField.getText());
            setValid(true);
        }
    }


}
