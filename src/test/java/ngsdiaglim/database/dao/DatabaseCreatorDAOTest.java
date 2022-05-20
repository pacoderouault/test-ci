package ngsdiaglim.database.dao;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseCreatorDAOTest extends BaseSetup {

    @Test
    void createTables() throws SQLException {
        Assertions.assertTrue(DAOController.getUsersDAO().userExists("admin"));
        assertEquals(1, DAOController.getUsersDAO().getUsers(null).size());
        assertEquals(1, DAOController.getUsersDAO().getActiveUsers().size());

        assertNotNull(DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name()));
        assertNotNull(DAOController.getRolesDAO().getRole(DefaultRolesEnum.GUEST.name()));
    }
}
