package ngsdiaglim.database.dao;

import ngsdiaglim.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DAO {

    public DAO() {
    }

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.instance().getConnection();
    }
}
