package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.modeles.ciq.CIQModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CIQHotspotDAO extends DAO {

    public ObservableList<CIQHotspot> getCIQHotspots(CIQModel ciqModel) throws SQLException {
        ObservableList<CIQHotspot> hotspots = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, contig, position, ref, alt, targetVaf FROM CIQHotspot WHERE ciq_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, ciqModel.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                hotspots.add(
                        new CIQHotspot(
                                rs.getLong("id"),
                                ciqModel,
                                rs.getString("name"),
                                rs.getString("contig"),
                                rs.getInt("position"),
                                rs.getString("ref"),
                                rs.getString("alt"),
                                rs.getFloat("targetVaf")
                        )
                );
            }
        }
        return hotspots;
    }


    public void addCIQHotspot(CIQHotspot hotspot, long ciqModelId) throws SQLException {
        final String sql = "INSERT INTO CIQHotspot (ciq_id, name, contig, position, ref, alt, targetVaf) VALUES(?,?,?,?,?,?,?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, ciqModelId);
            stm.setString(++i, hotspot.getName());
            stm.setString(++i, hotspot.getContig());
            stm.setInt(++i, hotspot.getPosition());
            stm.setString(++i, hotspot.getRef());
            stm.setString(++i, hotspot.getAlt());
            stm.setFloat(++i, hotspot.getVafTarget());
            stm.executeUpdate();
        }
    }
}
