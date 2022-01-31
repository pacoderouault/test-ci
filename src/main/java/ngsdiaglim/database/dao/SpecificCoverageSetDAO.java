package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageSet;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.HotspotsSet;

import java.sql.*;

public class SpecificCoverageSetDAO extends DAO {

    public boolean specificCoverageSetExists(String name) throws SQLException {
        final String sql = "SELECT id FROM specificCoverageSet WHERE lower(name)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }

    public long addSpecificCoverageSet(String name) throws SQLException {
        long id;
        final String sql = "INSERT INTO specificCoverageSet (name, is_active) VALUES(?, True);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, name);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No specificCoverageSet set inserted in the database");
            }
        }
        return id;
    }

    public ObservableList<SpecificCoverageSet> getSpecificCoverageSets() throws SQLException {
        ObservableList<SpecificCoverageSet> specificCoverageSets = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active FROM specificCoverageSet ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                ObservableList<SpecificCoverage> specificCoverages = DAOController.getSpecificCoverageDAO().getSpecificCoverage(id);
                SpecificCoverageSet specificCoverageSet = new SpecificCoverageSet(id, name, specificCoverages, isActive);
                specificCoverageSets.add(specificCoverageSet);
            }
        }
        return specificCoverageSets;
    }

    public ObservableList<SpecificCoverageSet> getActiveSpecificCoverageSet() throws SQLException {
        ObservableList<SpecificCoverageSet> specificCoverageSets = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active FROM specificCoverageSet WHERE is_active=True ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                ObservableList<SpecificCoverage> specificCoverages = DAOController.getSpecificCoverageDAO().getSpecificCoverage(id);
                SpecificCoverageSet specificCoverageSet = new SpecificCoverageSet(id, name, specificCoverages, isActive);
                specificCoverageSets.add(specificCoverageSet);
            }
        }
        return specificCoverageSets;
    }

    public SpecificCoverageSet getSpecificCoverageSet(long id) throws SQLException {
        final String sql = "SELECT name, is_active FROM specificCoverageSet WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                ObservableList<SpecificCoverage> specificCoverages = DAOController.getSpecificCoverageDAO().getSpecificCoverage(id);
                return new SpecificCoverageSet(id, name, specificCoverages, isActive);
            }
            return null;
        }
    }

    public void activeSpecificCoverageSet(long id, boolean isActive) throws SQLException {
        final String sql = "UPDATE specificCoverageSet SET is_active=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareCall(sql)) {
            int i = 0;
            stm.setBoolean(++i, isActive);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

    public void deleteSpecificCoverageSet(long id) throws SQLException {
        final String sql = "DELETE FROM specificCoverageSet WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    public boolean isUsed(long id) throws SQLException {
        final String sql = "SELECT id from analysisParameters WHERE specificCoverageSet_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareCall(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            return (rs.next());
        }
    }
}
