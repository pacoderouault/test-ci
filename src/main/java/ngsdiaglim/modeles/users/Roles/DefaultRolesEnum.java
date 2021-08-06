package ngsdiaglim.modeles.users.Roles;

import ngsdiaglim.App;

public enum DefaultRolesEnum {
    ADMIN(App.getBundle().getString("roles.default.admin")),
    GUEST(App.getBundle().getString("roles.default.guest"));

    private String roleName;

    DefaultRolesEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() { return roleName; }
}
