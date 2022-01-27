package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.CIQRecordState;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import ngsdiaglim.modeles.users.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;

public class CIQRecordHistoryDAO extends DAO {

    private static final Logger logger = LogManager.getLogger(CIQRecordHistoryDAO.class);

    public long addCIQRecordHistory(CIQVariantRecord record, CIQRecordState oldAccepted, CIQRecordState newAccepted, float mean, float sd) throws SQLException {
        long id;
        final String sql = "INSERT INTO CIQRecordHistory (ciqRecord_id, user_id, user_name, accepted_before, accepted_after, mean, sd, datetime) VALUES (?,?,?,?,?,?,?,?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            User user = App.get().getLoggedUser();
            int i = 0;
            stm.setLong(++i, record.getId());
            stm.setLong(++i, user.getId());
            stm.setString(++i, user.getUsername());
            if (oldAccepted == null) {
                stm.setNull(++i, Types.VARCHAR);
            } else {
                stm.setString(++i, oldAccepted.name());
            }
            stm.setString(++i, newAccepted.name());
            stm.setFloat(++i, mean);
            stm.setFloat(++i, sd);
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));

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


    public ObservableList<CIQRecordHistory> getRecordHistory(long recordId) throws SQLException {
        ObservableList<CIQRecordHistory> ciqRecordHistories = FXCollections.observableArrayList();
        final String sql = "SELECT id, user_id, user_name, accepted_before, accepted_after, mean, sd, datetime, comment FROM CIQRecordHistory WHERE ciqRecord_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, recordId);

            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                CIQRecordState beforeState = CIQRecordState.UNKNOWN;
                CIQRecordState afterState = CIQRecordState.UNKNOWN;
                try {
                    String beforeStr = rs.getString("accepted_before");
                    if (!rs.wasNull()) {
                        beforeState = CIQRecordState.valueOf(beforeStr);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(e);
                }
                try {
                    String afterStr = rs.getString("accepted_after");
                    if (!rs.wasNull()) {
                        afterState = CIQRecordState.valueOf(afterStr);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(e);
                }

                ciqRecordHistories.add(new CIQRecordHistory(
                        rs.getLong("id"),
                        recordId,
                        beforeState,
                        afterState,
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getFloat("mean"),
                        rs.getFloat("sd"),
                        rs.getTimestamp("datetime").toLocalDateTime(),
                        rs.getString("comment")
                ));
            }
        }
        return ciqRecordHistories;
    }


    public void addComment(long recordHistoryId, String comment) throws SQLException {
        final String sql = "UPDATE CIQRecordHistory SET comment=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, comment);
            stm.setLong(++i, recordHistoryId);
            stm.executeUpdate();
        }
    }
}

