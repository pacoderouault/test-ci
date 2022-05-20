package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Role;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.users.UserPreferences;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UsersDAOTest extends BaseSetup {
    private static final UsersDAO usersDAO = DAOController.getUsersDAO();

    @BeforeAll
    static void insertUsers() throws SQLException {
        usersDAO.addUser("username1", "password", FXCollections.observableSet(), null);
        usersDAO.addUser("username2", "password", FXCollections.observableSet(), null);
        usersDAO.addUser("username3", "password", FXCollections.observableSet(), null);
        User user3 = usersDAO.getUser("username3");
        usersDAO.inactiveUser(user3.getId());
    }

    /**
     * Clear the users table
     * @throws SQLException
     */
    @AfterAll
    static void removeUsers() throws SQLException {
        DAO dao = new DAO();
        final String sql = "DELETE FROM users WHERE username != 'admin'";
        try (Connection connection = dao.getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.executeUpdate();
        }
    }

    @Test
    void userExists() throws SQLException {
        assertTrue(usersDAO.userExists("username1"));
        assertTrue(usersDAO.userExists("username2"));
        assertFalse(usersDAO.userExists("username4"));
    }

    @Test
    void addUser() throws SQLException {
        assertThrows(JdbcSQLIntegrityConstraintViolationException.class, () -> usersDAO.addUser("username1", "password", null, null));
        assertNotNull(usersDAO.getUser("   username1  "));
    }

    @Test
    void isLastAdmin() throws SQLException {
        assertTrue(usersDAO.isLastAdmin("admin"));
        User user = usersDAO.getUser("username1");
        Role adminRole = DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name());
        DAOController.getUserRolesDAO().addUserRole(user.getId(), adminRole);
        assertFalse(usersDAO.isLastAdmin("admin"));
    }

    @Test
    void checkUserConnection() throws SQLException {
        assertNotNull(usersDAO.checkUserConnection("username1", "password"));
        assertNotNull(usersDAO.checkUserConnection("UseRNaMe1", "password"));
        assertNotNull(usersDAO.checkUserConnection("  UseRNaMe1 ", "password"));
        assertNull(usersDAO.checkUserConnection("username1", "bad_password"));
    }

    @Test
    void getUser() throws SQLException {
        assertNotNull(usersDAO.getUser("AdmIn"));
        assertNotNull(usersDAO.getUser("  AdmIn  "));
    }

    @Test
    void getUsers() throws SQLException {
        assertEquals(4, usersDAO.getUsers(null).size());
        User user = usersDAO.getUser("admin");
        assertEquals(1, user.getId());
        assertEquals("admin", user.getUsername());
    }

    @Test
    void getActiveUsers() throws SQLException {
        assertEquals(3, usersDAO.getActiveUsers().size());
    }

    @Test
    void inactiveUser() throws SQLException {
        User user = usersDAO.getUser("username2");
        usersDAO.inactiveUser(user.getId());
        assertEquals(2, usersDAO.getActiveUsers().size());
    }

    @Test
    void activeUser() throws SQLException {
        User user = usersDAO.getUser("username2");
        usersDAO.activeUser(user.getId());
        assertEquals(3, usersDAO.getActiveUsers().size());
    }

    @Test
    void updateUsername() throws SQLException {
        User user = usersDAO.getUser("username3");
        user.setUsername("newname");
        usersDAO.updateUsername(user);
        assertEquals(user, usersDAO.getUser("newname"));
    }

    @Test
    void updatePreferences() throws SQLException {
        User user = usersDAO.getUser("username1");
        String pref = DefaultPreferencesEnum.FULL_SCREEN.name() + ":" + true + ";" + DefaultPreferencesEnum.INITIAL_DIR.name() + ":/path/to/dir";
        user.setPreferences(new UserPreferences(pref));
        usersDAO.updatePreferences(user);

        User user2 = usersDAO.getUser("username1");
        assertEquals("/path/to/dir", user2.getPreferences().getProperty(DefaultPreferencesEnum.INITIAL_DIR.name()));
        assertEquals("true", user2.getPreferences().getProperty(DefaultPreferencesEnum.FULL_SCREEN.name()));

    }

}