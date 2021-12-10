package ngsdiaglim.database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    private static DatabaseConnection instance = new DatabaseConnection();
    private static Datasource datasource;
//    private static final String JDBC_DRIVER = "org.h2.Driver";
//    private static final String DBC_CONNECTION = "jdbc:h2:./database/test";
//    private static final String USER = "admin";
//    private static final String PASS = "JWDD;{auEk)p$2[r .:?d5Z6Cce:h8}A";

    private JdbcConnectionPool cp;

    private DatabaseConnection() {
        try {
            datasource = loadDatasource();
            Class.forName(datasource.getJDBC_DRIVER());
            cp = JdbcConnectionPool.create(datasource.getJDBC_CONNECTION() + datasource.getJDBC_PATH() + datasource.getJDBC_FEATURES(),
                    datasource.getUSER(),
                    datasource.getPASS());

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            logger.fatal("Error when connecting to the database", e);
        }
    }

    private Datasource loadDatasource() throws IOException {
        Properties props = new Properties();
        props.load(DatabaseConnection.class.getResourceAsStream("/database.properties"));
        return new Datasource(
                props.getProperty("jdbc.driverClassName"),
                props.getProperty("jdbc.url"),
                props.getProperty("jdbc.path"),
                props.getProperty("jdbc.features"),
                props.getProperty("jdbc.user"),
                props.getProperty("jdbc.pass")
        );

    }

    public static DatabaseConnection instance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Datasource getDatasource() { return datasource; }

    public Connection getConnection() throws SQLException {
        return cp.getConnection();
    }

    public void dispose() {
        cp.dispose();
    }

}
