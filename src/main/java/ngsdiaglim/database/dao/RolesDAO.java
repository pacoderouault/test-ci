package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.Permission;
import ngsdiaglim.modeles.users.Roles.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class RolesDAO extends DAO {

    public boolean roleExists(String rolename) throws SQLException {
        final String sql = "SELECT id FROM roles WHERE role_name=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, rolename);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }

    public void addRole(String rolename) throws SQLException {
        final String sql = "INSERT INTO roles (role_name, editable) VALUES(?, True);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, rolename);
            stm.executeUpdate();
        }
    }

    public void renameRole(Role role, String newName) throws SQLException {
        final String sql = "UPDATE roles SET role_name=? WHERE id = ? AND editable=True;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, newName);
            stm.setLong(++i, role.getId());
            stm.executeUpdate();
        }
    }

    public ObservableList<Role> getRoles() throws SQLException {
        ObservableList<Role> roles = FXCollections.observableArrayList();
        final String sql = "SELECT id, role_name, editable FROM roles;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String roleName = rs.getString("role_name");
                boolean editable = rs.getBoolean("editable");
                Set<Permission> permissions = DAOController.getRolePermissionsDAO().getPermissions(id);
                roles.add(new Role(rs.getLong("id"), roleName, permissions, editable));
            }
        }
        return roles;
    }

    public Role getRole(String roleName) throws SQLException {
        final String sql = "SELECT id, editable FROM roles WHERE role_name = ?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, roleName);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                boolean editable = rs.getBoolean("editable");
                Set<Permission> permissions = DAOController.getRolePermissionsDAO().getPermissions(id);
                return new Role(rs.getLong("id"), roleName, permissions, editable);
            }
        }
        return null;
    }

    public void deleteRole(Role role) throws SQLException {
        final String sql = "DELETE FROM roles WHERE id=? AND editable=True;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, role.getId());
            stm.executeUpdate();
        }
    }
}
