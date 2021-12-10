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
    ANALYSIS_MANAGEMENT(App.getBundle().getString("permissions.analyses_management")),
    ADD_ANALYSE(App.getBundle().getString("permissions.runs_management.add_analyse")),
    EDIT_ANALYSE(App.getBundle().getString("permissions.runs_management.edit_analyse")),
    REMOVE_ANALYSE(App.getBundle().getString("permissions.runs_management.remove_analyse")),
    CHANGE_ANALYSIS_STATE(App.getBundle().getString("permissions.runs_management.changeanalysisstate")),
    ADD_ANALYSIS_COMMENT(App.getBundle().getString("permissions.runs_management.add_analysis_comment")),
    IMPORT_ANALYSIS_IMAGES(App.getBundle().getString("permissions.runs_management.import_analysis_images")),
    MANAGE_ANALYSISPARAMETERS(App.getBundle().getString("permissions.runs_management.manage_anlysis_parameters")),
    MANAGE_GENEPANELS(App.getBundle().getString("permissions.runs_management.manage_genepanels")),
    ADD_EDIT_GENEPANEL(App.getBundle().getString("permissions.runs_management.add_edit_genepanels")),
    REMOVE_GENEPANEL(App.getBundle().getString("permissions.runs_management.delete_genepanels")),
    MANAGE_VARIANT_PATHOGENICITY(App.getBundle().getString("permissions.runs_management.manage_variant_pathogenicity")),
    EDIT_VARIANT_PATHOGENICITY(App.getBundle().getString("permissions.runs_management.edit_variant_pathogenicity")),
    VALIDATE_VARIANT_PATHOGENICITY(App.getBundle().getString("permissions.runs_management.validate_variant_pathogenicity")),
    VALIDATE_VARIANT_FALSE_POSITIVE(App.getBundle().getString("permissions.runs_management.validate_variant_falsepositive")),
    ADD_SANGER_CHECK(App.getBundle().getString("permissions.runs_management.add_sanger_state")),
    MANAGE_CNVS_PARAMETERS(App.getBundle().getString("permissions.runs_management.managecnvparmeters")),
    CREATE_REPORT(App.getBundle().getString("permissions.runs_management.createReports")),
    CREATE_REPORT_COMMENT(App.getBundle().getString("permissions.runs_management.createReportComments")),
    EDIT_REPORT_COMMENT(App.getBundle().getString("permissions.runs_management.editReportComments"))
    ;

    private final String permissionName;

    PermissionsEnum(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() { return permissionName; }
}
