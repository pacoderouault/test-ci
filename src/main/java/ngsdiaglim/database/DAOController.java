package ngsdiaglim.database;

import ngsdiaglim.database.dao.*;

public class DAOController {

    private static final DatabaseCreatorDAO databaseCreatorDAO = new DatabaseCreatorDAO();
    private static final UsersDAO usersDAO = new UsersDAO();
    private static final UserVariantTableColumnsDAO userVariantTableColumnsDAO = new UserVariantTableColumnsDAO();
    private static final RolesDAO rolesDAO = new RolesDAO();
    private static final UserRolesDAO userRolesDAO = new UserRolesDAO();
    private static final RolePermissionsDAO rolePermissionsDAO = new RolePermissionsDAO();
    private static final PanelDAO panelDAO = new PanelDAO();
    private static final PanelRegionDAO panelRegionDAO = new PanelRegionDAO();
    private static final AnalysisParametersDAO analysisParametersDAO = new AnalysisParametersDAO();
    private static final RunsDAO runsDAO = new RunsDAO();
    private static final RunFilesDAO runFilesDAO = new RunFilesDAO();
    private static final AnalysisDAO analysisDAO = new AnalysisDAO();
    private static final VariantsDAO variantsDAO = new VariantsDAO();
    private static final GeneSetDAO geneSetDAO = new GeneSetDAO();
    private static final GeneDAO geneDAO = new GeneDAO();
    private static final TranscriptsDAO transcriptsDAO = new TranscriptsDAO();
    private static final VariantAnalysisDAO variantAnalysisDAO = new VariantAnalysisDAO();
    private static final GenesPanelDAO genesPanelDAO = new GenesPanelDAO();
    private static final ColumnsExportDAO columnsExportDAO = new ColumnsExportDAO();
    private static final VariantPathogenicityDAO variantPathogenicityDAO = new VariantPathogenicityDAO();
    private static final VariantFalsePositiveDAO variantFalsePositiveDAO = new VariantFalsePositiveDAO();
    private static final SangerStateDAO sangerStateDAO = new SangerStateDAO();
    private static final VariantCommentaryDAO variantCommentaryDAO = new VariantCommentaryDAO();
    private static final AnnotationCommentaryDAO annotationCommentaryDAO = new AnnotationCommentaryDAO();
    private static final AnalysisCommentaryDAO analysisCommentaryDAO = new AnalysisCommentaryDAO();
    private static final AnalysisImagesDAO analysisImagesDAO = new AnalysisImagesDAO();
    private static final CNVControlGroupsDAO cnvControlGroupsDAO = new CNVControlGroupsDAO();
    private static final CNVControlsDAO cnvControlsDAO = new CNVControlsDAO();
    private static final RunsStatisticsDAO runsStatisticsDAO = new RunsStatisticsDAO();
    private static final HotspotsSetDAO hotspotsSetDAO = new HotspotsSetDAO();
    private static final HotspotDAO hotspotDAO = new HotspotDAO();
    private static final PrescriberDAO prescriberDAO = new PrescriberDAO();
    private static final ReportCommentaryDAO reportCommentaryDAO = new ReportCommentaryDAO();
    private static final ReportGeneCommentaryDAO reportGeneCommentaryDAO = new ReportGeneCommentaryDAO();
    private static final ReportMutationCommentaryDAO reportMutationCommentaryDAO = new ReportMutationCommentaryDAO();
    
    public static DatabaseCreatorDAO getDatabaseCreatorDAO() { return databaseCreatorDAO; }

    public static UsersDAO getUsersDAO() { return usersDAO; }

    public static UserVariantTableColumnsDAO getUserVariantTableColumnsDAO() {return userVariantTableColumnsDAO;}

    public static RolesDAO getRolesDAO() { return rolesDAO; }

    public static UserRolesDAO getUserRolesDAO() { return userRolesDAO; }

    public static RolePermissionsDAO getRolePermissionsDAO() { return rolePermissionsDAO; }

    public static PanelDAO getPanelDAO() {return panelDAO;}

    public static PanelRegionDAO getPanelRegionDAO() {return panelRegionDAO;}

    public static AnalysisParametersDAO getAnalysisParametersDAO() {return analysisParametersDAO;}

    public static RunsDAO getRunsDAO() {return runsDAO;}

    public static RunFilesDAO getRunFilesDAO() {return runFilesDAO;}

    public static AnalysisDAO getAnalysisDAO() {return analysisDAO;}

    public static VariantsDAO getVariantsDAO() {return variantsDAO;}

    public static GeneSetDAO getGeneSetDAO() {return geneSetDAO;}

    public static GeneDAO getGeneDAO() {return geneDAO;}

    public static TranscriptsDAO getTranscriptsDAO() {return transcriptsDAO;}

    public static VariantAnalysisDAO getVariantAnalysisDAO() {return variantAnalysisDAO;}

    public static GenesPanelDAO getGenesPanelDAO() {return genesPanelDAO;}

    public static ColumnsExportDAO getColumnsExportDAO() {return columnsExportDAO;}

    public static VariantPathogenicityDAO getVariantPathogenicityDAO() {return variantPathogenicityDAO;}

    public static VariantFalsePositiveDAO getVariantFalsePositiveDAO() {return variantFalsePositiveDAO;}

    public static SangerStateDAO getSangerStateDAO() {return sangerStateDAO;}

    public static VariantCommentaryDAO getVariantCommentaryDAO() {return variantCommentaryDAO;}

    public static AnnotationCommentaryDAO getAnnotationCommentaryDAO() {return annotationCommentaryDAO;}

    public static AnalysisCommentaryDAO getAnalysisCommentaryDAO() {return analysisCommentaryDAO;}

    public static AnalysisImagesDAO getAnalysisImagesDAO() {return analysisImagesDAO;}

    public static CNVControlGroupsDAO getCnvControlGroupsDAO() {return cnvControlGroupsDAO;}

    public static CNVControlsDAO getCnvControlsDAO() {return cnvControlsDAO;}

    public static RunsStatisticsDAO getRunsStatisticsDAO() {return runsStatisticsDAO;}

    public static HotspotsSetDAO getHotspotsSetDAO() {return hotspotsSetDAO;}

    public static HotspotDAO getHotspotDAO() {return hotspotDAO;}

    public static PrescriberDAO getPrescriberDAO() {return prescriberDAO;}

    public static ReportCommentaryDAO getReportCommentaryDAO() {return reportCommentaryDAO;}

    public static ReportGeneCommentaryDAO getReportGeneCommentaryDAO() {return reportGeneCommentaryDAO;}

    public static ReportMutationCommentaryDAO getReportMutationCommentaryDAO() {return reportMutationCommentaryDAO;}
}
