package ngsdiaglim.modeles.biofeatures;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableSet;

import java.time.LocalDate;
import java.util.Collection;

public class GenePanel {

    private long id;
    private final SimpleStringProperty name;
    private final ObservableSet<Gene> genes;
    private final String creationUser;
    private final LocalDate creationDate;

    public GenePanel(long id, String name, ObservableSet<Gene> genes, String creationUser, LocalDate creationDate) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.genes = genes;
        this.creationUser = creationUser;
        this.creationDate = creationDate;
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableSet<Gene> getGenes() {return genes;}

    public void addGene(Gene gene) {
        this.genes.add(gene);
    }

    public void setGenes(Collection<Gene> genes) {
        this.genes.addAll(genes);
    }

    public boolean hasGene(Gene g) {
        return genes.contains(g);
    }

    public String getCreationUser() {return creationUser;}

    public LocalDate getCreationDate() {return creationDate;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenePanel genePanel = (GenePanel) o;

        return name.equals(genePanel.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name.get();
    }
}
