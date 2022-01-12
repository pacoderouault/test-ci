package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddUserDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.dialogs.ResetPasswordDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.*;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class UsersManageController extends Module {

    private final Logger logger = LogManager.getLogger(UsersManageController.class);

    @FXML private Button addNewUserBtn;
    @FXML private Button addNewGroupBtn;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> creationDateCol;
    @FXML private TableColumn<User, Set<Role>> groupsCol;
    @FXML private TableColumn<User, Boolean> activeCol;
    @FXML private TableColumn<User, Void> actionsCol;
    @FXML private TableView<Role> rolesTable;
    @FXML private TableColumn<Role, String> roleNameCol;
    @FXML private TableColumn<Role, Void> roleActionsCol;
    @FXML private TreeView<Permission> permissionsTreeView;


    private final Tooltip resetPasswordTooltip = new Tooltip(App.getBundle().getString("usermanage.tp.resetPassword"));
    private final Tooltip deleteRoleTooltip = new Tooltip(App.getBundle().getString("usermanage.tp.deleteRole"));

    public UsersManageController() {
        super(App.getBundle().getString("usersmanage.lb.title"));
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/UsersManage.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Problem when loading the manage user panel", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {
        try {
            initUserTable();
            fillUsersTable();

            initRolesTable();
            fillRolesTable();

            initPermissionsTreeView();

            resetPasswordTooltip.setShowDelay(Duration.ZERO);
            deleteRoleTooltip.setShowDelay(Duration.ZERO);
        } catch (SQLException e) {
            logger.error("Error when initializing use table", e);
            Message.error(e.getMessage(), e);
        }

        addNewGroupBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES));
        addNewUserBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.USERS_MANAGEMENT));
    }

    private void initUserTable() throws SQLException {
        userTable.setEditable(true);
        userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userTable.getSelectionModel().setCellSelectionEnabled(true);

        nameCol.setCellValueFactory(data -> data.getValue().usernameProperty());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
                User user = t.getTableView().getItems().get(t.getTablePosition().getRow());
                try {
                    String error = checkUsername(user, t.getNewValue());
                    if (error != null) {
                        Message.error(error);
                    } else {
                        user.setUsername(t.getNewValue());
                        if (App.get().getLoggedUser().equals(user)) {
                            App.get().getLoggedUser().setUsername(t.getNewValue());
                        }
                        DAOController.getUsersDAO().updateUsername(user);
                    }
                } catch (SQLException e) {
                    user.setUsername(t.getOldValue());
                    if (App.get().getLoggedUser().equals(user)) {
                        App.get().getLoggedUser().setUsername(t.getOldValue());
                    }
                    logger.error("Error when editing user name", e);
                    Message.error(e.getMessage(), e);
                }
            }
            userTable.refresh();
        });

        creationDateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        groupsCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getRoles()));
        groupsCol.setCellFactory((tablecolumn) -> new TableCell<>() {

            private final CheckComboBox<Role> cb = new CheckComboBox<>();

            private final ListChangeListener<Role> listener = c -> {
                User user = getTableView().getItems().get(getIndex());
                while (c.next()) {
                    for (Role remitem : c.getRemoved()) {
                        if (remitem.getRoleName().equals(DefaultRolesEnum.ADMIN.name())) {
                            try {
                                if (DAOController.getUsersDAO().isLastAdmin(user.getUsername())) {
                                    Message.error(App.getBundle().getString("usersmanage.msg.err.islastadmin"));
                                    continue;
                                }
                            }catch (SQLException e) {
                                logger.error("Error when checking last admin", e);
                                Message.error(e.getMessage(), e);
                            }
                        }

                        try {
                            DAOController.getUserRolesDAO().removeUserRole(user.getId(), remitem);
                            user.getRoles().remove(remitem);
                        } catch (SQLException e) {
                            logger.error("Error when editing user role", e);
                            Message.error(e.getMessage(), e);
                        }

                    }
                    for (Role additem : c.getAddedSubList()) {
                        if (!user.hasRole(additem)) {
                            try {
                                DAOController.getUserRolesDAO().addUserRole(user.getId(), additem);
                                user.getRoles().add(additem);
                            } catch (SQLException e) {
                                logger.error("Error when editing user role", e);
                                Message.error(e.getMessage(), e);
                            }
                        }
                    }

                }
            };



            @Override
            protected void updateItem(Set<Role> item, boolean empty) {
                super.updateItem(item, empty);

                setText(null);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
//                    User user = getTableView().getItems().get(getIndex());
                    try {
                        cb.getItems().setAll(DAOController.getRolesDAO().getRoles());
                        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
                            cb.setDisable(true);
                        }

                    } catch (SQLException e) {
                        logger.error("Error when gettings roles from db", e);
                    }

                    for (Role r : item) {
                        if (item.contains(r)) {
                            cb.getCheckModel().check(r);
                        }
                    }
                    cb.getCheckModel().getCheckedItems().addListener(listener);
                    setGraphic(cb);
                }
                setText(null);
            }
        });

        activeCol.setCellValueFactory(data -> data.getValue().activeProperty());
        activeCol.setCellFactory(p -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<User, Boolean> tableCell = new TableCell<>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (empty || item == null)
                        setGraphic(null);
                    else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
                            checkBox.setDisable(true);
                        }
                    }
                }
            };
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validateUserActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });

        actionsCol.setCellFactory((tableColumn) -> new TableCell<>() {
            private final HBox box = new HBox();
            private final Button resetPassword = new Button("", new FontIcon("mdal-autorenew"));

            {
                box.getStyleClass().add("box-action-cell");
                resetPassword.getStyleClass().add("button-action-cell");
                resetPassword.setTooltip(resetPasswordTooltip);
                resetPassword.setOnAction(e -> {
                    if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
                        User user = getTableRow().getItem();
                        DialogPane.Dialog<String> dialog = new ResetPasswordDialog(user);
                        Message.showDialog(dialog);
                        dialog.getButton(ButtonType.OK).setOnAction(event -> {
                            if (dialog.isValid() && dialog.getValue() != null) {
                                try {
                                    DAOController.getUsersDAO().updatePassword(user, dialog.getValue());
                                } catch (SQLException ex) {
                                    logger.error("Error when updating password", ex);
                                    Message.error(ex.getMessage(), ex);
                                }
                            }
                        });
                    }
                });
                if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
                    resetPassword.setDisable(true);
                }

                box.getChildren().addAll(resetPassword);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(box);
                }
            }
        });

    }

    private void validateUserActivation(CheckBox checkBox, User user, Event event) {
        event.consume();
        Object[] messageArguments = {user.getUsername()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("usermanage.msg.confinactive", messageArguments);
        }
        else {
            message = BundleFormatter.format("usermanage.msg.confactive", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                if(DAOController.getUsersDAO().isLastAdmin(user.getUsername())) {
                    Message.error(App.getBundle().getString("usersmanage.msg.err.inactivelastadmin"));
                }
                else {
                    if (checkBox.isSelected()) {
                        DAOController.getUsersDAO().inactiveUser(user.getId());
                    }
                    else {
                        DAOController.getUsersDAO().activeUser(user.getId());
                    }
                    user.setActive(!checkBox.isSelected());
                    checkBox.setSelected(!checkBox.isSelected());
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            finally {
                Message.hideDialog(d);
            }

        });
    }

    private void fillUsersTable() throws SQLException {
        userTable.setItems(DAOController.getUsersDAO().getUsers());
    }

    private String checkUsername(User user, String username) {
        if (StringUtils.isBlank(username)) {
            return App.getBundle().getString("adduserdialog.msg.err.emptyusername");
        }
        else {
            try {
                User u = DAOController.getUsersDAO().getUser(username);
                if (u != null && !user.equals(u)) {
                    return App.getBundle().getString("adduserdialog.msg.err.usernamesexists");
                }
            } catch (SQLException e) {
                logger.error("Error when getting user", e);
                return e.getMessage();
            }
        }
        return null;
    }


    @FXML
    private void AddNewUser() {
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
            return;
        }
        AddUserDialog dialog = new AddUserDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (dialog.isValid() && dialog.getValue() != null) {
                try {
                    DAOController.getUsersDAO().addUser(dialog.getValue().getUsername(), dialog.getValue().getPassword(), dialog.getValue().getRoles());
                    fillUsersTable();
                    Message.hideDialog(dialog);
                } catch (SQLException ex) {
                    logger.error("Error when adding user", ex);
                    Message.error(ex.getMessage(), ex);
                }
            }

        });
    }



    private void initRolesTable() {

        rolesTable.setEditable(true);
        rolesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
//            if (newV != null) {
                fillPermissions();
//            }
        });
        roleNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoleName()));
        roleNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        roleNameCol.setOnEditCommit(t -> {
            Role role = t.getRowValue();
            if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES) && role.isEditable()) {

                try {
                    String error = checkRoleName(t.getNewValue());
                    if (error != null) {
                        Message.error(error);
                    } else {
                        DAOController.getRolesDAO().renameRole(role, t.getNewValue());
                        role.setRoleName(t.getNewValue());
                    }
                } catch (SQLException e) {
                    role.setRoleName(t.getOldValue());
                    logger.error("Error when editing role name", e);
                    Message.error(e.getMessage(), e);
                }
                userTable.refresh();
            }
        });

        roleActionsCol.setCellFactory((tableColumn) -> new TableCell<>() {
            private final HBox box = new HBox();
            private final Button deleteRoleBtn = new Button("", new FontIcon("mdal-delete_forever"));

            {
                box.getStyleClass().add("box-action-cell");
                deleteRoleBtn.getStyleClass().add("button-action-cell");
                deleteRoleBtn.setTooltip(deleteRoleTooltip);
                deleteRoleBtn.setOnAction(e -> {
                    Role role = getTableRow().getItem();
                    if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES)) {
                        Object[] arguments = {role.getRoleName()};

                        DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("usermanage.roles.msg.conf.deleterole", arguments));
                        dialog.getButton(ButtonType.YES).setOnAction(event -> {
                            try {
                                DAOController.getRolesDAO().deleteRole(role);
                                rolesTable.getItems().remove(role);
                                userTable.refresh();
                                Message.hideDialog(dialog);
                            } catch (SQLException ex) {
                                logger.error("Error when deleting role", ex);
                                Message.error(ex.getMessage(), ex);
                            }
                        });
                    }
                });

                box.getChildren().addAll(deleteRoleBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || getTableRow().getItem() == null) {
                    this.setGraphic(null);
                } else {
                    deleteRoleBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES) || !getTableRow().getItem().isEditable());
                    this.setGraphic(box);
                }
            }
        });
    }

    private String checkRoleName(String roleName) {
        try {
            if (StringUtils.isBlank(roleName)) {
                return App.getBundle().getString("usermanage.roles.err.emptyroleName");
            }
            if (DAOController.getRolesDAO().roleExists(roleName)) {
                return App.getBundle().getString("usermanage.roles.err.roleexists");
            }
        } catch (SQLException e) {
            logger.error("Error when checking role exists", e);
            return e.getMessage();
        }
        return null;
    }


    private void fillRolesTable() {
        try {
            rolesTable.getItems().setAll(DAOController.getRolesDAO().getRoles());
        } catch (SQLException e) {
            logger.error("Error when getting roles from db", e);
            Message.error(e.getMessage(), e);
        }
    }


    private void initPermissionsTreeView() {
        permissionsTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        permissionsTreeView.setShowRoot(false);
    }

    private void fillPermissions() {
        permissionsTreeView.setDisable(
                rolesTable.getSelectionModel().getSelectedItem() == null
                || !rolesTable.getSelectionModel().getSelectedItem().isEditable()
                || !App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES));

        CheckBoxTreeItem<Permission> rootItem = new CheckBoxTreeItem<>(new Permission(PermissionsEnum.ROOT));
        addpermissions(Permissions.getPermissionsList(), rootItem);
        rootItem.setExpanded(true);
        permissionsTreeView.setRoot(rootItem);
    }

    private void addpermissions(List<Permission> permissions, CheckBoxTreeItem<Permission> parent) {
        Role selectedRole = rolesTable.getSelectionModel().getSelectedItem();
        for (Permission p : permissions) {
            CheckBoxTreeItem<Permission> treeItem = new CheckBoxTreeItem<>(p);
            if (selectedRole != null) {
                treeItem.setSelected(selectedRole.hasPermission(p));
                treeItem.selectedProperty().addListener((obs, oldV, newV) -> {
                    if (newV) {
                        try {
                            DAOController.getRolePermissionsDAO().addRolePermission(selectedRole.getId(), p);
                            selectedRole.getPermissions().add(p);
                            permissionsTreeView.refresh();
                        } catch (SQLException e) {
                            logger.error("Error when adding permission to group", e);
                            Message.error(e.getMessage(), e);
                        }
                    }
                    else {
                        try {
                            DAOController.getRolePermissionsDAO().removeRolePermission(selectedRole.getId(), p);
                            selectedRole.getPermissions().remove(p);
                            permissionsTreeView.refresh();
                        } catch (SQLException e) {
                            logger.error("Error when removing permission to group", e);
                            Message.error(e.getMessage(), e);
                        }
                    }
                });
            }

            treeItem.setExpanded(true);
            parent.getChildren().add(treeItem);
            addpermissions(List.copyOf(p.getChildren()), treeItem);
        }
    }


    @FXML
    private void addNewGroup() {
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
            return;
        }
        DialogPane.Dialog<String> dialog = Message.showTextInput("Ajouter un group", "");
        dialog.getButton(ButtonType.OK).setOnAction(event -> {
            String groupName = dialog.getValue();
            try {
                String error = checkRoleName(groupName);
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.getRolesDAO().addRole(groupName);
                    fillRolesTable();
                    userTable.refresh();
                    Message.hideDialog(dialog);
                }
            } catch (SQLException e) {
                logger.error("Error when adding new group", e);
                Message.error(e.getMessage(), e);
            }
        });
    }



}
