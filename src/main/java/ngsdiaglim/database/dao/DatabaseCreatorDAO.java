package ngsdiaglim.database.dao;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.DatabaseConnection;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Permission;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.Roles.Role;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DatabaseCreatorDAO extends DAO {

    public DatabaseCreatorDAO() {
    }

    public boolean exists() {
        File f = new File(DatabaseConnection.instance().getDatasource().getJDBC_PATH());
        return f.exists();
    }

    public void createTables() throws SQLException {
        createUsersTable();
        createUsersVariantTableColumnTable();
        createColumnExportTable();
        createRolesTable();
        createUserRolesTable();
        createRolePermissionsTable();
        createDefaultRoles();
        createAdminUser();
        createPanelTable();
        createPanelRegionTable();
        createGeneSetTable();
        createGenesTable();
        createTranscriptsTable();
        createGenesPanelTable();
        createHotspotsSetTable();
        createHotspotsTable();
        createAnalysisParametersTable();
        createRunTable();
        createRunFilesTable();
        createAnalysisTable();
        createVariantTable();
        createVariantCommentTable();
        createVariantHistoryTable();
        createVariantAnalysisesTable();
        createVariantPathogenicityTable();
        createVariantFalsePositiveTable();
        createSangerStateTable();
        createVariantCommentaryTable();
        createAnnotationCommentaryTable();
        createAnalysisCommentaryTable();
        createAdditionalImagesTable();
        createCNVControlGroupTable();
        createCNVControlTable();
        createPrescribersTable();
        createReportGeneCommentary();
        createReportMutationCommentary();
        createReportOtherCommentary();
        createCIQModelTable();
        createCIQHotspotTable();
        createCIQAnalysisTable();
        createCIQRecordTable();
        createCIQRecordHistoryTable();
    }

    private void createUsersTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS users ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "username VARCHAR_IGNORECASE(255), " +
                "password VARCHAR(255), " +
                "is_active BOOLEAN DEFAULT True, " +
                "creation_date DATE, " +
                "preferences VARCHAR, " +
                "UNIQUE (username));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createUsersVariantTableColumnTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS usersVariantTableColumnTable ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "user_id INT, " +
                "columns_order VARCHAR, " +
                "columns_visibility VARCHAR, " +
                "columns_size VARCHAR, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    private void createColumnExportTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS columnsExport ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "user_id INT, " +
                "columns VARCHAR(MAX), " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);";

        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }

    }



    private void createDefaultRoles() throws SQLException {

        // Admin role
        if (DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name()) == null) {
            final String sql = "INSERT INTO roles (role_name, editable) VALUES (?, False);";
            try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stm.setString(1, DefaultRolesEnum.ADMIN.name());
                stm.executeUpdate();
                ResultSet generatedKeys = stm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long role_id = generatedKeys.getLong(1);

                    for (PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
                        Permission permission = new Permission(permissionsEnum);
                        DAOController.getRolePermissionsDAO().addRolePermission(role_id, permission);
                    }
                } else {
                    throw new SQLException("No user inserted in the database");
                }
            }
        }

        // Guest role
        if (DAOController.getRolesDAO().getRole(DefaultRolesEnum.GUEST.name()) == null) {
            final String sql = "INSERT INTO roles (role_name, editable) VALUES (?, False);";
            try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setString(1, DefaultRolesEnum.GUEST.name());
                stm.executeUpdate();
            }
        }
    }


    private void createAdminUser() throws SQLException {
        // check if user table is empty
        final String sql = "SELECT id FROM users;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            if (!rs.next()) {
                Role adminRole = DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name());
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(adminRole);
                DAOController.getUsersDAO().addUser("admin", "admin", userRoles);
            }
        }

    }


    private void createRolesTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS roles ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "role_name VARCHAR_IGNORECASE(255) NOT NULL, " +
                "editable BOOLEAN DEFAULT True, " +
                "UNIQUE (role_name));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

