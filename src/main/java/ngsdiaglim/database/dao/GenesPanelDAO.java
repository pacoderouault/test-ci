package ngsdiaglim.database.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import ngsdiaglim.App;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GenePanel;

import java.sql.*;
import java.util.Collection;
import java.util.StringJoiner;


public class GenesPanelDAO extends DAO {

    private final static String fieldsSplitter = ";";

    public long addGenesPanel(String name, Collection<Gene> genes) throws SQLException {
        final String sql = "INSERT INTO genesPanel (name, genes, user_creation, creation_date) VALUES (?, ?, ?, NOW());";
        try (Connection connection=getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            long genesPanelId;

            int i = 0;
            stm.setString(++i, name);
            stm.setString(++i, genesToString(genes));
            stm.setString(++i, App.get().getLoggedUser().getUsername());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                genesPanelId = generatedKeys.getLong(1);
            } else {
                throw new SQLException("No user inserted in the database");
            }
            return genesPanelId;
        }
    }


    public long genesPanelNameExists(String name) throws SQLException {
        final String sql = "SELECT id FROM genesPanel WHERE UPPER(name)=UPPER(?);";
        try (Connection connection=getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
            return -1;
        }
    }


    public ObservableList<GenePanel> getGenesPanels() throws SQLException {
        ObservableList<GenePanel> genesPanels = FXCollections.observableArrayList();
        final String sql = "SELECT id, name, genes, user_creation, creation_date FROM genesPanel ORDER BY name;";
        try (Connection connection=getConnection(); PreparedStatement stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {

                ObservableSet<Gene> genes = FXCollections.observableSet();
                for (String g : rs.getString("genes").split(fieldsSplitter)) {
                    genes.add(new Gene(g));
                }
                genesPanels.add(
                        new GenePanel(
                                rs.getLong("id"),
                                rs.getString("name"),
                                genes,
                                rs.getString("user_creation"),
                                rs.getDate("creation_date").toLocalDate()
                        )
                );
            }
            return genesPanels;
        }
    }


    public void updateGenesPanel(long id, String name, Collection<Gene> genes) throws SQLException {
        final String sql = "UPDATE genesPanel SET name=?, genes=? WHERE id=?;";
        try (Connection connection=getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            int i = 0;
            stm.setString(++i, name);
            stm.setString(++i, genesToString(genes));
            stm.setLong(++i, id);
            stm.executeUpdate();
        }
    }


    public void removeGenesPanel(long id) throws SQLException {
        final String sql = "DELETE FROM genesPanel WHERE id=?;";
        try (Connection connection=getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }


    private String genesToString(Collection<Gene> genes) {
        StringJoiner sj = new StringJoiner(fieldsSplitter);
        for (Gene g : genes) {
            sj.add(g.getGeneName().toUpperCase());
        }
        return sj.toString();
    }
}
