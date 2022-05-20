package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.analyse.Panel;

import java.io.File;
import java.sql.*;

public class PanelDAO extends DAO {

    public boolean panelExists(String panelName) throws SQLException {
        final String sql = "SELECT id FROM panels WHERE lower(name)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, panelName);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public long addPanel(String panelname, String bedPath) throws SQLException {
        long panel_id;
        final String sql = "INSERT INTO panels (name, is_active, bed_path) VALUES(?, True, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, panelname);
            stm.setString(++i, bedPath);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                panel_id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No user inserted in the database");
            }
        }
        return panel_id;
    }


    public void updatePanel(Panel panel, boolean isActive) throws SQLException {
        final String sql = "UPDATE panels SET is_active=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setBoolean(++i, isActive);
            stm.setLong(++i, panel.getId());
            stm.executeUpdate();
        }
    }


    public ObservableList<Panel> getPanels() throws SQLException {
        ObservableList<Panel> panels = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active, bed_path FROM panels ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                File bedFile = new File(rs.getString("bed_path"));
                panels.add(new Panel(id, name, isActive, bedFile));
            }
        }
        return panels;
    }

    public ObservableList<Panel> getActivePanels() throws SQLException {
        ObservableList<Panel> panels = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active, bed_path FROM panels WHERE is_active=True ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                File bedFile = new File(rs.getString("bed_path"));
                panels.add(new Panel(id, name, isActive, bedFile));
            }
        }
        return panels;
    }

    public Panel getPanel(long panel_id) throws SQLException {
        final String sql = "SELECT id, name, is_active, bed_path FROM panels WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, panel_id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                File bedFile = new File(rs.getString("bed_path"));
                return new Panel(id, name, isActive, bedFile);
            }
            return null;
        }
    }


    public void deletePanel(long panelId) throws SQLException {
        final String sql = "DELETE FROM panels WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, panelId);
            stm.executeUpdate();
        }
    }

    public boolean isUsed(long id) throws SQLException {
        final String sql = "SELECT id FROM analysisParameters WHERE panel_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }
}