//    /**
//     * Create 2 default roles
//     * Admin and Guest
//     * @throws SQLException
//     */
//    private void createDefaultRoles() throws SQLException {
//        final String sql_admin = "MERGE INTO roles " +
//                "KEY(id) " +
//                "VALUES (0, " + DefaultRolesEnum.ADMIN.name() + "), (1, False);";
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql_admin)) {
//            stm.executeUpdate();
//        }
//
//        final String sql_guest = "MERGE INTO roles " +
//                "KEY(id) " +
//                "VALUES (0, " + DefaultRolesEnum.GUEST.name() + "), (1, False);";
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql_guest)) {
//            stm.executeUpdate();
//        }
//    }

    private void createUserRolesTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS userRoles ( " +
                "user_id INT NOT NULL, " +
                "role_id INT NOT NULL, " +
                "PRIMARY KEY(user_id, role_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createRolePermissionsTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS rolePermissions ( " +
                "role_id INT NOT NULL, " +
                "permission_name VARCHAR(255) NOT NULL, " +
                "PRIMARY KEY (role_id, permission_name), " +
                "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    public void setupAdminPermissions() throws SQLException {
        // Admin role
        Role adminRole = DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name());
        if (adminRole != null) {
            for (PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
                Permission permission = new Permission(permissionsEnum);
                if (!adminRole.hasPermission(permission)) {
                    DAOController.getRolePermissionsDAO().addRolePermission(adminRole.getId(), permission);
                }
            }
        }
    }


    private void createAnalysisParametersTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS analysisParameters ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR_IGNORECASE(255), " +
                "genome VARCHAR(255), " +
                "min_depth INT, " +
                "warning_depth INT, " +
                "min_vaf FLOAT, " +
                "is_active BOOLEAN DEFAULT True, " +
                "panel_id INT, " +
                "geneSet_id INT, " +
                "hotspotsSet_id INT, " +
                "target_enrichment VARCHAR(255), " +
                "UNIQUE (name), " +
                "FOREIGN KEY (panel_id) REFERENCES panels(id), " +
                "FOREIGN KEY (hotspotsSet_id) REFERENCES hotspotsSet(id), " +
                "FOREIGN KEY (geneSet_id) REFERENCES geneSet(id));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createPanelTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS panels ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR_IGNORECASE(255), " +
                "is_active BOOLEAN DEFAULT True, " +
                "bed_path VARCHAR, " +
                "UNIQUE (name));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createPanelRegionTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS panelRegions ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "contig VARCHAR(255), " +
                "start INT, " +
                "end_ INT, " +
                "name VARCHAR_IGNORECASE(255), " +
                "panel_id INT, " +
                "pool VARCHAR(50), " +
                "FOREIGN KEY (panel_id) REFERENCES panels(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createGeneSetTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS geneSet ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR_IGNORECASE(255), " +
                "is_active BOOLEAN DEFAULT True, " +
                "UNIQUE (name));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

//    public void createGeneTranscriptsSetTable() throws SQLException {
//        final String sql = "CREATE TABLE IF NOT EXISTS geneTranscriptSet ( " +
//                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
//                "name VARCHAR_IGNORECASE(255), " +
//                "is_active BOOLEAN DEFAULT True, " +
//                "UNIQUE (name));";
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
//            stm.executeUpdate();
//        }
//    }

    private void createGenesTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS genes ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "geneset_id INT NOT NULL, " +
                "gene_name VARCHAR(255), " +
                "transcript_preferred_id INT, " +
                "FOREIGN KEY (geneset_id) REFERENCES geneSet(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createGenesPanelTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS genesPanel ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR, " +
                "genes VARCHAR, " +
                "user_creation VARCHAR, " +
                "creation_date DATE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createHotspotsSetTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS hotspotsSet ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR_IGNORECASE(255), " +
                "is_active BOOLEAN DEFAULT True, " +
                "UNIQUE (name));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createHotspotsTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS hotspots ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "hotspotsSet_id INT NOT NULL, " +
                "hotspot_id VARCHAR(255), " +
                "contig VARCHAR(255), " +
                "start INT, " +
                "end_ INT, " +
                "ref VARCHAR(255), " +
                "alt VARCHAR(255), " +
                "gene VARCHAR(255), " +
                "coding_mut VARCHAR(255), " +
                "protein_mut VARCHAR(255), " +
                "type VARCHAR(255), " +
                "FOREIGN KEY (hotspotsSet_id) REFERENCES hotspotsSet(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createTranscriptsTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS transcripts ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR(255), " +
                "gene_id INT NOT NULL, " +
                "FOREIGN KEY (gene_id) REFERENCES genes(id) ON DELETE CASCADE);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

