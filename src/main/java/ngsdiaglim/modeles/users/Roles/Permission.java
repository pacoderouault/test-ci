package ngsdiaglim.modeles.users.Roles;

public class Permission {

    private long id;
    private String permissionName;

    public Permission(long id, String permissionName) {
        this.id = id;
        this.permissionName = permissionName;
    }

    public long getId() { return id; }

    public String getPermissionName() { return permissionName; }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
