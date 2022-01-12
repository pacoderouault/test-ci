package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class GeneDAO extends DAO {

    public long addGene(Gene gene, long geneSetId) throws SQLException {
        final String sql = "INSERT INTO genes(geneset_id, gene_name) VALUES (?, ?);";
        long id;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 0;
            stm.setLong(++i, geneSetId);
            stm.setString(++i, gene.getGeneName());
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


    public ObservableList<Gene> getGenes(long geneSetId) throws SQLException {
        ObservableList<Gene> genes = FXCollections.observableArrayList();
        final String sql = "SELECT id, gene_name, transcript_preferred_id FROM genes WHERE geneset_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, geneSetId);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("gene_name");
                long transcriptsPreferredId = rs.getLong("transcript_preferred_id");

                Gene gene = new Gene(id, geneSetId, name);
                HashMap<String, Transcript> transcripts = DAOController.getTranscriptsDAO().getTranscripts(gene);
                gene.setTranscripts(transcripts);
                // find transcript pref
                if (transcriptsPreferredId > 0) {
                    Optional<Transcript> opt = transcripts.values().stream().filter(t -> t.getId() == transcriptsPreferredId).findAny();
                    opt.ifPresent(t -> {
                        t.setPreferred(true);
                        gene.setTranscriptPreferred(t);
                    });
                }
                genes.add(gene);
            }
        }
        return genes;
    }

    public void setPreferredTranscript(long gene_id, long transcript_id) throws SQLException {
        final String sql = "UPDATE genes SET transcript_preferred_id=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            if (transcript_id >= 0) {
                stm.setLong(++i, transcript_id);
            }
            else {
                stm.setNull(++i, Types.NULL);
            }
            stm.setLong(++i, gene_id);
            stm.executeUpdate();
        }
    }


    public int getGeneCount(long geneSetId) throws SQLException {
        final String sql = "SELECT COUNT(id) AS count FROM genes WHERE geneset_id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, geneSetId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    public HashSet<Gene> getGenes() throws SQLException {
        HashSet<Gene> genes = new HashSet<>();
        final String sql = "SELECT DISTINCT(gene_name) AS name FROM genes ORDER BY gene_name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                genes.add(new Gene(rs.getString("name")));
            }
            return genes;
        }
    }
}