//    public void createGeneTranscriptTable() throws SQLException {
//        final String sql = "CREATE TABLE IF NOT EXISTS geneTranscripts ( " +
//                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
//                "gene_name VARCHAR(255), " +
//                "transcripts_names VARCHAR(255), " +
//                "geneTranscriptSet_id INT, " +
//                "FOREIGN KEY (geneTranscriptSet_id) REFERENCES geneTranscriptSet(id) ON DELETE CASCADE);";
//        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
//            stm.executeUpdate();
//        }
//    }


    private void createRunTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS runs ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR(255), " +
                "path VARCHAR, " +
                "date DATE, " +
                "creation_date DATE, " +
                "creation_user VARCHAR, " +
                "UNIQUE(name));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createRunFilesTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS runFiles ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "path VARCHAR(255), " +
                "run_id INT, " +
                "FOREIGN KEY (run_id) REFERENCES runs(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createAnalysisTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS analysis ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR(255), " +
                "path VARCHAR, " +
                "vcf_path VARCHAR, " +
                "bam_path VARCHAR, " +
                "depth_path VARCHAR, " +
                "coverage_path VARCHAR, " +
                "creation_datetime TIMESTAMP, " +
                "creation_user VARCHAR, " +
                "sample_name VARCHAR(255), " +
                "run_id INT, " +
                "analysisParameters_id INT, " +
                "status VARCHAR(255), " +
                "metadata VARCHAR, " +
                "FOREIGN KEY (run_id) REFERENCES runs(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (analysisParameters_id) REFERENCES analysisParameters(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createVariantTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variants ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "contig VARCHAR_IGNORECASE(10), " +
                "start INT, " +
                "end_ INT, " +
                "ref VARCHAR_IGNORECASE, " +
                "alt VARCHAR_IGNORECASE, " +
                "pathogenicity_value INT DEFAULT 3, " +
                "pathogenicity_confirmed BOOLEAN DEFAULT TRUE, " +
                "false_positive BOOLEAN DEFAULT FALSE, " +
                "UNIQUE (contig, start, end_, ref, alt));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createVariantCommentTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variantComments ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "commentary VARCHAR(MAX), " +
                "date TIMESTAMP, " +
                "last_edit_date TIMESTAMP, " +
                "foreign key (variant_id) references variants(id));";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createVariantHistoryTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variantsHistory ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "pathogenicity_before INT, " +
                "pathogenicity_after INT, " +
                "date TIMESTAMP, " +
                "validation_date TIMESTAMP, " +
                "validation_username VARCHAR(255), " +
                "commentary TEXT, " +
                "foreign key (variant_id) references variants(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createVariantPathogenicityTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variantPathogenicity ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT NOT NULL, " +
                "pathogenicity_score INT NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "date TIMESTAMP, " +
                "validation_userid INT, " +
                "validation_username VARCHAR(255), " +
                "validation_date TIMESTAMP, " +
                "commentary TEXT, " +
                "foreign key (variant_id) references variants(id) ON DELETE CASCADE, " +
                "foreign key (user_id) references users(id) ON DELETE SET NULL);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createVariantFalsePositiveTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variantFalsePositive ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT NOT NULL, " +
                "false_positive BOOLEAN NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "date TIMESTAMP, " +
                "validation_userid INT, " +
                "validation_username VARCHAR(255), " +
                "validation_date TIMESTAMP, " +
                "commentary TEXT, " +
                "foreign key (variant_id) references variants(id) ON DELETE CASCADE, " +
                "foreign key (user_id) references users(id) ON DELETE SET NULL);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createVariantAnalysisesTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variantAnalyses ( " +
                "variant_id INT NOT NULL, " +
                "analysis_id INT NOT NULL, " +
                "FOREIGN KEY (variant_id) references variants(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (analysis_id) references analysis(id) ON DELETE CASCADE, " +
                "PRIMARY KEY (variant_id, analysis_id));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createSangerStateTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS sangerState ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT NOT NULL, " +
                "analysis_id INT NOT NULL, " +
                "sanger_state VARCHAR(255) NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "date TIMESTAMP, " +
                "commentary TEXT, " +
                "foreign key (variant_id) references variants(id) ON DELETE CASCADE, " +
                "foreign key (analysis_id) references analysis(id) ON DELETE CASCADE, " +
                "foreign key (user_id) references users(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createVariantCommentaryTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS variantCommentary ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "date TIMESTAMP, " +
                "commentary TEXT, " +
                "foreign key (variant_id) references variants(id) ON DELETE CASCADE, " +
                "foreign key (user_id) references users(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createAnnotationCommentaryTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS annotationCommentary ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "variant_id INT NOT NULL, " +
                "analysis_id INT NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "date TIMESTAMP, " +
                "commentary TEXT, " +
                "foreign key (variant_id) references variants(id) ON DELETE CASCADE, " +
                "foreign key (analysis_id) references analysis(id) ON DELETE CASCADE, " +
                "foreign key (user_id) references users(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createAnalysisCommentaryTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS analysisCommentary ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "analysis_id INT NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "date TIMESTAMP, " +
                "commentary TEXT, " +
                "foreign key (analysis_id) references analysis(id) ON DELETE CASCADE, " +
                "foreign key (user_id) references users(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createAdditionalImagesTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS analysisImages ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "analysis_id INT, " +
                "path VARCHAR," +
                "userImport VARCHAR," +
                "date TIMESTAMP, " +
                "FOREIGN KEY (analysis_id) REFERENCES analysis(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }


    private void createCNVControlGroupTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS cnvControlGroups ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "panel_id INT, " +
                "name VARCHAR, " +
                "algorithm VARCHAR, " +
                "matrix_path VARCHAR, " +
                "path VARCHAR, " +
                "FOREIGN KEY (panel_id) REFERENCES panels(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }

    private void createCNVControlTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS cnvControls ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "group_id INT, " +
                "name VARCHAR, " +
                "depth_path VARCHAR, " +
                "gender VARCHAR, " +
                "FOREIGN KEY (group_id) REFERENCES cnvControlGroups(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }

    private void createPrescribersTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS Prescribers ( " +
                "id INT auto_increment, " +
                "status VARCHAR, " +
                "first_name VARCHAR, " +
                "last_name VARCHAR, " +
                "address VARCHAR);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    private void createReportGeneCommentary()  throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS reportGeneCommentary ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "report_type VARCHAR(16), " +
                "gene_name VARCHAR(16), " +
                "title VARCHAR(255), " +
                "commentary VARCHAR(MAX));";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    private void createReportMutationCommentary()  throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS reportMutationCommentary ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "report_type VARCHAR(16), " +
                "variant_id INT, " +
                "title VARCHAR(255), " +
                "commentary VARCHAR(MAX), " +
                "FOREIGN KEY (variant_id) REFERENCES variants(id) ON DELETE CASCADE);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createReportOtherCommentary()  throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS reportOtherCommentary ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "report_type VARCHAR(16), " +
                "title VARCHAR(MAX), " +
                "commentary VARCHAR(MAX));";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createCIQModelTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS CIQModel ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "name VARCHAR(MAX), " +
                "barcode VARCHAR(MAX), " +
                "is_active BOOLEAN DEFAULT True, " +
                "UNIQUE (barcode));";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }


    private void createCIQHotspotTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS CIQHotspot ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "ciq_id INT, " +
                "name VARCHAR(MAX), " +
                "contig VARCHAR_IGNORECASE(10), " +
                "position INT, " +
                "ref VARCHAR_IGNORECASE, " +
                "alt VARCHAR_IGNORECASE, " +
                "targetVaf FLOAT, " +
                "FOREIGN KEY (ciq_id) REFERENCES CIQModel(id) ON DELETE CASCADE);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    private void createCIQAnalysisTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS CIQAnalysis ( " +
                "ciq_id INT NOT NULL, " +
                "analysis_id INT NOT NULL, " +
                "PRIMARY KEY(ciq_id, analysis_id), " +
                "FOREIGN KEY (ciq_id) REFERENCES CIQModel(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (analysis_id) REFERENCES analysis(id) ON DELETE CASCADE);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    private void createCIQRecordTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS CIQRecord ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "ciqModel_id INT NOT NULL, " +
                "ciqHotspot_id INT NOT NULL, " +
                "analysis_id INT NOT NULL, " +
                "dp INT, " +
                "ao INT, " +
                "vaf FLOAT, " +
                "FOREIGN KEY (ciqModel_id) REFERENCES CIQModel(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (analysis_id) REFERENCES analysis(id) ON DELETE CASCADE);";
        try(Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    private void createCIQRecordHistoryTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS CIQRecordHistory ( " +
                "id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "ciqRecord_id INT NOT NULL, " +
                "user_id INT, " +
                "user_name VARCHAR(255), " +
                "accepted_before VARCHAR, " +
                "accepted_after VARCHAR, " +
                "mean FLOAT, " +
                "sd FLOAT, " +
                "datetime TIMESTAMP, " +
                "comment VARCHAR(MAX), " +
                "foreign key (ciqRecord_id) references CIQRecord(id) ON DELETE CASCADE);";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)){
            stm.executeUpdate();
        }
    }
}