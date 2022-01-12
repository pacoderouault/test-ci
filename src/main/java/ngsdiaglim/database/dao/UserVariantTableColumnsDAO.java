package ngsdiaglim.database.dao;

import ngsdiaglim.modeles.users.UserVariantTableColumns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserVariantTableColumnsDAO extends DAO {

    public boolean userHasColumns(long id) throws SQLException {
        final String sql = "SELECT id FROM usersVariantTableColumnTable WHERE user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public void addUsersVariantTableColumn(long user_id, UserVariantTableColumns userVariantTableColumns) throws SQLException {
        final String sql = "INSERT INTO usersVariantTableColumnTable (user_id, columns_order, columns_visibility, columns_size) VALUES (?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, user_id);
            stm.setString(++i, userVariantTableColumns.getColumnsOrderAsString());
            stm.setString(++i, userVariantTableColumns.getColumnsVisibilityAsString());
            stm.setString(++i, userVariantTableColumns.getColumnsSizeAsString());

            stm.executeUpdate();
        }
    }


    public void updateUsersVariantTableColumn(long user_id, UserVariantTableColumns userVariantTableColumns) throws SQLException {
        final String sql = "UPDATE usersVariantTableColumnTable SET columns_order=?, columns_visibility=?, columns_size=? WHERE user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, userVariantTableColumns.getColumnsOrderAsString());
            stm.setString(++i, userVariantTableColumns.getColumnsVisibilityAsString());
            stm.setString(++i, userVariantTableColumns.getColumnsSizeAsString());
            stm.setLong(++i, user_id);

            stm.executeUpdate();
        }
    }


    public UserVariantTableColumns getUsersVariantTableColumn(long user_id) throws SQLException {
        final String sql = "SELECT columns_order, columns_visibility, columns_size FROM usersVariantTableColumnTable WHERE user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, user_id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new UserVariantTableColumns(
                        rs.getString("columns_visibility"),
                        rs.getString("columns_size"),
                        rs.getString("columns_order")
                );
            }
            return null;
        }
    }
}
