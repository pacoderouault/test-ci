package ngsdiaglim.database.dao;

import javafx.collections.ObservableSet;
import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RolePermissionsDAOTest extends BaseSetup {

    private static final RolePermissionsDAO rolePermissionsDAO = DAOController.getRolePermissionsDAO();

    /**
     * Clear the users table
     * @throws SQLException
     */
    @AfterAll
    static void removesRoles() throws SQLException {
        for (Role r : DAOController.getRolesDAO().getRoles()) {
            DAOController.getRolesDAO().deleteRole(r);
        }
    }

    @Test
    void addRolePermission() throws SQLException {
        DAOController.getRolesDAO().addRole("role1");
        Role role1 = DAOController.getRolesDAO().getRole("role1");
        Permission p = new Permission(PermissionsEnum.ADD_RUN);
        rolePermissionsDAO.addRolePermission(role1.getId(), p);
        assertTrue(rolePermissionsDAO.getPermissions(role1.getId()).contains(p));

    }


    @Test
    void removeRolePermission() throws SQLException {
        DAOController.getRolesDAO().addRole("role2");
        Role role2 = DAOController.getRolesDAO().getRole("role2");
        Permission p = new Permission(PermissionsEnum.EDIT_ANALYSE);
        rolePermissionsDAO.addRolePermission(role2.getId(), p);
        assertTrue(rolePermissionsDAO.getPermissions(role2.getId()).contains(p));

        rolePermissionsDAO.removeRolePermission(role2.getId(), p);
        assertFalse(rolePermissionsDAO.getPermissions(role2.getId()).contains(p));
    }

    @Test
    void getPermissions() throws SQLException {
        List<Permission> availablePermissions = new ArrayList<>();
        Arrays.stream(PermissionsEnum.values()).forEach(penum -> availablePermissions.add(new Permission(penum)));

        Role adminRole = DAOController.getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name());
        Role guestRole = DAOController.getRolesDAO().getRole(DefaultRolesEnum.GUEST.name());

        ObservableSet<Permission> adminPermissionList = rolePermissionsDAO.getPermissions(adminRole.getId());
        assertTrue(adminPermissionList.containsAll(availablePermissions));

        ObservableSet<Permission> guestPermissionList = rolePermissionsDAO.getPermissions(guestRole.getId());
        assertFalse(guestPermissionList.stream().anyMatch(availablePermissions::contains));
    }

}