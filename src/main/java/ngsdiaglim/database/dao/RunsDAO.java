package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.Order;
import ngsdiaglim.enumerations.RunOrder;
import ngsdiaglim.modeles.analyse.Run;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.time.LocalDate;

public class RunsDAO extends DAO {

    public boolean runExists(String runName) throws SQLException {
        final String sql = "SELECT id FROM runs WHERE lower(name)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, runName);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public long addRun(String runName, String runPath, LocalDate date, LocalDate creationDate, String userName) throws SQLException {
        long run_id;
        final String sql = "INSERT INTO runs (name, path, date, creation_date, creation_user) VALUES(?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, runName);
            stm.setString(++i, runPath);
            stm.setDate(++i, Date.valueOf(date));
            stm.setDate(++i, Date.valueOf(creationDate));
            stm.setString(++i, userName);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                run_id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No run inserted in the database");
            }
        }
        return run_id;
    }


    public void editRun(Run run, String newName, LocalDate newDate) throws SQLException {
        final String sql = "UPDATE runs SET runName=?, date=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, newName);
            stm.setDate(++i, Date.valueOf(newDate));
            stm.setLong(++i, run.getId());
            stm.executeUpdate();
        }
    }


    public int getRunsCount(String runNameFilter) throws SQLException {
        String sql = "SELECT COUNT(*) FROM runs";
        if (StringUtils.isNotBlank(runNameFilter)) {
            sql += " WHERE instr(lower(name), ?) > 0";
        }
        sql += ";";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            if (StringUtils.isNotBlank(runNameFilter)) {
                stm.setString(1, runNameFilter);
            }
            ResultSet rs = stm.executeQuery();
            int count = -1;
            if (rs.next())
            {
                count = rs.getInt(1);
            }
            return count;
        }
    }


    public ObservableList<Run> getRuns() throws SQLException {
        ObservableList<Run> runs = FXCollections.observableArrayList();
        final String sql = "SELECT id, path, name, date, creation_date, creation_user FROM runs ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                String creationUser = rs.getString("creation_user");
                runs.add(new Run(id, path, name, date, creationDate, creationUser));
            }
        }
        return runs;
    }


    public ObservableList<Run> getRuns(String filterName, Order order, RunOrder runOrder, int leftLimit, int rightLimit) throws SQLException {
        ObservableList<Run> runs = FXCollections.observableArrayList();
        String sql = "SELECT id, path, name, date, creation_date, creation_user FROM runs ";

        if (StringUtils.isNotBlank(filterName)) {
            sql += " WHERE instr(lower(name), ?) > 0 ";
        }

        if (runOrder.equals(RunOrder.NAME)) {
            sql += "ORDER BY name";
        } else if (runOrder.equals(RunOrder.DATE)) {
            sql += "ORDER BY date";
        }
        if (order.equals(Order.ASC)) {
            sql += " ASC";
        } else if (order.equals(Order.DESC)) {
            sql += " DESC";
        }
        sql += " LIMIT ?,?;";

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            if (StringUtils.isNotBlank(filterName)) {
                stm.setString(++i, filterName.toLowerCase());
            }
            stm.setInt(++i, leftLimit);
            stm.setInt(++i, rightLimit);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                String creationUser = rs.getString("creation_user");
                runs.add(new Run(id, path, name, date, creationDate, creationUser));
            }
        }
        return runs;
    }


    public void deleteRun(long runId) throws SQLException {
        final String sql = "DELETE FROM runs WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, runId);
            stm.executeUpdate();
        }
    }

    public Run getRun(long runId) throws SQLException {
        final String sql = "SELECT id, path, name, date, creation_date, creation_user FROM runs WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, runId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                String creationUser = rs.getString("creation_user");
                return new Run(id, path, name, date, creationDate, creationUser);
            }
            return null;
        }
    }

//    public void close() throws SQLException {
//        getConnection().createStatement().execute("SHUTDOWN");
//    }
}
