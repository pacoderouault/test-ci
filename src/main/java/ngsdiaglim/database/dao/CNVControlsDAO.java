package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.cnv.CNVControl;
import ngsdiaglim.cnv.CNVControlGroup;
import ngsdiaglim.enumerations.Gender;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CNVControlsDAO extends DAO {

    public void addCNVControl(CNVControlGroup group, String name, File depthFile, Gender gender) throws SQLException {
        final String sql = "INSERT INTO cnvControls (group_id, name, depth_path, gender) VALUES (?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, group.getId());
            stm.setString(++i, name);
            stm.setString(++i, depthFile == null ? "" : depthFile.getPath());
            stm.setString(++i, gender.name());
            stm.executeUpdate();
        }
    }


    public ObservableList<CNVControl> getCNVControls(CNVControlGroup group) throws SQLException {
        ObservableList<CNVControl> cnvControls = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, depth_path, gender FROM cnvControls WHERE group_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, group.getId());
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                CNVControl cnvControl = new CNVControl(
                        rs.getLong("id"),
                        group,
                        rs.getString("name"),
                        new File(rs.getString("depth_path")),
                        Gender.valueOf(rs.getString("gender"))
                );
                cnvControls.add(cnvControl);
            }
        }
        return cnvControls;
    }


    public void removeCNVControl(long id) throws SQLException {
        final String sql = "DELETE FROM cnvControls WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

    public void updateControl(CNVControl control) throws SQLException {
        final String sql = "UPDATE cnvControls SET gender=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, control.getGender().name());
            stm.setLong(++i, control.getId());
            stm.executeUpdate();
        }
    }
}
