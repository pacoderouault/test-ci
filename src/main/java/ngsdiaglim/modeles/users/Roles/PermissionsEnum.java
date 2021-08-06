package ngsdiaglim.modeles.users.Roles;

import ngsdiaglim.App;

public enum PermissionsEnum {

    ROOT("Permissions"),
    USERS_MANAGEMENT(App.getBundle().getString("permissions.users_management")),
    MANAGE_ACCOUNT(App.getBundle().getString("permissions.users_management.manage_account")),
    MANAGE_ROLES(App.getBundle().getString("permissions.users_management.manage_roles")),
    EDIT_U(App.getBundle().getString("permissions.users_management")),
    RUNS_MANAGEMENT(App.getBundle().getString("permissions.runs_management")),
    ADD_RUN(App.getBundle().getString("permissions.runs_management.add_run")),
    EDIT_RUN(App.getBundle().getString("permissions.runs_management.edit_run")),
    REMOVE_RUN(App.getBundle().getString("permissions.runs_management.remove_run")),
    ADD_ANALYSE(App.getBundle().getString("permissions.runs_management.add_analyse")),
    EDIT_ANALYSE(App.getBundle().getString("permissions.runs_management.edit_analyse")),
    REMOVE_ANALYSE(App.getBundle().getString("permissions.runs_management.remove_analyse")),
    MANAGE_ANALYSISPARAMETERS(App.getBundle().getString("permissions.runs_management.manage_anlysis_parameters"))
    ;

    private final String permissionName;

    PermissionsEnum(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() { return permissionName; }
}
