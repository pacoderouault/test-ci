package ngsdiaglim.database.dao;

import ngsdiaglim.modeles.ciq.CIQModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CIQAnalysisDAO extends DAO {

    public void addAnalysisCIQ(long analysis_id, long ciq_id) throws SQLException {
        final String sql = "INSERT INTO CIQAnalysis (analysis_id, ciq_id) VALUES(?,?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, analysis_id);
            stm.setLong(++i, ciq_id);
            stm.executeUpdate();
        }
    }

    public int countCIQ(long id) throws SQLException {
        final String sql = "SELECT COUNT analysis_id AS c FROM CIQAnalysis WHERE ciq_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("c");
            }
            return 0;
        }
    }


    public boolean CIQisUsed(long id) throws SQLException {
        final String sql = "SELECT analysis_id FROM CIQAnalysis WHERE ciq_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }

    public boolean analysisIsCIQ(long analysis_id) throws SQLException {
        final String sql = "SELECT ciq_id FROM CIQAnalysis WHERE analysis_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, analysis_id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }

    public CIQModel getCIQModel(long analysis_id) throws SQLException {
        final String sql = "SELECT A.ciq_id AS aId, C.name AS cName, C.barcode AS cBarcode, C.is_active AS cIsActive FROM CIQAnalysis AS A JOIN CIQModel AS C WHERE A.ciq_id=C.id AND A.analysis_id=? LIMIT 1;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, analysis_id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new CIQModel(
                        rs.getLong("aId"),
                        rs.getString("cName"),
                        rs.getString("cBarcode"),
                        rs.getBoolean("cIsActive")
                );
            }
        }
        return null;
    }
}
