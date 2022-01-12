package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.analyse.PanelRegion;

import java.sql.*;

public class PanelRegionDAO extends DAO{

    public long addRegion(PanelRegion region, long panel_id) throws SQLException {
        long region_id;
        final String sql = "INSERT INTO panelRegions (contig, start, end_, name, panel_id, pool) VALUES(?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, region.getContig());
            stm.setInt(++i, region.getStart());
            stm.setInt(++i, region.getEnd());
            stm.setString(++i, region.getName());
            stm.setLong(++i, panel_id);
            stm.setString(++i, region.getPoolAmplification());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                region_id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No panel region inserted in the database");
            }
        }
        return region_id;
    }

    public ObservableList<PanelRegion> getPanelRegions(long panel_id) throws SQLException {
        ObservableList<PanelRegion> panelRegions = FXCollections.observableArrayList();
        final String sql = "SELECT id, contig, start, end_, name, pool FROM panelRegions WHERE panel_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, panel_id);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String contig = rs.getString("contig");
                int start = rs.getInt("start");
                int end = rs.getInt("end_");
                String name = rs.getString("name");
                String pool = rs.getString("pool");
                panelRegions.add(new PanelRegion(id, panel_id, contig, start, end, name, pool));
            }
        }
        return panelRegions;
    }

}
