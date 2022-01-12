package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.AnalysisCommentary;
import ngsdiaglim.modeles.users.User;

import java.sql.*;
import java.time.LocalDateTime;

public class AnalysisCommentaryDAO extends DAO {

    public void addAnalysisCommentary(long analysisId, String comment) throws SQLException {
        final String sql = "INSERT INTO analysisCommentary(analysis_id, user_id, user_name, date, commentary) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            User user = App.get().getLoggedUser();
            stm.setLong(++i, analysisId);
            stm.setLong(++i, user.getId());
            stm.setString(++i, user.getUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);
            stm.executeUpdate();
        }
    }


    public void addAnalysisCommentary(long analysisId, String comment, LocalDateTime date, User user) throws SQLException {
        final String sql = "INSERT INTO analysisCommentary(analysis_id, user_id, user_name, date, commentary) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;

            stm.setLong(++i, analysisId);
            stm.setLong(++i, user.getId());
            stm.setString(++i, user.getUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(date));
            stm.setString(++i, comment);
            stm.executeUpdate();
        }
    }


    public void updateAnalysisCommentary(long id, String comment) throws SQLException {
        final String sql = "UPDATE analysisCommentary SET date=?, commentary=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public void deleteAnalysisCommentary(long id) throws SQLException {
        final String sql = "DELETE FROM analysisCommentary WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public ObservableList<AnalysisCommentary> getVariantCommentaries(long analysis_id) throws SQLException {
        ObservableList<AnalysisCommentary> commentaries = FXCollections.observableArrayList();
        final String sql = "SELECT id, user_id, user_name, date, commentary FROM analysisCommentary WHERE analysis_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, analysis_id);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                AnalysisCommentary vc = new AnalysisCommentary(
                        rs.getLong("id"),
                        analysis_id,
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getString("commentary"),
                        rs.getTimestamp("date").toLocalDateTime()
                );
                commentaries.add(vc);
            }
        }
        return commentaries;
    }
}
