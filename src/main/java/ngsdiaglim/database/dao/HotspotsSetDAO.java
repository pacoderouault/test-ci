package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.HotspotsSet;

import java.sql.*;

public class HotspotsSetDAO extends DAO {

    public boolean hotspotsSetExists(String name) throws SQLException {
        final String sql = "SELECT id FROM hotspotsSet WHERE lower(name)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public long addHotspotsSet(String name) throws SQLException {
        long id;
        final String sql = "INSERT INTO hotspotsSet (name, is_active) VALUES(?, True);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, name);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No hotspots set inserted in the database");
            }
        }
        return id;
    }


    public void updateHotspotsSet(HotspotsSet hotspotsSet, String newName, boolean isActive) throws SQLException {
        final String sql = "UPDATE hotspotsSet SET name=?, is_active=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, newName);
            stm.setBoolean(++i, isActive);
            stm.setLong(++i, hotspotsSet.getId());
            stm.executeUpdate();
        }
    }


    public ObservableList<HotspotsSet> getHotspotsSets() throws SQLException {
        ObservableList<HotspotsSet> hotspotsSets = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active FROM hotspotsSet ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                ObservableList<Hotspot> hotspots = DAOController.getHotspotDAO().getHotspots(id);
                HotspotsSet geneSet = new HotspotsSet(id, name, hotspots, isActive);
                hotspotsSets.add(geneSet);
            }
        }
        return hotspotsSets;
    }

    public ObservableList<HotspotsSet> getActiveHotspotsSets() throws SQLException {
        ObservableList<HotspotsSet> hotspotsSets = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active FROM hotspotsSet WHERE is_active=True ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                ObservableList<Hotspot> hotspots = DAOController.getHotspotDAO().getHotspots(id);
                HotspotsSet hotspotsSet = new HotspotsSet(id, name, hotspots, isActive);
                hotspotsSets.add(hotspotsSet);
            }
        }
        return hotspotsSets;
    }

    public HotspotsSet getHotspotsSet(long id) throws SQLException {
        final String sql = "SELECT name, is_active FROM hotspotsSet WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                ObservableList<Hotspot> hotspots = DAOController.getHotspotDAO().getHotspots(id);
                return new HotspotsSet(id, name, hotspots, isActive);
            }
            return null;
        }
    }


    public void deleteHotspotsSet(long id) throws SQLException {
        final String sql = "DELETE FROM hotspotsSet WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }
}
