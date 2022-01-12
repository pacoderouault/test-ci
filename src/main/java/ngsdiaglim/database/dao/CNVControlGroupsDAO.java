package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.cnv.CNVControlGroup;

import java.io.File;
import java.sql.*;

public class CNVControlGroupsDAO extends DAO {

    public boolean exists(String name)  throws SQLException {
        final String sql = "SELECT id FROM cnvControlGroups WHERE name=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, name);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }

    public CNVControlGroup addGroup(Panel panel, String name, TargetEnrichment algorithm, File matrixFile, File path) throws SQLException {
        final String sql = "INSERT INTO cnvControlGroups (panel_id, name, algorithm, matrix_path, path) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, panel.getId());
            stm.setString(++i, name);
            stm.setString(++i, algorithm.name());
            stm.setString(++i, matrixFile == null ? "" : matrixFile.getPath());
            stm.setString(++i, path.getPath());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                long id = generatedKeys.getLong(1);
                return new CNVControlGroup( id, name, algorithm, panel, matrixFile, path);
            } else {
                throw new SQLException("No run inserted in the database");
            }
        }
    }

    public ObservableList<CNVControlGroup> getCNVControlGroups() throws SQLException {
        ObservableList<CNVControlGroup> controlGroups = FXCollections.observableArrayList();
        final String sql = "SELECT g.id AS gId, g.name AS gName, g.algorithm AS gAlgorithm, g.matrix_path AS gMatrix_path, g.path AS gPath," +
                " p.id AS pId, p.name AS pName, p.is_active AS pIs_active, p.bed_path AS pBed_path FROM cnvControlGroups AS g JOIN panels AS p WHERE g.panel_id=p.id;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Panel panel = new Panel(
                        rs.getLong("pId"),
                        rs.getString("pName"),
                        rs.getBoolean("pIs_active"),
                        new File(rs.getString("pBed_path"))
                );
                CNVControlGroup cnvControlGroup = new CNVControlGroup(
                        rs.getLong("gId"),
                        rs.getString("gName"),
                        TargetEnrichment.valueOf(rs.getString("gAlgorithm")),
                        panel,
                        new File(rs.getString("gMatrix_path")),
                        new File(rs.getString("gPath"))
                );
                cnvControlGroup.setControls(DAOController.getCnvControlsDAO().getCNVControls(cnvControlGroup));
                controlGroups.add(cnvControlGroup);
            }
        }
        return controlGroups;
    }

    public ObservableList<CNVControlGroup> getCNVControlGroups(Panel panel) throws SQLException {
        ObservableList<CNVControlGroup> controlGroups = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, algorithm, matrix_path, path," +
                " FROM cnvControlGroups WHERE panel_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, panel.getId());
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                CNVControlGroup cnvControlGroup = new CNVControlGroup(
                        rs.getLong("id"),
                        rs.getString("name"),
                        TargetEnrichment.valueOf(rs.getString("algorithm")),
                        panel,
                        new File(rs.getString("matrix_path")),
                        new File(rs.getString("path"))
                );
                cnvControlGroup.setControls(DAOController.getCnvControlsDAO().getCNVControls(cnvControlGroup));
                controlGroups.add(cnvControlGroup);
            }
        }
        return controlGroups;
    }

    public void removeCNVControlGroup(long id) throws SQLException {
        final String sql = "DELETE FROM cnvControlGroups WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


}
