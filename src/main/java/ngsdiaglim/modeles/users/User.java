package ngsdiaglim.modeles.users;

import ngsdiaglim.modeles.users.Roles.Permission;
import ngsdiaglim.modeles.users.Roles.Role;

import java.util.Optional;
import java.util.Set;

public class User {

    private long id;
    private String username;
    private String password;
    private Set<Role> roles;

    public User(long id, String username, String password, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public long getId() { return id; }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() { return roles; }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean isPermitted(Permission permission) {
        return roles.parallelStream().anyMatch(r -> r.hashPermission(permission));
    }
}
