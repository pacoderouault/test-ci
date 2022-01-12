package ngsdiaglim.database.dao;

import ngsdiaglim.modeles.variants.VariantFalsePositive;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VariantFalsePositiveDAO extends DAO {

    public long addVariantFalsePositive(VariantFalsePositive vp) throws SQLException {
        long id;
        final String sql = "INSERT INTO variantFalsePositive " +
                "(variant_id, false_positive, user_id, user_name, date, validation_userid, validation_username, validation_date, commentary)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, vp.getVariantId());
            stm.setBoolean(++i, vp.isFalsePositive());
            stm.setLong(++i, vp.getUserId());
            stm.setString(++i, vp.getUserName());
            stm.setTimestamp(++i, Timestamp.valueOf(vp.getDateTime()));
            stm.setLong(++i, vp.getVerifiedUserId());
            stm.setString(++i, vp.getVerifiedUsername());
            stm.setTimestamp(++i, vp.getVerifiedDateTime() == null ? null : Timestamp.valueOf(vp.getVerifiedDateTime()));
            stm.setString(++i, vp.getCommentary());

            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No false positive inserted in the database");
            }

        }
        return id;

    }

    public void updateVariantFalsePositive(VariantFalsePositive vp) throws SQLException {
        final String sql = "UPDATE variantFalsePositive " +
                "SET validation_userid=?, validation_username=?, validation_date=? " +
                "WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, vp.getVerifiedUserId());
            stm.setString(++i, vp.getVerifiedUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(vp.getVerifiedDateTime()));
            stm.setLong(++i, vp.getVariantId());
            stm.executeUpdate();
            stm.executeUpdate();
        }
    }


    public List<VariantFalsePositive> getVariantFalsePositiveHistory(long variantId) throws SQLException {
        List<VariantFalsePositive> history = new ArrayList<>();
        final String sql = "SELECT id, false_positive, user_id, user_name, date, validation_userid, validation_username, validation_date, commentary " +
                "FROM variantFalsePositive WHERE variant_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, variantId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                LocalDateTime validationDate = null;
                Timestamp validationTimestamp = rs.getTimestamp("validation_date");
                if (validationTimestamp != null) {
                    validationDate = validationTimestamp.toLocalDateTime();
                }
                VariantFalsePositive vp = new VariantFalsePositive(
                        rs.getLong("id"),
                        variantId,
                        rs.getBoolean("false_positive"),
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getLong("validation_userid"),
                        rs.getString("validation_username"),
                        validationDate,
                        rs.getString("commentary")
                );
                history.add(vp);
            }
        }
        return history;
    }
}
