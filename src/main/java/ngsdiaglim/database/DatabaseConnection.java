package ngsdiaglim.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    private static DatabaseConnection instance = new DatabaseConnection();

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DBC_CONNECTION = "jdbc:h2:./database/test";
    private static final String USER = "admin";
    private static final String PASS = "JWDD;{auEk)p$2[r .:?d5Z6Cce:h8}A";

    private JdbcConnectionPool cp;

    private DatabaseConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            cp = JdbcConnectionPool.create(DBC_CONNECTION + ";AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE", USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.fatal("Error when connecting to the database", e);
        }
    }

    public static DatabaseConnection instance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return cp.getConnection();
    }

    public void dispose() {
        cp.dispose();
    }

}
