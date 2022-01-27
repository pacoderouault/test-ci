package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.CIQRecordState;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Comparator;

public class CIQRecordDAO extends DAO {

    private static final Logger logger = LogManager.getLogger(CIQRecordDAO.class);

    public void addCIQRecord(CIQModel ciqModel, CIQHotspot ciqHotspot, long analysisId, int dp, int ao, float vaf) throws SQLException {
        final String sql = "INSERT INTO CIQRecord (ciqModel_id, ciqHotspot_id, analysis_id, dp, ao, vaf) VALUES (?,?,?,?,?,?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, ciqModel.getId());
            stm.setLong(++i, ciqHotspot.getId());
            stm.setLong(++i, analysisId);
            stm.setInt(++i, dp);
            stm.setInt(++i, ao);
            stm.setFloat(++i, vaf);
            stm.executeUpdate();
        }
    }

    public ObservableList<CIQVariantRecord> getCIQRecords(CIQHotspot ciqHotspot) throws SQLException {
        ObservableList<CIQVariantRecord> records = FXCollections.observableArrayList();

        final String sql = "SELECT id, analysis_id, dp, ao, vaf FROM CIQRecord WHERE ciqHotspot_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, ciqHotspot.getId());

            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long recordId = rs.getLong("id");
                long analysisId = rs.getLong("analysis_id");
                int dp = rs.getInt("dp");
                int ao = rs.getInt("ao");
                float vaf = rs.getFloat("vaf");
                Analysis a = DAOController.getAnalysisDAO().getAnalysis(analysisId);
                ObservableList<CIQRecordHistory> history = null;
                try {
                    history = DAOController.getCiqRecordHistoryDAO().getRecordHistory(recordId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                records.add(new CIQVariantRecord(recordId, ciqHotspot, a, dp, ao, vaf, history));
            }
        }
        return records;
    }


    public void updateCIQRecord(CIQHotspot ciqHotspot) throws SQLException {
        final String sql = "UPDATE CIQRecord accepted=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {

        }
    }
}
