package ngsdiaglim.modeles.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import ngsdiaglim.modeles.users.Roles.Permission;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.Roles.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static User user1;
    private static User user2;
    private static final ObservableSet<Role> roles1 = FXCollections.observableSet();
    private static final ObservableSet<Role> roles2 = FXCollections.observableSet();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @BeforeAll
    static void initUsers() {
        roles1.add(
                new Role(
                        1,
                        "role1",
                        new HashSet<>(Arrays.asList(
                            new Permission(PermissionsEnum.ADD_RUN),
                            new Permission(PermissionsEnum.EDIT_RUN))),
                        true
                )

        );
        roles2.add(
                new Role(
                        1,
                        "role1",
                        new HashSet<>(Arrays.asList(
                                new Permission(PermissionsEnum.ADD_ANALYSE),
                                new Permission(PermissionsEnum.EDIT_ANALYSE))),
                        true
                )

        );
        user1 = new User(1, "user1", roles1, LocalDate.parse("01/04/1987", dateTimeFormatter));
        user2 = new User(2, "user2", roles2, LocalDate.parse("08/08/2001", dateTimeFormatter), false);
    }

    @Test
    void getId() {
        assertEquals(1, user1.getId());
        assertEquals(2, user2.getId());
    }

    @Test
    void getUsername() {
        assertEquals("user1", user1.getUsername());
        assertEquals("user2", user2.getUsername());
    }

    @Test
    void setUsername() {
        User u = new User(1, "user", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        u.setUsername("userRenamed");
        assertEquals("userRenamed", u.getUsername());
    }

    @Test
    void getRoles() {
        assertEquals(roles1, user1.getRoles());
    }

    @Test
    void setRoles() {
        User u = new User(1, "user", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        u.setRoles(roles1);
        assertEquals(roles1, u.getRoles());
    }

    @Test
    void hasRole() {
        Role r = new Role(
                1,
                "role1",
                new HashSet<>(Arrays.asList(
                        new Permission(PermissionsEnum.ADD_RUN),
                        new Permission(PermissionsEnum.EDIT_RUN))),
                true
        );
        Role r2 = new Role(
                2,
                "role2",
                new HashSet<>(Collections.singletonList(
                        new Permission(PermissionsEnum.MANAGE_ACCOUNT)
                )),
                true);
        assertTrue(user1.hasRole(r));
        assertFalse(user1.hasRole(r2));
    }

    @Test
    void isPermitted() {
        assertTrue(user1.isPermitted(new Permission(PermissionsEnum.ADD_RUN)));
        assertFalse(user1.isPermitted(new Permission(PermissionsEnum.MANAGE_ACCOUNT)));
    }

    @Test
    void getCreationDate() {
        assertEquals(user1.getCreationDate(), LocalDate.parse("01/04/1987", dateTimeFormatter));
    }

    @Test
    void getActive() {
        System.out.println(user1.isActive());
        assertTrue(user1.isActive());
        assertFalse(user2.isActive());
    }

    @Test
    void setActive() {
        User u = new User(1, "user", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        u.setActive(false);
        assertFalse(u.isActive());

        User u2 = new User(1, "user", null, LocalDate.parse("01/04/1987", dateTimeFormatter), false);
        u2.setActive(true);
        assertTrue(u2.isActive());
    }

    @Test
    void testEquals() {
        User u1 = new User(1, "user1", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        User u2 = new User(1, "user2", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        User u3 = new User(2, "user1", null, LocalDate.parse("01/04/1987", dateTimeFormatter));

        assertEquals(u1, u1);
        assertEquals(u1, u2);
        assertNotEquals(u1, null);
        assertNotEquals(u1, u3);
        assertNotEquals(u2, u3);

    }

    @Test
    void testHashCode() {
        User u1 = new User(1, "user1", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        User u2 = new User(1, "user2", null, LocalDate.parse("01/04/1987", dateTimeFormatter));
        User u3 = new User(2, "user1", null, LocalDate.parse("01/04/1987", dateTimeFormatter));

        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1.hashCode(), u3.hashCode());
        assertNotEquals(u2.hashCode(), u3.hashCode());
    }
}