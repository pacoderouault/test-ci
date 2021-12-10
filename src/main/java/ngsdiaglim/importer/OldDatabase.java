package ngsdiaglim.importer;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.Roles.DefaultRolesEnum;
import ngsdiaglim.modeles.users.Roles.Role;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OldDatabase {

    private static OldDatabase instance = new OldDatabase();

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String USER = "admin";
    private static final String PASS = "JWDD;{auEk)p$2[r .:?d5Z6Cce:h8}A";

    private JdbcConnectionPool cp;

    private OldDatabase() {
        try {
            Class.forName(JDBC_DRIVER).newInstance();
            String ddbPath = "/mnt/Data/dev/IdeaProjects/ngsbgm/ddb/toupdate/NGSbgm";
            System.out.println(ddbPath);
            cp = JdbcConnectionPool.create("jdbc:h2:" + ddbPath + ";AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE", USER, PASS);
            cp.setMaxConnections(20);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static OldDatabase instance() {
        if (instance == null) {
            instance = new OldDatabase();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return cp.getConnection();
    }


    public void test(String userName) throws SQLException {
        String login = userName.replaceAll(" ", "").trim().toLowerCase();
        String sql = "SELECT id, name FROM user WHERE name = ?";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, login);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString(2);
                System.out.println(name.trim().equalsIgnoreCase(userName));
            }
        }
        System.out.println();
    }

    public void importUsersFromDb() throws SQLException {
        String sql = "SELECT id, name, is_admin, rank, date FROM user ORDER BY name DESC;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                String name = rs.getString(2);
                boolean is_admin = rs.getBoolean(3);

                if (!DAOController.get().getUsersDAO().userExists(name)) {
                    Set<Role> roles = new HashSet<>();
                    if (is_admin) {
                        roles.add(DAOController.get().getRolesDAO().getRole(DefaultRolesEnum.ADMIN.name()));
                    } else {
                        roles.add(DAOController.get().getRolesDAO().getRole(DefaultRolesEnum.GUEST.name()));
                    }
                    DAOController.get().getUsersDAO().addUser(name, name, roles);
                }
            }
        }
    }
}
