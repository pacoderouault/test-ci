package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.ReportMutationCommentary;
import ngsdiaglim.modeles.variants.Annotation;

import java.sql.*;
import java.util.Map;

public class ReportMutationCommentaryDAO extends DAO {

    public ObservableList<ReportMutationCommentary> getReportMutationCommentary(ReportType reportType, Map<Long, Annotation> variants) throws SQLException {
        ObservableList<ReportMutationCommentary> reportMutCommentaries = FXCollections.observableArrayList();
        Connection connection = getConnection();
        Object[] objetsList = new Object[variants.size()];
        int k = 0;
        for (Long variantId : variants.keySet()) {
            objetsList[k++] = variantId;
        }
        Array array = connection.createArrayOf("Varchar", objetsList);
        final String sql = "SELECT id, variant_id, title, commentary FROM reportMutationCommentary WHERE ARRAY_CONTAINS(?, variant_id) AND report_type=?;";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setArray(++i, array);
            stm.setString(++i, reportType.name());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                reportMutCommentaries.add(new ReportMutationCommentary(
                        rs.getLong("id"),
                        reportType,
                        rs.getString("title"),
                        rs.getString("commentary"),
                        variants.get(rs.getLong("variant_id"))));
            }
            return reportMutCommentaries;
        } finally {
            connection.close();
        }
    }

    public long insertReportMutationCommentary(ReportMutationCommentary reportMutationCommentary) throws SQLException {
        final String sql = "INSERT INTO ReportMutationCommentary (variant_id, report_type, title, commentary) VALUES (?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, reportMutationCommentary.getAnnotation().getVariant().getId());
            stm.setString(++i, reportMutationCommentary.getReportType().name());
            stm.setString(++i, reportMutationCommentary.getTitle());
            stm.setString(++i, reportMutationCommentary.getComment());
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

    public void editReportMutationCommentary(long id, String title, String comment) throws SQLException {
        final String sql = "UPDATE ReportMutationCommentary SET title=?, commentary=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, title);
            stm.setString(++i, comment);
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }

    public void deleteReportMutationCommentary(long id) throws SQLException {
        final String sql = "DELETE FROM ReportMutationCommentary WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }
}
