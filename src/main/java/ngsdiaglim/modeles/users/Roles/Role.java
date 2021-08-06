package ngsdiaglim.modeles.users.Roles;

import java.util.Set;

public class Role {

    private final long id;
    private String roleName;
    private Set<Permission> permissions;
    private final boolean isEditable;

    public Role(long id, String roleName, Set<Permission> permissions, boolean isEditable) {
        this.id = id;
        this.roleName = roleName;
        this.permissions = permissions;
        this.isEditable = isEditable;
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

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean isEditable() {return isEditable;}

    @Override
    public String toString() {
        return roleName;
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
