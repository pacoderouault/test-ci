package ngsdiaglim.modeles.users.Roles;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    private static List<Permission> permissionsList;

    public static List<Permission> getPermissionsList() {
        if (permissionsList == null) {
            initPermissions();
        }
        return permissionsList;
    }

    private static void initPermissions() {

        permissionsList = new ArrayList<>();

        // Users management
        Permission usersManagementPermission = new Permission(PermissionsEnum.USERS_MANAGEMENT);
        usersManagementPermission.addPermission(new Permission(PermissionsEnum.MANAGE_ACCOUNT));
        usersManagementPermission.addPermission(new Permission(PermissionsEnum.MANAGE_ROLES));
        permissionsList.add(usersManagementPermission);

        //Runs Management
        Permission runsManagementPermission = new Permission(PermissionsEnum.RUNS_MANAGEMENT);
        runsManagementPermission.addPermission(new Permission(PermissionsEnum.ADD_RUN));
        runsManagementPermission.addPermission(new Permission(PermissionsEnum.EDIT_RUN));
        runsManagementPermission.addPermission(new Permission(PermissionsEnum.REMOVE_RUN));
        runsManagementPermission.addPermission(new Permission(PermissionsEnum.ADD_ANALYSE));
        runsManagementPermission.addPermission(new Permission(PermissionsEnum.EDIT_ANALYSE));
        runsManagementPermission.addPermission(new Permission(PermissionsEnum.REMOVE_ANALYSE));
        permissionsList.add(runsManagementPermission);

    }

}
