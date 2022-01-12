package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.HotspotType;
import ngsdiaglim.modeles.variants.Hotspot;

import java.sql.*;

public class HotspotDAO extends DAO {

    public long addHotspot(long hotspotsSetId, String hotspotId, String contig, int start, int end,
                           String ref, String alt, String gene, String codingMut, String proteinMut, HotspotType type) throws SQLException {
        final String sql = "INSERT INTO hotspots(hotspotsSet_id, hotspot_id, contig, start, end_, ref, alt, gene, coding_mut, protein_mut, type)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        long id;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, hotspotsSetId);
            stm.setString(++i, hotspotId);
            stm.setString(++i, contig);
            stm.setInt(++i, start);
            stm.setInt(++i, end);
            stm.setString(++i, ref);
            stm.setString(++i, alt);
            stm.setString(++i, gene);
            stm.setString(++i, codingMut);
            stm.setString(++i, proteinMut);
            stm.setString(++i, type.name());
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


    public long addHotspot(long hotspotsSetId, Hotspot hotspot) throws SQLException {
        final String sql = "INSERT INTO hotspots(hotspotsSet_id, hotspot_id, contig, start, end_, ref, alt, gene, coding_mut, protein_mut, type)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        long id;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, hotspotsSetId);
            stm.setString(++i, hotspot.getHotspotId());
            stm.setString(++i, hotspot.getContig());
            stm.setInt(++i, hotspot.getStart());
            stm.setInt(++i, hotspot.getEnd());
            stm.setString(++i, hotspot.getRef());
            stm.setString(++i, hotspot.getAlt());
            stm.setString(++i, hotspot.getGene());
            stm.setString(++i, hotspot.getCodingMut());
            stm.setString(++i, hotspot.getProteinMut());
            stm.setString(++i, hotspot.getType().name());
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


    public ObservableList<Hotspot> getHotspots(long hotspotsSetId) throws SQLException {
        ObservableList<Hotspot> hotspots = FXCollections.observableArrayList();
        final String sql = "SELECT id, hotspot_id, contig, start, end_, ref, alt, gene, coding_mut, protein_mut, type FROM hotspots WHERE hotspotsSet_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, hotspotsSetId);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String hotspot_id = rs.getString("hotspot_id");
                String contig = rs.getString("contig");
                int start = rs.getInt("start");
                int end = rs.getInt("end_");
                String ref = rs.getString("ref");
                String alt = rs.getString("alt");
                String gene = rs.getString("gene");
                String coding_mut = rs.getString("coding_mut");
                String protein_mut = rs.getString("protein_mut");
                HotspotType type;
                try {
                    type = HotspotType.valueOf(rs.getString("type"));
                } catch (Exception e) {
                    type = HotspotType.REGION;
                }

                Hotspot hotspot = new Hotspot(id, hotspot_id, contig, start, end, ref, alt, gene, coding_mut, protein_mut, type);
                hotspots.add(hotspot);
            }
        }
        return hotspots;
    }


    public int getGeneCount(long hotspotsSetId) throws SQLException {
        final String sql = "SELECT COUNT(id) AS count FROM hotspots WHERE hotspotsSet_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, hotspotsSetId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

}
