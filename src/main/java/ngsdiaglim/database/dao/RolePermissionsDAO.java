package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import ngsdiaglim.modeles.users.Roles.Permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolePermissionsDAO extends DAO {

    public void addRolePermission(long role_id, Permission permission) throws SQLException {
        final String sql = "INSERT INTO rolePermissions (role_id, permission_name) VALUES(?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, role_id);
            stm.setString(++i, permission.getPermissionEnum().name());
            stm.executeUpdate();
        }
    }

    public void removeRolePermission(long role_id, Permission permission) throws SQLException {
        final String sql = "DELETE FROM rolePermissions WHERE role_id=? AND permission_name=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, role_id);
            stm.setString(++i, permission.getPermissionEnum().name());
            stm.executeUpdate();
        }
    }

    public ObservableSet<Permission> getPermissions(long roleId) throws SQLException {
        ObservableSet<Permission> permissions = FXCollections.observableSet();
        final String sql = "SELECT role_id, permission_name FROM rolePermissions WHERE role_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, roleId);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                try {
                    permissions.add(new Permission(rs.getString("permission_name")));
                } catch (IllegalArgumentException ignored) {} // old unused permission
            }
        }
        return permissions;
    }

}
