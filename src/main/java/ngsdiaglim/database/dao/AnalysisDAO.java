package ngsdiaglim.database.dao;

import htsjdk.samtools.util.Tuple;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Run;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class AnalysisDAO extends DAO {
    private final static Logger logger = LogManager.getLogger(AnalysisDAO.class);
    private static final NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    public long addAnalyse(String name, String path, File vcfFile, File bamFile, File depthFile, File coverageFile, File specCoverageFile, LocalDateTime creationDate, String sampleName, Run run, AnalysisParameters analysisParameters, String metadata) throws SQLException, IOException {
        long analysis_id;
        final String sql = "INSERT INTO analysis (name, path, vcf_path, bam_path, depth_path, coverage_path, specificCoverage_path, creation_datetime, creation_user, sample_name, run_id, analysisParameters_id, status, metadata)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, name);
            stm.setString(++i, path);
            stm.setString(++i, vcfFile.toString());
            if (bamFile != null) {
                stm.setString(++i, bamFile.getPath());
            }
            else {
                stm.setNull(++i, Types.NULL);
            }
            if (depthFile != null) {
                stm.setString(++i, depthFile.toString());
            }
            else {
                stm.setNull(++i, Types.NULL);
            }
            if (coverageFile != null) {
                stm.setString(++i, coverageFile.toString());
            }
            else {
                stm.setNull(++i, Types.NULL);
            }
            if (specCoverageFile != null) {
                stm.setString(++i, specCoverageFile.toString());
            }
            else {
                stm.setNull(++i, Types.NULL);
            }
//            stm.setDate(++i, Date.valueOf(creationDate));
            stm.setTimestamp(++i, Timestamp.valueOf(creationDate));
            stm.setString(++i, App.get().getLoggedUser().getUsername());
            stm.setString(++i, sampleName);
            stm.setLong(++i, run.getId());
            stm.setLong(++i, analysisParameters.getId());
            stm.setString(++i, AnalysisStatus.INPROGRESS.name());
            stm.setString(++i, metadata);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                analysis_id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No run inserted in the database");
            }
        }
        return analysis_id;
    }


    public ObservableList<Analysis> getAnalysis(Run run) throws SQLException {
        ObservableList<Analysis> analyses = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, path, vcf_path, bam_path, depth_path, coverage_path, specificCoverage_path, creation_datetime, creation_user, sample_name, analysisParameters_id, status, import_complete, metadata" +
                " FROM analysis WHERE run_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, run.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String vcf_path = rs.getString("vcf_path");
                String bam_path = rs.getString("bam_path");
                String depth_path = rs.getString("depth_path");
                String coverage = rs.getString("coverage_path");
                String specCoverage = rs.getString("specificCoverage_path");
                LocalDateTime creationDate = rs.getTimestamp("creation_datetime").toLocalDateTime();
                String creationUser = rs.getString("creation_user");
                String sampleName = rs.getString("sample_name");
                long analysisParametersId = rs.getLong("analysisParameters_id");
                String metadata = rs.getString("metadata");
                AnalysisStatus status;
                try {
                    status = AnalysisStatus.valueOf(rs.getString("status"));
                } catch (Exception e) {
                    logger.error(e);
                    status = AnalysisStatus.INPROGRESS;
                }
                boolean importComplete = rs.getBoolean("import_complete");
                AnalysisParameters analysisParameters = DAOController.getAnalysisParametersDAO().getAnalysisParameters(analysisParametersId);

                Analysis analysis = new Analysis(
                        id,
                        name,
                        path,
                        vcf_path,
                        bam_path,
                        depth_path,
                        coverage,
                        specCoverage,
                        run,
                        creationDate,
                        creationUser,
                        sampleName,
                        analysisParameters,
                        status,
                        importComplete,
                        metadata
                );

                analyses.add(analysis);
            }
        }

        return analyses.stream()
                .sorted((o1, o2) -> naturalSortComparator.compare(o1.getSampleName(), o2.getSampleName()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }


    public Analysis getAnalysis(Run run, long id) throws SQLException {
        final String sql = "SELECT name, path, vcf_path, bam_path, depth_path, coverage_path, specificCoverage_path, creation_datetime, creation_user, sample_name, analysisParameters_id, status, import_complete, metadata" +
                " FROM analysis WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String path = rs.getString("path");
                String vcf_path = rs.getString("vcf_path");
                String bam_path = rs.getString("bam_path");
                String depth_path = rs.getString("depth_path");
                String coverage = rs.getString("coverage_path");
                String specCoverage = rs.getString("specificCoverage_path");
                LocalDateTime creationDate = rs.getTimestamp("creation_datetime").toLocalDateTime();
                String creationUser = rs.getString("creation_user");
                String sampleName = rs.getString("sample_name");
                long analysisParametersId = rs.getLong("analysisParameters_id");
                String metadata = rs.getString("metadata");
                AnalysisStatus status;
                try {
                    status = AnalysisStatus.valueOf(rs.getString("status"));
                } catch (Exception e) {
                    logger.error(e);
                    status = AnalysisStatus.INPROGRESS;
                }
                boolean importComplete = rs.getBoolean("import_complete");
                AnalysisParameters analysisParameters = DAOController.getAnalysisParametersDAO().getAnalysisParameters(analysisParametersId);

                return new Analysis(
                        id,
                        name,
                        path,
                        vcf_path,
                        bam_path,
                        depth_path,
                        coverage,
                        specCoverage,
                        run,
                        creationDate,
                        creationUser,
                        sampleName,
                        analysisParameters,
                        status,
                        importComplete,
                        metadata
                );

            }
        }

        return null;
    }


    public Analysis getAnalysis(long id) throws SQLException {
        final String sql = "SELECT name, path, vcf_path, bam_path, depth_path, coverage_path, specificCoverage_path, creation_datetime, " +
                "creation_user, sample_name, run_id, analysisParameters_id, status, import_complete, metadata" +
                " FROM analysis WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String path = rs.getString("path");
                String vcf_path = rs.getString("vcf_path");
                String bam_path = rs.getString("bam_path");
                String depth_path = rs.getString("depth_path");
                String coverage = rs.getString("coverage_path");
                String specCoverage = rs.getString("specificCoverage_path");
                LocalDateTime creationDate = rs.getTimestamp("creation_datetime").toLocalDateTime();
                String creationUser = rs.getString("creation_user");
                String sampleName = rs.getString("sample_name");
                long runID = rs.getLong("run_id");
                long analysisParametersId = rs.getLong("analysisParameters_id");
                String metadata = rs.getString("metadata");
                AnalysisStatus status;
                try {
                    status = AnalysisStatus.valueOf(rs.getString("status"));
                } catch (Exception e) {
                    logger.error(e);
                    status = AnalysisStatus.INPROGRESS;
                }
                boolean importComplete = rs.getBoolean("import_complete");
                AnalysisParameters analysisParameters = DAOController.getAnalysisParametersDAO().getAnalysisParameters(analysisParametersId);

                Run run = DAOController.getRunsDAO().getRun(runID);

                return new Analysis(
                        id,
                        name,
                        path,
                        vcf_path,
                        bam_path,
                        depth_path,
                        coverage,
                        specCoverage,
                        run,
                        creationDate,
                        creationUser,
                        sampleName,
                        analysisParameters,
                        status,
                        importComplete,
                        metadata
                );

            }
        }

        return null;
    }


    public Tuple<Integer, Integer> getAnalysisCount(long runId) throws SQLException {
        final String sql = "SELECT status FROM analysis WHERE run_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, runId);
            ResultSet rs = stm.executeQuery();
            int inProgressCount = 0;
            int completeCount = 0;
            while (rs.next()) {
                AnalysisStatus status;
                try {
                    status = AnalysisStatus.valueOf(rs.getString("status"));
                } catch (Exception e) {
                    logger.error(e);
                    status = AnalysisStatus.INPROGRESS;
                }
                if (status.equals(AnalysisStatus.DONE)) {
                    completeCount++;
                } else {
                    inProgressCount++;
                }
            }
            return new Tuple<>(inProgressCount, completeCount);
        }
    }


    public void deleteAnalysis(long id) throws SQLException {
        final String sql = "DELETE FROM analysis WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }


    public void setCoverageFile(long id, File coverageFile) throws SQLException {
        final String sql = "UPDATE Analysis SET coverage_path=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, coverageFile.getPath());
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

    public void updateAnalysisStatus(long analysis_id, AnalysisStatus value) throws SQLException {
        final String sql = "UPDATE Analysis SET status=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, value.name());
            stm.setLong(++i, analysis_id);
            stm.executeUpdate();
        }
    }

    public void updateAnalysisImportState(long analysis_id, boolean import_complete) throws SQLException {
        final String sql = "UPDATE analysis SET import_complete=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setBoolean(++i, import_complete);
            stm.setLong(++i, analysis_id);
            stm.executeUpdate();
        }
    }


    public ObservableList<Analysis> searchAnalysis(String query) throws SQLException {
        ObservableList<Analysis> analyses = FXCollections.observableArrayList();
        final String sql = "SELECT a.id AS aId, a.name AS aName, a.path AS aPath, a.vcf_path AS Avcf_path," +
                " a.bam_path AS aBam_path, a.depth_path AS aDepth_path, a.coverage_path AS aCoverage_path, a.specificCoverage_path AS aSpecificCoverage_path," +
                " a.creation_datetime AS aCreation_datetime, a.creation_user AS aCreation_user, a.sample_name AS aSample_name," +
                " a.run_id AS aRun_id, a.analysisParameters_id AS aAnalysisParameters_id, a.status AS aStatus, a.import_complete AS aImport_complete, a.metadata AS aMetadata," +
                " r.id AS rId, r.path AS rPath, r.name AS rName, r.date AS rDate, r.creation_date AS rCreation_date, r.creation_user AS rCreation_user" +
                " FROM analysis AS a JOIN Runs as r WHERE a.run_id=r.id AND (Upper(a.name) LIKE UPPER(?) OR UPPER(a.sample_name) LIKE UPPER(?) OR UPPER(r.name) LIKE UPPER(?));";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
//            query = query
//                    .replace("!", "!!")
//                    .replace("%", "!%")
//                    .replace("_", "!_")
//                    .replace("[", "![");
            int i = 0;
            query = "%" +query+ "%";
            stm.setString(++i, query);
            stm.setString(++i, query);
            stm.setString(++i, query);

            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Run run = new Run(
                        rs.getLong("rId"),
                        rs.getString("rPath"),
                        rs.getString("rName"),
                        rs.getDate("rDate").toLocalDate(),
                        rs.getDate("rCreation_date").toLocalDate(),
                        rs.getString("rCreation_user")
                );

                long id = rs.getLong("aId");
                String name = rs.getString("aName");
                String path = rs.getString("aPath");
                String vcf_path = rs.getString("aVcf_path");
                String bam_path = rs.getString("aBam_path");
                String depth_path = rs.getString("aDepth_path");
                String coverage = rs.getString("aCoverage_path");
                String specCoverage = rs.getString("aSpecificCoverage_path");
                LocalDateTime creationDate = rs.getTimestamp("aCreation_datetime").toLocalDateTime();
                String creationUser = rs.getString("aCreation_user");
                String sampleName = rs.getString("aSample_name");
                long analysisParametersId = rs.getLong("aAnalysisParameters_id");
                String metadata = rs.getString("aMetadata");
                AnalysisStatus status;
                try {
                    status = AnalysisStatus.valueOf(rs.getString("aStatus"));
                } catch (Exception e) {
                    logger.error(e);
                    status = AnalysisStatus.INPROGRESS;
                }
                boolean importComplete = rs.getBoolean("aImport_complete");
                AnalysisParameters analysisParameters = DAOController.getAnalysisParametersDAO().getAnalysisParameters(analysisParametersId);

                Analysis analysis = new Analysis(
                        id,
                        name,
                        path,
                        vcf_path,
                        bam_path,
                        depth_path,
                        coverage,
                        specCoverage,
                        run,
                        creationDate,
                        creationUser,
                        sampleName,
                        analysisParameters,
                        status,
                        importComplete,
                        metadata
                );
                analyses.add(analysis);
            }

        }

        return analyses;
    }
}
