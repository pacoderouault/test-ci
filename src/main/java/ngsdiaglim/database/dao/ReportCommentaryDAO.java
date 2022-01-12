package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;

import java.sql.*;

public class ReportCommentaryDAO extends DAO {

    public ObservableList<ReportCommentary> getReportCommentary(ReportType reportType) throws SQLException {
        ObservableList<ReportCommentary> commentaries = FXCollections.observableArrayList();
        final String sql = "SELECT id, title, commentary FROM reportOtherCommentary WHERE report_type=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, reportType.name());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                commentaries.add( new ReportCommentary(
                        rs.getLong("id"),
                        reportType,
                        rs.getString("title"),
                        rs.getString("commentary")));
            }
            return commentaries;
        }
    }

    public long insertReportCommentary(ReportCommentary reportGeneCommentary) throws SQLException {
        final String sql = "INSERT INTO reportOtherCommentary (report_type, title, commentary) VALUES (?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, reportGeneCommentary.getReportType().name());
            stm.setString(++i, reportGeneCommentary.getTitle());
            stm.setString(++i, reportGeneCommentary.getComment());
            stm.executeUpdate();
            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                else {
                    return -1;
                }
            }
        }
    }

    public void editReportCommentary(Long id, String title, String comment) throws SQLException {
        final String sql = "UPDATE reportOtherCommentary SET title=?, commentary=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, title);
            stm.setString(++i, comment);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

    public void deleteReportCommentary(long id) throws SQLException {
        final String sql = "DELETE FROM reportOtherCommentary WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }
}
