package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GeneSet;

import java.sql.*;
import java.util.List;

public class GeneSetDAO extends DAO {

    public boolean geneSetExists(String name) throws SQLException {
        final String sql = "SELECT id FROM geneSet WHERE lower(name)=lower(?);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }


    public long addGeneSet(String name) throws SQLException {
        long id;
        final String sql = "INSERT INTO geneSet (name, is_active) VALUES(?, True);";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, name);
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No gene set inserted in the database");
            }
        }
        return id;
    }


    public void updateGeneSet(GeneSet geneSet, String newName, boolean isActive) throws SQLException {
        final String sql = "UPDATE geneSet SET name=?, is_active=? WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, newName);
            stm.setBoolean(++i, isActive);
            stm.setLong(++i, geneSet.getId());
            stm.executeUpdate();
        }
    }


    public ObservableList<GeneSet> getGeneSets() throws SQLException {
        ObservableList<GeneSet> geneSets = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active FROM geneSet ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                GeneSet geneSet = new GeneSet(id, name, isActive);
                List<Gene> genes = DAOController.getGeneDAO().getGenes(id);
                for (Gene gene : genes) {
                    geneSet.addGene(gene);
                }
                geneSets.add(geneSet);
            }
        }
        return geneSets;
    }

    public ObservableList<GeneSet> getActiveGeneSets() throws SQLException {
        ObservableList<GeneSet> geneTranscriptSets = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, is_active FROM geneSet WHERE is_active=True ORDER BY name;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                GeneSet geneSet = new GeneSet(id, name, isActive);
                List<Gene> genes = DAOController.getGeneDAO().getGenes(id);
                for (Gene gene : genes) {
                    geneSet.addGene(gene);
                }
                geneTranscriptSets.add(geneSet);
            }
        }
        return geneTranscriptSets;
    }

    public GeneSet getGeneSet(long id) throws SQLException {
        final String sql = "SELECT name, is_active FROM geneSet WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                boolean isActive = rs.getBoolean("is_active");
                GeneSet geneSet = new GeneSet(id, name, isActive);
                List<Gene> genes = DAOController.getGeneDAO().getGenes(id);
                for (Gene gene : genes) {
                    geneSet.addGene(gene);
                }
                return geneSet;
            }
            return null;
        }
    }


    public void deleteGeneSet(long id) throws SQLException {
        final String sql = "DELETE FROM geneSet WHERE id=?;";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }
}
