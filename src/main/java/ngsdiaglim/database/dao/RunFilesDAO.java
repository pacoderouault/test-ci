package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.analyse.RunFile;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RunFilesDAO extends DAO {

    public void addRunFile(long run_id, File file) throws SQLException, IOException {
//        Path relativePath = FilesUtils.convertAbsolutePathToRelative(file.toPath());
        final String sql = "INSERT INTO runFiles (path, run_id) VALUES(?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, file.toString());
            stm.setLong(++i, run_id);

            stm.executeUpdate();
        }
    }


    public void removeRunFile(long runfile_id) throws SQLException {
        final String sql = "DELETE FROM runFiles WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, runfile_id);
            stm.executeUpdate();
        }
    }


    public boolean runFileExists(long run_id, File file) throws SQLException, IOException {
//        Path relativePath = FilesUtils.convertAbsolutePathToRelative(file.toPath());
        final String sql = "SELECT id FROM runFiles WHERE run_id=? AND path=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, run_id);
            stm.setString(++i, file.toString());

            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public ObservableList<RunFile> getRunFiles(Run run) throws SQLException {
        ObservableList<RunFile> runFiles = FXCollections.observableArrayList();

        final String sql = "SELECT id, path FROM runFiles WHERE run_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, run.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                RunFile file = new RunFile(
                        rs.getLong("id"),
                        new File(rs.getString("path")),
                        run);
                runFiles.add(file);
            }
        }
        return runFiles;
    }
}
