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
        permissionsList.add(runsManagementPermission);

        // Analysis Management
        Permission analysesManagementPermission = new Permission(PermissionsEnum.ANALYSIS_MANAGEMENT);
        analysesManagementPermission.addPermission(new Permission(PermissionsEnum.ADD_ANALYSE));
        analysesManagementPermission.addPermission(new Permission(PermissionsEnum.EDIT_ANALYSE));
        analysesManagementPermission.addPermission(new Permission(PermissionsEnum.REMOVE_ANALYSE));
        analysesManagementPermission.addPermission(new Permission(PermissionsEnum.CHANGE_ANALYSIS_STATE));
        analysesManagementPermission.addPermission(new Permission(PermissionsEnum.ADD_ANALYSIS_COMMENT));
        analysesManagementPermission.addPermission(new Permission(PermissionsEnum.IMPORT_ANALYSIS_IMAGES));
        permissionsList.add(analysesManagementPermission);


        // Manage analyis parameters
        Permission analysisParametersPermission = new Permission(PermissionsEnum.MANAGE_ANALYSISPARAMETERS);
        permissionsList.add(analysisParametersPermission);

        // Manage Gene Panels
        Permission genePanelsPermission = new Permission(PermissionsEnum.MANAGE_GENEPANELS);
        genePanelsPermission.addPermission(new Permission(PermissionsEnum.ADD_EDIT_GENEPANEL));
        genePanelsPermission.addPermission(new Permission(PermissionsEnum.REMOVE_GENEPANEL));
        permissionsList.add(genePanelsPermission);

        // Manage Variant pathogenicity
        Permission variantPathogenicity = new Permission(PermissionsEnum.MANAGE_VARIANT_PATHOGENICITY);
        variantPathogenicity.addPermission(new Permission(PermissionsEnum.EDIT_VARIANT_PATHOGENICITY));
        variantPathogenicity.addPermission(new Permission(PermissionsEnum.VALIDATE_VARIANT_PATHOGENICITY));
        variantPathogenicity.addPermission(new Permission(PermissionsEnum.VALIDATE_VARIANT_FALSE_POSITIVE));
        permissionsList.add(variantPathogenicity);

        // Manage Sanger Validation
        Permission sangerCheck = new Permission(PermissionsEnum.ADD_SANGER_CHECK);
        permissionsList.add(sangerCheck);

        // Manage Sanger Validation
        Permission cnvParameters = new Permission(PermissionsEnum.MANAGE_CNVS_PARAMETERS);
        permissionsList.add(cnvParameters);

        // Manage report
        Permission createReportsParameters = new Permission(PermissionsEnum.CREATE_REPORT);
        createReportsParameters.addPermission(new Permission(PermissionsEnum.CREATE_REPORT_COMMENT));
        createReportsParameters.addPermission(new Permission(PermissionsEnum.EDIT_REPORT_COMMENT));
        permissionsList.add(createReportsParameters);

        // CIQ
        Permission manageCIQ = new Permission(PermissionsEnum.MANAGE_CIQ);
        manageCIQ.addPermission(new Permission(PermissionsEnum.ADD_EDIT_CIQ));
        manageCIQ.addPermission(new Permission(PermissionsEnum.VALIDATE_CIQ));

    }

}
