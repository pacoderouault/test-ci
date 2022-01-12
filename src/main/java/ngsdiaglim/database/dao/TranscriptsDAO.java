package ngsdiaglim.database.dao;

import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;

import java.sql.*;
import java.util.HashMap;

public class TranscriptsDAO extends DAO {

    public long addTranscript(String name, long geneId) throws SQLException {
        final String sql = "INSERT INTO transcripts (name, gene_id) VALUES (?, ?);";
        long id;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setString(++i, name.toUpperCase());
            stm.setLong(++i, geneId);
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


    public HashMap<String, Transcript> getTranscripts(Gene gene) throws SQLException {
        HashMap<String, Transcript> transcripts = new HashMap<>();
        final String sql = "SELECT id, name FROM transcripts WHERE gene_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, gene.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transcript t = new Transcript(
                        rs.getLong("id"),
                        gene.getId(),
                        rs.getString("name"),
                        gene
                );
                transcripts.putIfAbsent(t.getName(), t);
            }
        }
        return transcripts;
    }

}
