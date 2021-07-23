package ngsdiaglim.modeles.users.Roles;

import java.util.Set;

public class Role {

    private long id;
    private String roleName;
    private Set<Permission> permissions;

    public Role(long id, String roleName, Set<Permission> permissions) {
        this.id = id;
        this.roleName = roleName;
        this.permissions = permissions;
    }

    public long getId() { return id; }

    public String getRoleName() { return roleName; }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Permission> getPermissions() { return permissions; }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hashPermission(Permission permission) {
        return permissions.contains(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return id == role.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
