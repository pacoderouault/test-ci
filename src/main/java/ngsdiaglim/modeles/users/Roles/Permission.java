package ngsdiaglim.modeles.users.Roles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Permission {

    private final PermissionsEnum permissionEnum;
    private final Set<Permission> children = new HashSet<>();


    public Permission(PermissionsEnum permissionEnum) {
        this.permissionEnum = permissionEnum;
    }

    public Permission(String permissionName) throws IllegalArgumentException {
        permissionEnum = PermissionsEnum.valueOf(permissionName);
    }

    public PermissionsEnum getPermissionEnum() { return permissionEnum; }

    public String getPermissionName() { return permissionEnum.getPermissionName(); }

    public Set<Permission> getChildren() { return children; }

    public void addPermission(Permission permission) {
        children.add(permission);
    }

    public void addPermissions(List<Permission> permissions) {
        children.addAll(permissions);
    }

    public void removePermission(Permission permission) {
        children.remove(permission);
    }

    public void removePermissions(List<Permission> permissions) {
        permissions.forEach(children::remove);
    }

    public String toString() {
        return getPermissionName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return permissionEnum == that.permissionEnum;
    }

    @Override
    public int hashCode() {
        return permissionEnum.hashCode();
    }
}
