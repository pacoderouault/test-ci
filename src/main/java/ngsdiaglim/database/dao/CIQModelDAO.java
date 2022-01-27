package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.ciq.CIQModel;

import java.sql.*;

public class CIQModelDAO extends DAO {

    public long addCIQModel(String name, String barcode) throws SQLException {
        final String sql = "INSERT INTO CIQModel(name, barcode) VALUES (?, ?);";
        long id;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, name);
            stm.setString(++i, barcode);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No gene inserted in the database");
            }
        }
        return id;
    }


    public boolean CIQModelExists(String name) throws SQLException {
        final String sql = "SELECT id FROM CIQModel WHERE lower(barcode)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public ObservableList<CIQModel> getCIQModels() throws SQLException {
        ObservableList<CIQModel> ciqModels = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, barcode, is_active FROM CIQModel;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                CIQModel ciqModel = new CIQModel(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("barcode"),
                        rs.getBoolean("is_active")
                );

                ciqModels.add(ciqModel);
            }
        }
        return ciqModels;
    }


    public ObservableList<CIQModel> getActiveCIQModels() throws SQLException {
        ObservableList<CIQModel> ciqModels = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, barcode, is_active FROM CIQModel WHERE is_active=True;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                CIQModel ciqModel = new CIQModel(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("barcode"),
                        rs.getBoolean("is_active")
                );
                ciqModel.setHotspots(DAOController.getCiqHotspotDAO().getCIQHotspots(ciqModel));
                ciqModels.add(ciqModel);
            }
        }
        return ciqModels;
    }


    public CIQModel getCIQModel(long id) throws SQLException {
        final String sql = "SELECT id, name, barcode, is_active FROM CIQModel WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                CIQModel ciqModel = new CIQModel(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("barcode"),
                        rs.getBoolean("is_active")
                );
                ciqModel.setHotspots(DAOController.getCiqHotspotDAO().getCIQHotspots(ciqModel));
                return ciqModel;
            }
        }
        return null;
    }


    public void updateCIQModel(CIQModel ciqModel) throws SQLException {
        final String sql = "UPDATE CIQModel SET is_active=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setBoolean(++i, ciqModel.isActive());
            stm.setLong(++i, ciqModel.getId());
        }
    }


    public void deleteCIQModel(long ciqModelId) throws SQLException {
        final String sql = "DELETE FROM CIQModel WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, ciqModelId);
            stm.executeUpdate();
        }
    }
}
