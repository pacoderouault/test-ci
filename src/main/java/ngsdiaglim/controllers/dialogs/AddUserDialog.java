package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Role;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalComplexType;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AddUserDialog extends DialogPane.Dialog<AddUserDialog.UserCreation> {

    private final static Logger logger = LogManager.getLogger(AddUserDialog.class);

    private final GridPane gridPane = new GridPane();
    private final TextField usernameTf = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField passwordFieldconfirm = new PasswordField();
    private final CheckComboBox<Role> rolesCb = new CheckComboBox<>();
    private final Label errorLabel = new Label();
    private final UserCreation userCreation = new UserCreation();
    private final CheckBox setExpirationDateCb = new CheckBox(App.getBundle().getString("adduserdialog.lb.setExpirattionDate"));
    private final DatePicker expirationDatePicker = new DatePicker(LocalDate.now());

    ChangeListener<LocalDate> changeDateListener = (obs, oldV, newV) -> changeFormEvent();

    public AddUserDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        setTitle(App.getBundle().getString("adduserdialog.title"));
        setContent(gridPane);
        initView();

        usernameTf.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        passwordField.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        passwordFieldconfirm.textProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        rolesCb.getCheckModel().getCheckedItems().addListener((ListChangeListener<Role>) change -> changeFormEvent());
        setExpirationDateCb.selectedProperty().addListener((obs, oldV, newV) -> changeFormEvent());
        expirationDatePicker.valueProperty().addListener(changeDateListener);
        expirationDatePicker.disableProperty().bind(setExpirationDateCb.selectedProperty().not());

        expirationDatePicker.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            errorLabel.setText(null);
            if (isValidDate()) {
                // force the change of the datepicker value and the form validation
                expirationDatePicker.valueProperty().removeListener(changeDateListener);
                expirationDatePicker.setValue(expirationDatePicker.getConverter().fromString(expirationDatePicker.getEditor().getText()));
                changeFormEvent();
                expirationDatePicker.valueProperty().addListener(changeDateListener);
            } else {
                errorLabel.setText(App.getBundle().getString("adduserdialog.msg.err.expirationDateInvalidFormat"));
                setValid(false);
            }
        });

        setValue(userCreation);
        setValid(false);

    }


    private void changeFormEvent() {
        errorLabel.setText("");
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
                userCreation.setUseExpirationDate(setExpirationDateCb.isSelected());
                userCreation.setExpirationDate(expirationDatePicker.getValue());
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
        else if (rolesCb.getCheckModel().getCheckedItems().isEmpty()) {
            return App.getBundle().getString("adduserdialog.msg.err.emptyrole");
        } else if (setExpirationDateCb.isSelected() && expirationDatePicker.getValue() == null) {
            return App.getBundle().getString("adduserdialog.msg.err.setExpirationDate");
        } else if (setExpirationDateCb.isSelected() && expirationDatePicker.getValue().isBefore(LocalDate.now())) {
            return App.getBundle().getString("adduserdialog.msg.err.expirationDateNotValid");
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
        gridPane.add(setExpirationDateCb, 0, ++rowIdx);
        gridPane.add(expirationDatePicker, 0, ++rowIdx);
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
        private String username = "";
        private String password = "";
        private final Set<Role> roles = new HashSet<>();
        private boolean useExpirationDate = false;
        private LocalDate expirationDate = null;

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

        public boolean isUseExpirationDate() {return useExpirationDate;}

        public void setUseExpirationDate(boolean useExpirationDate) {
            this.useExpirationDate = useExpirationDate;
        }

        public LocalDate getExpirationDate() {return expirationDate;}

        public void setExpirationDate(LocalDate expirationDate) {
            this.expirationDate = expirationDate;
        }
    }

    private boolean isValidDate() {
        try {
            expirationDatePicker.getConverter().fromString(expirationDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
