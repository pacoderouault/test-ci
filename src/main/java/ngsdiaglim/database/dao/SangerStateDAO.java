package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.SangerState;
import ngsdiaglim.modeles.analyse.AnnotationSangerCheck;
import ngsdiaglim.modeles.analyse.SangerCheck;
import ngsdiaglim.modeles.variants.Annotation;

import java.sql.*;
import java.time.LocalDateTime;

public class SangerStateDAO extends DAO {

    public boolean hasSangerState(long variantId, long analysisId) throws SQLException {
        final String sql = "SELECT id FROM sangerState WHERE variant_id=? AND analysis_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, variantId);
            stm.setLong(2, analysisId);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public void addSangerState(long variantId, long analysisId, SangerState state, String comment) throws SQLException {
        final String sql = "INSERT INTO sangerState (variant_id, analysis_id, sanger_state, user_id, user_name, date, commentary) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setLong(++i, variantId);
            stm.setLong(++i, analysisId);
            stm.setString(++i, state.name());
            stm.setLong(++i, App.get().getLoggedUser().getId());
            stm.setString(++i, App.get().getLoggedUser().getUsername());
            stm.setTimestamp(++i, Timestamp.valueOf(LocalDateTime.now()));
            stm.setString(++i, comment);

            stm.executeUpdate();
        }
    }


    public AnnotationSangerCheck getSangerChecks(Annotation annotation, long analysisId) throws SQLException {
        AnnotationSangerCheck sangerCheck = new AnnotationSangerCheck(annotation);
        ObservableList<SangerCheck> sangerChecks = FXCollections.observableArrayList();
        final String sql = "SELECT id, sanger_state, user_id, user_name, date, commentary FROM sangerState WHERE variant_id=? AND analysis_id=? ORDER BY date DESC;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, annotation.getVariant().getId());
            stm.setLong(2, analysisId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                sangerChecks.add(new SangerCheck(
                        rs.getLong("id"),
                        annotation.getVariant().getId(),
                        analysisId,
                        SangerState.valueOf(rs.getString("sanger_state")),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getString("commentary")
                ));
            }
        }
        if (!sangerChecks.isEmpty()) {
            sangerCheck.setSangerChecks(sangerChecks);
        }
        return sangerCheck;
    }
}
