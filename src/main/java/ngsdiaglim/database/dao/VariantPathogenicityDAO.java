package ngsdiaglim.database.dao;

import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.variants.VariantPathogenicity;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VariantPathogenicityDAO extends DAO {

    public long addVariantPathogenicity(VariantPathogenicity vp) throws SQLException {
        long id;
        final String sql = "INSERT INTO variantPathogenicity " +
                "(variant_id, pathogenicity_score, user_id, user_name, date, validation_userid, validation_username, validation_date, commentary)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, vp.getVariantId());
            stm.setInt(++i, vp.getAcmg().getPathogenicityValue());
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
                throw new SQLException("No pathogenicity inserted in the database");
            }

        }
        return id;

    }

    public void updateVariantPathogenicity(VariantPathogenicity vp) throws SQLException {
        final String sql = "UPDATE variantPathogenicity " +
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


    public List<VariantPathogenicity> getVariantPathogenicityHistory(long variantId) throws SQLException {
        List<VariantPathogenicity> history = new ArrayList<>();
        final String sql = "SELECT id, pathogenicity_score, user_id, user_name, date, validation_userid, validation_username, validation_date, commentary " +
                "FROM variantPathogenicity WHERE variant_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, variantId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                LocalDateTime validationDate = null;
                Timestamp validationTimestamp = rs.getTimestamp("validation_date");
                if (validationTimestamp != null) {
                    validationDate = validationTimestamp.toLocalDateTime();
                }
                VariantPathogenicity vp = new VariantPathogenicity(
                        rs.getLong("id"),
                        variantId,
                        ACMG.getFromPathogenicityValue(rs.getInt("pathogenicity_score")),
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
