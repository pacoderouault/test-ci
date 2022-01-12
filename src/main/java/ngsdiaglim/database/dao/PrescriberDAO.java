package ngsdiaglim.database.dao;

import ngsdiaglim.modeles.Prescriber;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriberDAO extends DAO {

    public long addPrescriber(Prescriber prescriber) throws SQLException {
        final String sql = "INSERT INTO Prescribers (status, first_name, last_name, address) VALUES (?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            stm.setString(i++, prescriber.getStatus());
            stm.setString(i++, prescriber.getFirstName());
            stm.setString(i++, prescriber.getLastName());
            stm.setString(i, prescriber.getAddress());
            stm.executeUpdate();

            // get and return the id of the new user
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        }
        return -1;
    }


    public void deletePrescriber(long id) throws SQLException {
        final String sql = "DELETE FROM Prescribers WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }

    public void updatePrescriber(Prescriber prescriber) throws SQLException {
        final String sql = "UPDATE Prescribers SET status=?, first_name=?, last_name=?, address=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 1;
            stm.setString(i++, prescriber.getStatus());
            stm.setString(i++, prescriber.getFirstName());
            stm.setString(i++, prescriber.getLastName());
            stm.setString(i++, prescriber.getAddress());
            stm.setLong(i, prescriber.getId());
            stm.executeUpdate();
        }
    }

    public List<Prescriber> getPrescribers() throws SQLException {
        List<Prescriber> prescribers = new ArrayList<>();

        final String sql = "SELECT id, status, first_name, last_name, address FROM Prescribers;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {

            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                prescribers.add(new Prescriber(
                        rs.getLong("id"),
                        rs.getString("status"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address")));
            }
        }
        return prescribers;
    }
}
