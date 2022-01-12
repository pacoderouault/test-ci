package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.ReportGeneCommentary;

import java.sql.*;
import java.util.Set;

public class ReportGeneCommentaryDAO extends DAO {

    public ObservableList<ReportGeneCommentary> getReportGeneCommentary(ReportType reportType, Set<String> geneNames) throws SQLException {
        ObservableList<ReportGeneCommentary> reportGeneCommentaries = FXCollections.observableArrayList();
        Connection connection = getConnection();
        Object[] objetsList = new Object[geneNames.size()];
        int k = 0;
        for (String geneName : geneNames) {
            objetsList[k++] = geneName;
        }
        Array array = connection.createArrayOf("Varchar", objetsList);
        final String sql = "SELECT id, gene_name, title, commentary FROM reportGeneCommentary WHERE ARRAY_CONTAINS(?, gene_name) AND report_type=?;";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setArray(++i, array);
            stm.setString(++i, reportType.name());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                reportGeneCommentaries.add( new ReportGeneCommentary(
                        rs.getLong("id"),
                        reportType,
                        rs.getString("gene_name"),
                        rs.getString("title"),
                        rs.getString("commentary")));
            }

        } finally {
            connection.close();
        }
        return reportGeneCommentaries;
    }

    public long insertReportGeneCommentary(ReportGeneCommentary reportGeneCommentary) throws SQLException {
        final String sql = "INSERT INTO reportGeneCommentary (report_type, gene_name, title, commentary) VALUES (?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, reportGeneCommentary.getReportType().name());
            stm.setString(++i, reportGeneCommentary.getGeneName());
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

    public void editReportGeneCommentary(long id, String title, String comment) throws SQLException {
        final String sql = "UPDATE reportGeneCommentary SET title=?, commentary=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, title);
            stm.setString(++i, comment);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

    public void deleteReportGeneCommentary(long id) throws SQLException {
        final String sql = "DELETE FROM reportGeneCommentary WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

}
