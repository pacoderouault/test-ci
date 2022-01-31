package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.HotspotType;
import ngsdiaglim.modeles.biofeatures.Region;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.variants.Hotspot;

import java.sql.*;

public class SpecificCoverageDAO extends DAO {

    public long addSpecificCoverage(long specificCoverageSetId, Region region, int minCov) throws SQLException {
        long id;
        final String sql = "INSERT INTO specificCoverage (specificCoverageSet_id, name, contig, start, end_, mincov) VALUES (?,?,?,?,?,?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, specificCoverageSetId);
            stm.setString(++i, region.getName());
            stm.setString(++i, region.getContig());
            stm.setInt(++i, region.getStart());
            stm.setInt(++i, region.getEnd());
            stm.setInt(++i, minCov);

            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No gene inserted in the database");
            }
        }
        return id;
    }

    public ObservableList<SpecificCoverage> getSpecificCoverage(long specificCoverageSet_id) throws SQLException {
        ObservableList<SpecificCoverage> coverages = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, contig, start, end_, mincov FROM specificCoverage WHERE specificCoverageSet_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, specificCoverageSet_id);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                coverages.add(new SpecificCoverage(
                        rs.getInt("id"),
                        specificCoverageSet_id,
                        rs.getString("name"),
                        rs.getString("contig"),
                        rs.getInt("start"),
                        rs.getInt("end_"),
                        rs.getInt("minCov")
                ));
            }
        }
        return coverages;
    }

    public int getSpecificCoverageCount(long specificCoverageSet_id) throws SQLException {
        final String sql = "SELECT COUNT(id) AS count FROM specificCoverage WHERE specificCoverageSet_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, specificCoverageSet_id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }
}
