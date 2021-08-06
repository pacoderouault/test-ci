package ngsdiaglim.modeles.users;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import ngsdiaglim.modeles.users.Roles.Permission;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.Roles.Role;

import java.time.LocalDate;
import java.util.List;

public class User {

    private final long id;
    private final SimpleStringProperty username = new SimpleStringProperty();
    private ObservableSet<Role> roles;
    private final LocalDate creationDate;
    private final SimpleBooleanProperty active = new SimpleBooleanProperty(true);
    private UserPreferences preferences = new UserPreferences();

    public User(long id, String username, ObservableSet<Role> roles, LocalDate creationDate) {
        this.id = id;
        this.username.setValue(username);
        this.roles = roles == null ? FXCollections.observableSet() : roles;
        this.creationDate = creationDate;
    }

    public User(long id, String username, ObservableSet<Role> roles, LocalDate creationDate, boolean isActive) {
        this(id, username, roles, creationDate);
        this.active.set(isActive);
    }

    public long getId() { return id; }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public ObservableSet<Role> getRoles() { return roles; }

    public void setRoles(ObservableSet<Role> roles) {
        this.roles = roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean isPermitted(Permission permission) {
        return roles.parallelStream().anyMatch(r -> r.hasPermission(permission));
    }

    public boolean isPermitted(PermissionsEnum permissionenum) {
        return roles.parallelStream().anyMatch(r -> r.hasPermission(new Permission(permissionenum)));
    }

    public LocalDate getCreationDate() { return creationDate; }

    public boolean isActive() {
        return active.get();
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public UserPreferences getPreferences() { return preferences; }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
