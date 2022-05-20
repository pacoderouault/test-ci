package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.Role;
import ngsdiaglim.modeles.users.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserRolesDAOTest extends BaseSetup {
    private static final UsersDAO usersDAO = DAOController.getUsersDAO();
    private static final RolesDAO rolesDAO = DAOController.getRolesDAO();
    private static final UserRolesDAO userRolesDAO = DAOController.getUserRolesDAO();

    @BeforeAll
    static void insertUsers() throws SQLException {
        // add users
        usersDAO.addUser("username1", "password", FXCollections.observableSet(), null);
        usersDAO.addUser("username2", "password", FXCollections.observableSet(), null);
        usersDAO.addUser("username3", "password", FXCollections.observableSet(), null);

        // add roles
        rolesDAO.addRole("role1");
        rolesDAO.addRole("role2");
        rolesDAO.addRole("role3");
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

        for (Role r : rolesDAO.getRoles()) {
            rolesDAO.deleteRole(r);
        }
    }

    @Test
    void addUserRole() throws SQLException {
        User user1 = usersDAO.getUser("username1");
        Role role1 = rolesDAO.getRole("role1");
        userRolesDAO.addUserRole(user1.getId(), role1);
        assertTrue(userRolesDAO.getRoles(user1.getId()).contains(role1));
        userRolesDAO.removeUserRole(user1.getId(), role1);
        assertFalse(userRolesDAO.getRoles(user1.getId()).contains(role1));
    }

//    @Test
//    void removeUserRole() throws SQLException {
//        User user2 = usersDAO.getUser("username2");
//        Role role2 = rolesDAO.getRole("role2");
//        userRolesDAO.addUserRole(user2.getId(), role2);
//        assertTrue(userRolesDAO.getRoles(user1.getId()).contains(role1));
//    }

//    @Test
//    void getRoles() {
//
//    }
}