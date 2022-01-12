package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.variants.HotspotsSet;

import java.sql.*;

public class AnalysisParametersDAO extends DAO {


    public boolean analysisParametersExists(String name) throws SQLException {
        final String sql = "SELECT id FROM analysisParameters  WHERE lower(name)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public long addAnalysisParameters(String name, Genome genome, int minDepth, int warningDepth,
                                      double minVaf, long panel_id, long geneSet_id,
                                      Long hotspotsSet_id, TargetEnrichment targetEnrichment) throws SQLException {
        long id;
        final String sql = "INSERT INTO analysisParameters (name, genome, min_depth, warning_depth, min_vaf," +
                " is_active, panel_id, geneSet_id, hotspotsSet_id, target_enrichment) VALUES(?, ?, ?, ?, ?, True, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, name);
            stm.setString(++i, genome.name());
            stm.setInt(++i, minDepth);
            stm.setInt(++i, warningDepth);
            stm.setDouble(++i, minVaf);
            stm.setLong(++i, panel_id);
            stm.setLong(++i, geneSet_id);
            if (hotspotsSet_id == null) {
                stm.setNull(++i, Types.INTEGER);
            } else {
                stm.setLong(++i, hotspotsSet_id);
            }
            stm.setString(++i, targetEnrichment.name());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No user inserted in the database");
            }
        }
        return id;
    }

    public long updateAnalysisParameters(long id, String name, Genome genome, int minDepth, int warningDepth, float minVaf, long panel_id, long geneSet_id, boolean isActive) throws SQLException {
        final String sql = "UPDATE analysisParameters SET name=?, genome=?, min_depth=?, warning_depth=?, min_vaf=?, panel_id=?, geneSet_id=?, is_active=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, name);
            stm.setString(++i, genome.name());
            stm.setInt(++i, minDepth);
            stm.setInt(++i, warningDepth);
            stm.setFloat(++i, minVaf);
            stm.setLong(++i, panel_id);
            stm.setLong(++i, geneSet_id);
            stm.setBoolean(++i, isActive);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
        return id;
    }


    public ObservableList<AnalysisParameters> getAnalysisParameters() throws SQLException {
        ObservableList<AnalysisParameters> analysisParameters = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, genome, min_depth, warning_depth, min_vaf, is_active, panel_id, geneSet_id, hotspotsSet_id, target_enrichment FROM analysisParameters ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                Genome genome = Genome.valueOf(rs.getString("genome"));
                int minDepth = rs.getInt("min_depth");
                int warningDepth = rs.getInt("warning_depth");
                float minVaf = rs.getFloat("min_vaf");
                boolean isActive = rs.getBoolean("is_active");
                long panelId = rs.getLong("panel_id");
                long geneSetId = rs.getLong("geneSet_id");
                long hotspotsSetId = rs.getLong("hotspotsSet_id");
                TargetEnrichment targetEnrichment = TargetEnrichment.valueOf(rs.getString("target_enrichment"));
                Panel panel = DAOController.getPanelDAO().getPanel(panelId);
                GeneSet geneSet = DAOController.getGeneSetDAO().getGeneSet(geneSetId);
                HotspotsSet hotspotsSet = DAOController.getHotspotsSetDAO().getHotspotsSet(hotspotsSetId);

                analysisParameters.add(new AnalysisParameters(id, genome, name, minDepth, warningDepth, minVaf, isActive, panel, geneSet, hotspotsSet, targetEnrichment));
            }
        }
        return analysisParameters;
    }


    public ObservableList<AnalysisParameters> getActiveAnalysisParameters() throws SQLException {
        ObservableList<AnalysisParameters> analysisParameters = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, genome, min_depth, warning_depth, min_vaf, is_active, panel_id, geneSet_id, hotspotsSet_id, target_enrichment FROM analysisParameters WHERE is_active=True ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                Genome genome = Genome.valueOf(rs.getString("genome"));
                int minDepth = rs.getInt("min_depth");
                int warningDepth = rs.getInt("warning_depth");
                float minVaf = rs.getFloat("min_vaf");
                boolean isActive = rs.getBoolean("is_active");
                long panelId = rs.getLong("panel_id");
                long geneSetId = rs.getLong("geneSet_id");
                long hotspotsSetId = rs.getLong("hotspotsSet_id");
                TargetEnrichment targetEnrichment = TargetEnrichment.valueOf(rs.getString("target_enrichment"));
                Panel panel = DAOController.getPanelDAO().getPanel(panelId);
                GeneSet geneSet = DAOController.getGeneSetDAO().getGeneSet(geneSetId);
                HotspotsSet hotspotsSet = DAOController.getHotspotsSetDAO().getHotspotsSet(hotspotsSetId);

                analysisParameters.add(new AnalysisParameters(id, genome, name, minDepth, warningDepth, minVaf, isActive, panel, geneSet, hotspotsSet, targetEnrichment));
            }
        }
        return analysisParameters;
    }


    public AnalysisParameters getAnalysisParameters(long id) throws SQLException {
        final String sql = "SELECT name, genome, min_depth, warning_depth, min_vaf, is_active, panel_id, geneSet_id, hotspotsSet_id, target_enrichment FROM analysisParameters WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                Genome genome = Genome.valueOf(rs.getString("genome"));
                int minDepth = rs.getInt("min_depth");
                int warningDepth = rs.getInt("warning_depth");
                float minVaf = rs.getFloat("min_vaf");
                boolean isActive = rs.getBoolean("is_active");
                long panelId = rs.getLong("panel_id");
                long geneSetId = rs.getLong("geneSet_id");
                long hotspotsSetId = rs.getLong("hotspotsSet_id");
                TargetEnrichment targetEnrichment = TargetEnrichment.valueOf(rs.getString("target_enrichment"));
                Panel panel = DAOController.getPanelDAO().getPanel(panelId);
                GeneSet geneSet = DAOController.getGeneSetDAO().getGeneSet(geneSetId);
                HotspotsSet hotspotsSet = DAOController.getHotspotsSetDAO().getHotspotsSet(hotspotsSetId);

                return new AnalysisParameters(id, genome, name, minDepth, warningDepth, minVaf, isActive, panel, geneSet, hotspotsSet, targetEnrichment);
            }
        }
        return null;
    }


    public AnalysisParameters getAnalysisParameters(String name) throws SQLException {
        final String sql = "SELECT id, genome, min_depth, warning_depth, min_vaf, is_active, panel_id, geneSet_id, hotspotsSet_id, target_enrichment FROM analysisParameters WHERE name=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                Genome genome = Genome.valueOf(rs.getString("genome"));
                int minDepth = rs.getInt("min_depth");
                int warningDepth = rs.getInt("warning_depth");
                float minVaf = rs.getFloat("min_vaf");
                boolean isActive = rs.getBoolean("is_active");
                long panelId = rs.getLong("panel_id");
                long geneSetId = rs.getLong("geneSet_id");
                long hotspotsSetId = rs.getLong("hotspotsSet_id");
                TargetEnrichment targetEnrichment = TargetEnrichment.valueOf(rs.getString("target_enrichment"));
                Panel panel = DAOController.getPanelDAO().getPanel(panelId);
                GeneSet geneSet = DAOController.getGeneSetDAO().getGeneSet(geneSetId);
                HotspotsSet hotspotsSet = DAOController.getHotspotsSetDAO().getHotspotsSet(hotspotsSetId);

                return new AnalysisParameters(id, genome, name, minDepth, warningDepth, minVaf, isActive, panel, geneSet, hotspotsSet, targetEnrichment);
            }
        }
        return null;
    }


    public boolean isUsed(long id) throws SQLException {
        final String sql = "SELECT id from analysis WHERE analysisParameters_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public void deleteAnalysisParameters(long id) throws SQLException {
        final String sql = "DELETE FROM analysisParameters WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }
}
