package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class ChangeUsernameDialog extends DialogPane.Dialog<ChangeUsernameDialog.ChangeUsernameData> {

    private final static Logger logger = LogManager.getLogger(ChangeUsernameDialog.class);

    private final GridPane gridPane = new GridPane();
    private final TextField userNameTf = new TextField();
    private final PasswordField passwordFieldconfirm = new PasswordField();
    private final Label usernameLb = new Label(App.getBundle().getString("changeusernamedialog.lb.newUsername"));
    private final Label passwordLb = new Label(App.getBundle().getString("changeusernamedialog.lb.password"));
    private final Label errorLabel = new Label();

    public ChangeUsernameDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        setTitle(App.getBundle().getString("changeusernamedialog.title"));
        setContent(gridPane);
        setValue(new ChangeUsernameData());
        initView();

        userNameTf.textProperty().bindBidirectional(getValue().newUsernameProperty());
        passwordFieldconfirm.textProperty().bindBidirectional(getValue().passwordProperty());
        userNameTf.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        setValid(false);
    }

    private void initView() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(usernameLb, 0, ++rowIdx);
        gridPane.add(userNameTf, 1, rowIdx);
        gridPane.add(passwordLb, 0, ++rowIdx);
        gridPane.add(passwordFieldconfirm, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);
    }

    private String checkErrorForm() throws SQLException {
        if (StringUtils.isBlank(userNameTf.getText())) {
            return App.getBundle().getString("changeusernamedialog.msg.err.emptyusername");
        } else if (userNameTf.getText().equalsIgnoreCase(App.get().getLoggedUser().getUsername())) {
            return App.getBundle().getString("changeusernamedialog.msg.err.usernameequals");
        }  else if (DAOController.getUsersDAO().userExists(userNameTf.getText())) {
            return App.getBundle().getString("changeusernamedialog.msg.err.usernameexists");
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
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    public static class ChangeUsernameData {
        private final SimpleStringProperty newUsername = new SimpleStringProperty();
        private final SimpleStringProperty password = new SimpleStringProperty();

        public String getNewUsername() {
            return newUsername.get();
        }

        public SimpleStringProperty newUsernameProperty() {
            return newUsername;
        }

        public void setNewUsername(String newUsername) {
            this.newUsername.set(newUsername);
        }

        public String getPassword() {
            return password.get();
        }

        public SimpleStringProperty passwordProperty() {
            return password;
        }

        public void setPassword(String password) {
            this.password.set(password);
        }
    }
}
