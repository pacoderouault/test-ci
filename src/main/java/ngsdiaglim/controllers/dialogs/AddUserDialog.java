package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Role;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;
import java.util.*;

public class AddUserDialog extends DialogPane.Dialog<AddUserDialog.UserCreation> {

    private final Logger logger = LogManager.getLogger(AddUserDialog.class);

    private final GridPane gridPane = new GridPane();
    private final TextField usernameTf = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField passwordFieldconfirm = new PasswordField();
    private final CheckComboBox<Role> rolesCb = new CheckComboBox<>();
    private final Label errorLabel = new Label();
    private final UserCreation userCreation = new UserCreation();

    public AddUserDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        setTitle(App.getBundle().getString("adduserdialog.title"));
        setContent(gridPane);
        initView();

        usernameTf.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        passwordField.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        passwordFieldconfirm.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());

        setValid(false);

    }


    private void changeFormEvent() {
        errorLabel.setText("");
        setValue(null);
        try {
            String error = checkErrorForm();
            if (error != null) {
                errorLabel.setText(error);
                setValid(false);
            }
            else {
                userCreation.setUsername(usernameTf.getText());
                userCreation.setPassword(passwordField.getText());
                userCreation.setRoles(rolesCb.getCheckModel().getCheckedItems());
                setValue(userCreation);
                setValid(true);
            }
        } catch (SQLException e) {
            logger.error("Error when checking form", e);
            errorLabel.setText(e.getMessage());
            setValid(false);
        }
    }


    private String checkErrorForm() throws SQLException {
        if (StringUtils.isBlank(usernameTf.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.emptyusername");
        }
        else if (StringUtils.isBlank(passwordField.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.emptypassword");
        }
        else if (!passwordField.getText().equals(passwordFieldconfirm.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.passwordsmatch");
        }
        else if (DAOController.getUsersDAO().userExists(usernameTf.getText())) {
            return App.getBundle().getString("adduserdialog.msg.err.usernamesexists");
        }
        return null;
    }


    private void initView() {
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        Label usernameLb = new Label(App.getBundle().getString("adduserdialog.lb.username"));
        Label passwordLb = new Label(App.getBundle().getString("adduserdialog.lb.password"));
        Label passwordConfirmLb = new Label(App.getBundle().getString("adduserdialog.lb.passwordconfirm"));
        Label groups = new Label(App.getBundle().getString("adduserdialog.lb.group"));
        errorLabel.getStyleClass().add("error-label");

        try {
            fillRoles();
            checkDefaultRole();
        } catch (SQLException e) {
            logger.error("Error when fill roles from db", e);
            Message.error(e.getMessage(), e);
        }

        int rowIdx = 0;
        gridPane.add(usernameLb, 0, ++rowIdx);
        gridPane.add(usernameTf, 1, rowIdx);
        gridPane.add(passwordLb, 0, ++rowIdx);
        gridPane.add(passwordField, 1, rowIdx);
        gridPane.add(passwordConfirmLb, 0, ++rowIdx);
        gridPane.add(passwordFieldconfirm, 1, rowIdx);
        gridPane.add(groups, 0, ++rowIdx);
        gridPane.add(rolesCb, 1, rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);

    }

    private void fillRoles() throws SQLException {
        rolesCb.getItems().addAll(DAOController.getRolesDAO().getRoles());
    }

    private void checkDefaultRole() {
        Optional<Role> guestRole = rolesCb.getItems().stream().filter(r -> r.getRoleName().equals(DefaultRolesEnum.GUEST.name())).findAny();
        guestRole.ifPresent(role -> rolesCb.getCheckModel().check(role));
    }

    public static class UserCreation {
        String username = "";
        String password = "";
        Set<Role> roles = new HashSet<>();

        public String getUsername() {return username;}

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {return password;}

        public void setPassword(String password) {
            this.password = password;
        }

        public Set<Role> getRoles() {return roles;}

        public void setRoles(List<Role> roles) {
            this.roles.clear();
            this.roles.addAll(roles);
        }
    }
}
