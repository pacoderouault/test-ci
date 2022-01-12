package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.PasswordAuthentication;
import ngsdiaglim.modeles.users.Roles.Role;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.users.UserPreferences;

import java.sql.*;
import java.time.LocalDate;
import java.util.Set;

public class UsersDAO extends DAO {

    public boolean userExists(String username) throws SQLException {
        final String sql = "SELECT id FROM users WHERE lower(username)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public void addUser(String username, String password, Set<Role> roles) throws SQLException {
        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        String hash = passwordAuthentication.createHash(password);
        long user_id;
        final String sql = "INSERT INTO users (username, password, creation_date, is_active, preferences) VALUES (?, ?, NOW(), True, '');";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, username.trim());
            stm.setString(++i, hash);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                user_id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No user inserted in the database");
            }
        }

        // Set roles for the user
        if (user_id >= 0) {
            if (roles != null) {
                for (Role role : roles) {
                    final String sql_role = "INSERT INTO userRoles (user_id, role_id) VALUES(?, ?);";
                    try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql_role)) {
                        int i = 0;
                        stm.setLong(++i, user_id);
                        stm.setLong(++i, role.getId());
                        stm.executeUpdate();
                    }
                }
            }
        }
    }


    /**
     * Check if a user is the last admin in the database
     */
    public boolean isLastAdmin(String username) throws SQLException {
        final String sql = "SELECT r.id AS roleId, r.role_name AS roleName, ur.user_id AS userId, u.username AS userName " +
                "FROM users AS u " +
                "JOIN userRoles AS ur " +
                "JOIN roles AS r " +
                "WHERE u.id = ur.user_id AND r.id = ur.role_id AND r.role_name = 'Admin' AND is_active=True;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            int admin_Nb = 0;
            boolean user_found = false;
            while (rs.next()) {
                admin_Nb += 1;
                String name = rs.getString("userName");
                if (name.equals(username)) {
                    user_found = true;
                }
            }
            return admin_Nb == 1 && user_found;
        }
    }

    public User checkUserConnection(String loginName, String loginPassword) throws SQLException {
        loginName = loginName.trim();
        String sql = "SELECT id, username, password, creation_date, is_active, preferences FROM users WHERE lower(username)=lower(?) AND is_active=True;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, loginName);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                // check password
                String password = rs.getString("password");
                PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
                if (passwordAuthentication.validatePassword(loginPassword, password)) {

                    // create user
                    long userId = rs.getLong("id");
                    String username = rs.getString("username");
                    LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                    UserPreferences preferences = new UserPreferences(rs.getString("preferences"));
                    ObservableSet<Role> userRoles = DAOController.getUserRolesDAO().getRoles(userId);
                    User user = new User(userId, username, userRoles, creationDate);
                    user.setPreferences(preferences);
                    return user;
                }
            }
            return null;
        }
    }


    public User getUser(String username) throws SQLException {
        username = username.trim();
        final String sql = "SELECT id, username, creation_date, preferences FROM users WHERE lower(username)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("id");
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                UserPreferences preferences = new UserPreferences(rs.getString("preferences"));
                ObservableSet<Role> userRoles = DAOController.getUserRolesDAO().getRoles(userId);
                User user = new User(userId, username, userRoles, creationDate);
                user.setPreferences(preferences);
                return user;
            }
        }
        return null;
    }

    public ObservableList<User> getUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        final String sql = "SELECT id, username, creation_date, preferences FROM users ORDER BY creation_date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long userId = rs.getLong("id");
                String username = rs.getString("username");
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                UserPreferences preferences = new UserPreferences(rs.getString("preferences"));
                ObservableSet<Role> userRoles = DAOController.getUserRolesDAO().getRoles(userId);

                User user = new User(userId, username, userRoles, creationDate);
                user.setPreferences(preferences);
                users.add(user);
            }
        }
        return users;
    }

    public ObservableList<User> getActiveUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        final String sql = "SELECT id, username, creation_date, preferences FROM users WHERE is_active=True ORDER BY creation_date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long userId = rs.getLong("id");
                String username = rs.getString("username");
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                UserPreferences preferences = new UserPreferences(rs.getString("preferences"));
                ObservableSet<Role> userRoles = DAOController.getUserRolesDAO().getRoles(userId);

                User user = new User(userId, username, userRoles, creationDate);
                user.setPreferences(preferences);
                users.add(user);
            }
        }
        return users;
    }


    public void inactiveUser(long user_id) throws SQLException {
        final String sql = "UPDATE users SET is_active=False WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, user_id);
            stm.executeUpdate();
        }
    }


    public void activeUser(long user_id) throws SQLException {
        final String sql = "UPDATE users SET is_active=True WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, user_id);
            stm.executeUpdate();
        }
    }

    public void updateUsername(User user) throws SQLException {
        final String sql = "UPDATE users SET username=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i= 0;
            stm.setString(++i, user.getUsername().trim());
            stm.setLong(++i, user.getId());
            stm.executeUpdate();
        }
    }

    public void updatePreferences(User user) throws SQLException {
        final String sql = "UPDATE users SET preferences=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i= 0;
            stm.setString(++i, user.getPreferences().getPreferencesAsString());
            stm.setLong(++i, user.getId());
            stm.executeUpdate();
        }
    }

    public void updatePassword(User user, String newPassword) throws SQLException{
        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        String hash = passwordAuthentication.createHash(newPassword);
        final String sql = "UPDATE users SET password=? WHERE id = ?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, hash);
            stm.setLong(++i, user.getId());
            stm.executeUpdate();
        }
    }
}
