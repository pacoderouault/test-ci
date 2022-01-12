package ngsdiaglim.database.dao;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Role;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RolesDAOTest extends BaseSetup {

    private static final RolesDAO rolesDAO = DAOController.getRolesDAO();

    @BeforeAll
    static void insertRoles() throws SQLException {
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
    static void removesRoles() throws SQLException {
        for (Role r : rolesDAO.getRoles()) {
            rolesDAO.deleteRole(r);
        }
    }


    @Test
    void roleExists() throws SQLException {
        assertTrue(rolesDAO.roleExists("role1"));
        assertTrue(rolesDAO.roleExists("role2"));
        assertFalse(rolesDAO.roleExists("unexistsRole"));
        assertTrue(rolesDAO.roleExists(DefaultRolesEnum.ADMIN.name()));
        assertTrue(rolesDAO.roleExists(DefaultRolesEnum.GUEST.name()));
    }

    @Test
    void renameRole() throws SQLException {
        rolesDAO.addRole("roleToRenamed");
        Role r = rolesDAO.getRole("roleToRenamed");
        rolesDAO.renameRole(r, "roleRenamed");
        assertEquals(rolesDAO.getRole("roleRenamed").getId(), r.getId());
    }

    @Test
    void getRole() throws SQLException {
        Role r = rolesDAO.getRole("role2");
        assertNotNull(r);
        Role r2 = rolesDAO.getRole("roleUnexists");
        assertNull(r2);
    }

    @Test
    void deleteRole() throws SQLException {
        int rolesNb = rolesDAO.getRoles().size();
        Role r = rolesDAO.getRole("role3");
        rolesDAO.deleteRole(r);
        assertFalse(rolesDAO.roleExists("role3"));
        assertEquals(rolesNb - 1, rolesDAO.getRoles().size());
    }
}