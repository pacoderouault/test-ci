package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import ngsdiaglim.App;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class ChangePasswordDialog extends DialogPane.Dialog<ChangePasswordDialog.ChangePasswordData> {

    private final static Logger logger = LogManager.getLogger(ChangePasswordDialog.class);

    private final GridPane gridPane = new GridPane();
    private final PasswordField newPasswordTf = new PasswordField();
    private final PasswordField newPasswordConfirmTf = new PasswordField();
    private final PasswordField actualPasswordTf = new PasswordField();
    private final Label newPasswordLb = new Label(App.getBundle().getString("changepassworddialog.lb.newpassword"));
    private final Label confirmPawwordLb = new Label(App.getBundle().getString("changepassworddialog.lb.confirmpassword"));
    private final Label actualPasswordLb = new Label(App.getBundle().getString("changepassworddialog.lb.actualpassword"));
    private final Label errorLabel = new Label();

    public ChangePasswordDialog(DialogPane pane) {

        super(pane, DialogPane.Type.INPUT);
        setTitle(App.getBundle().getString("changepassworddialog.title"));
        setContent(gridPane);
        setValue(new ChangePasswordData());
        initView();

        newPasswordTf.textProperty().bindBidirectional(getValue().newPasswordProperty());
        actualPasswordTf.textProperty().bindBidirectional(getValue().oldPasswordProperty());

        newPasswordTf.textProperty().addListener((observable -> changeFormEvent()));
        newPasswordConfirmTf.textProperty().addListener((observable -> changeFormEvent()));
        actualPasswordTf.textProperty().addListener((observable -> changeFormEvent()));
        setValid(false);
    }


    private void initView() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(newPasswordLb, 0, ++rowIdx);
        gridPane.add(newPasswordTf, 1, rowIdx);
        gridPane.add(confirmPawwordLb, 0, ++rowIdx);
        gridPane.add(newPasswordConfirmTf, 1, rowIdx);

        ++rowIdx;

        gridPane.add(actualPasswordLb, 0, ++rowIdx);
        gridPane.add(actualPasswordTf, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);

        for (int i = 0; i < rowIdx; i++) {
            gridPane.getRowConstraints().add(i, new RowConstraints(30));
        }
    }

    private String checkErrorForm() throws SQLException {
        if (StringUtils.isBlank(newPasswordTf.getText())) {
            return App.getBundle().getString("changepassworddialog.msg.err.emptypassword");
        } else if (!newPasswordTf.getText().equals(newPasswordConfirmTf.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.passwordsmatch");
        } else if (StringUtils.isBlank(actualPasswordTf.getText())) {
            return App.getBundle().getString("changepassworddialog.msg.err.emptyactualpassword");
        }
        return null;
    }

    private void changeFormEvent() {
        errorLabel.setText("");
        try {
            String error = checkErrorForm();
            if (error != null) {
                errorLabel.setText(error);
                setValid(false);
            } else {
                setValid(true);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage(), e);
        }
    }

    public static class ChangePasswordData {
        private final SimpleStringProperty newPassword = new SimpleStringProperty();
        private final SimpleStringProperty oldPassword = new SimpleStringProperty();

        public String getNewPassword() {
            return newPassword.get();
        }

        public SimpleStringProperty newPasswordProperty() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword.set(newPassword);
        }

        public String getOldPassword() {
            return oldPassword.get();
        }

        public SimpleStringProperty oldPasswordProperty() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword.set(oldPassword);
        }
    }
}
