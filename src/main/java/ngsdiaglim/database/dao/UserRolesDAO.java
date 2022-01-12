package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.Permission;
import ngsdiaglim.modeles.users.Roles.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRolesDAO extends DAO {

    public void addUserRole(long user_id, Role role) throws SQLException {
        final String sql = "INSERT INTO userRoles (user_id, role_id) VALUES(?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, user_id);
            stm.setLong(++i, role.getId());
            stm.executeUpdate();
        }
    }

    public void removeUserRole(long user_id, Role role) throws SQLException {
        final String sql = "DELETE FROM userRoles WHERE user_id = ? AND role_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, user_id);
            stm.setLong(++i, role.getId());
            stm.executeUpdate();
        }
    }

    public ObservableSet<Role> getRoles(long userId) throws SQLException {
        ObservableSet<Role> roles = FXCollections.observableSet();
        final String sql = "SELECT r.id AS rId, r.role_name AS rName, r.editable AS rEditable FROM roles AS r JOIN userRoles AS ur WHERE ur.role_id= r.id AND ur.user_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long roleId = rs.getLong("rId");
                String roleName = rs.getString("rName");
                boolean isEditable = rs.getBoolean("rEditable");
                ObservableSet<Permission> permissions = DAOController.getRolePermissionsDAO().getPermissions(roleId);
                roles.add(new Role(roleId, roleName, permissions, isEditable));
            }
        }
        return roles;
    }
}
