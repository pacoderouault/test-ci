package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.AnnotationCommentary;

import java.sql.*;
import java.time.LocalDateTime;

public class AnnotationCommentaryDAO extends DAO {

    public void addAnnotationCommentary(long variantId, long analysis_id, String comment) throws SQLException {
        final String sql = "INSERT INTO annotationCommentary(variant_id, analysis_id, user_id, user_name, date, commentary) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            User user = App.get().getLoggedUser();
            stm.setLong(++i, variantId);
            stm.setLong(++i, analysis_id);
            stm.setLong(++i, user.getId());
            stm.setString(++i, user.getUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);
            stm.executeUpdate();
        }
    }


    public void updateAnnotationCommentary(long id, String comment) throws SQLException {
        final String sql = "UPDATE annotationCommentary SET date=?, commentary=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public void deleteAnnotationCommentary(long id) throws SQLException {
        final String sql = "DELETE FROM annotationCommentary WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public ObservableList<AnnotationCommentary> getAnnotationCommentaries(long variant_id, long analysis_id) throws SQLException {
        ObservableList<AnnotationCommentary> commentaries = FXCollections.observableArrayList();
        final String sql = "SELECT id, user_id, user_name, date, commentary FROM annotationCommentary WHERE variant_id=? AND analysis_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, variant_id);
            stm.setLong(2, analysis_id);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                AnnotationCommentary vc = new AnnotationCommentary(
                        rs.getLong("id"),
                        variant_id,
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
