package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.VariantCommentary;

import java.sql.*;
import java.time.LocalDateTime;

public class VariantCommentaryDAO extends DAO {

    public void addVariantCommentary(long variantId, String comment) throws SQLException {
        final String sql = "INSERT INTO variantCommentary(variant_id, user_id, user_name, date, commentary) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            User user = App.get().getLoggedUser();
            stm.setLong(++i, variantId);
            stm.setLong(++i, user.getId());
            stm.setString(++i, user.getUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);
            stm.executeUpdate();
        }
    }


    public void addVariantCommentary(long variantId, User user, String comment, LocalDateTime date) throws SQLException {
        final String sql = "INSERT INTO variantCommentary(variant_id, user_id, user_name, date, commentary) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, variantId);
            stm.setLong(++i, user.getId());
            stm.setString(++i, user.getUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(date));
            stm.setString(++i, comment);
            stm.executeUpdate();
        }
    }


    public void updateVariantCommentary(long id, String comment) throws SQLException {
        final String sql = "UPDATE variantCommentary SET date=?, commentary=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public void deleteVariantCommentary(long id) throws SQLException {
        final String sql = "DELETE FROM variantCommentary WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public ObservableList<VariantCommentary> getVariantCommentaries(long variant_id) throws SQLException {
        ObservableList<VariantCommentary> commentaries = FXCollections.observableArrayList();
        final String sql = "SELECT id, user_id, user_name, date, commentary FROM variantCommentary WHERE variant_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, variant_id);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                VariantCommentary vc = new VariantCommentary(
                        rs.getLong("id"),
                        variant_id,
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

    public ObservableList<VariantCommentary> getUserVariantCommentaries(long variant_id, long user_id) throws SQLException {
        ObservableList<VariantCommentary> commentaries = FXCollections.observableArrayList();
        final String sql = "SELECT id, user_name, date, commentary FROM variantCommentary WHERE variant_id=? AND user_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, variant_id);
            stm.setLong(++i, user_id);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                VariantCommentary vc = new VariantCommentary(
                        rs.getLong("id"),
                        variant_id,
                        user_id,
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
