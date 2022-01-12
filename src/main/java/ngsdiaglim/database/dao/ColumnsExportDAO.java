package ngsdiaglim.database.dao;

import ngsdiaglim.modeles.users.ColumnsExport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnsExportDAO extends DAO {

    public boolean userHasColumns(long id) throws SQLException {
        final String sql = "SELECT id FROM columnsExport WHERE user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public void addColumnsExport(long user_id, ColumnsExport columnsExport) throws SQLException {
        final String sql = "INSERT INTO columnsExport (user_id, columns) VALUES (?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, user_id);
            stm.setString(++i, columnsExport.getColumnsAsString());
            stm.executeUpdate();
        }
    }

    public void setColumnsExport(long user_id, ColumnsExport columnsExport) throws SQLException {
        if (userHasColumns(user_id)) {
            updateColumnsExport(user_id, columnsExport);
        }
        else {
            addColumnsExport(user_id, columnsExport);
        }
    }

    public void updateColumnsExport(long user_id, ColumnsExport columnsExport) throws SQLException {
        final String sql = "UPDATE columnsExport SET columns=? WHERE user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, columnsExport.getColumnsAsString());
            stm.setLong(++i, user_id);
            stm.executeUpdate();
        }
    }


    public ColumnsExport getColumnsExport(long user_id) throws SQLException {
        final String sql = "SELECT columns FROM columnsExport WHERE user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, user_id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new ColumnsExport(rs.getString("columns"));
            }
            return null;
        }
    }

}
