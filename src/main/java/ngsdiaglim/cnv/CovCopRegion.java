package ngsdiaglim.cnv;

import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.modeles.biofeatures.Region;

import java.util.ArrayList;
import java.util.List;

public class CovCopRegion extends Region {

    private final SimpleStringProperty pool = new SimpleStringProperty();
    private final SimpleStringProperty gene = new SimpleStringProperty();;
    private final List<Integer> raw_values = new ArrayList<>();
    private final List<Double> normalized_values = new ArrayList<>();
    private final List<Double> zScores = new ArrayList<>();

    public CovCopRegion(String contig, int start, int end, String name, String pool, String gene) {
        super(contig, start, end, name);
        this.pool.set(pool);
        this.gene.set(gene);
    }

    public String getPool() {
        return pool.get();
    }

    public SimpleStringProperty poolProperty() {
        return pool;
    }

    public String getGene() {
        return gene.get();
    }

    public SimpleStringProperty geneProperty() {
        return gene;
    }

    public List<Integer> getRaw_values() {return raw_values;}

    public void setRaw_value(int index, Integer value) {
        raw_values.set(index, value);
    }

    public List<Double> getNormalized_values() {return normalized_values;}

    public List<Double> getzScores() {return zScores;}

    public void addRawValue(Integer val) {
        raw_values.add(val);
    }

    public void addNormalizedValue(Double val) {
        normalized_values.add(val);
    }
    public void addNormalizedValue(Double val, int index) {
        normalized_values.set(index, val);
    }

    public void addZScore(Double val) {
        zScores.add(val);
    }
    public void addZScore(Double val, int index) {
        zScores.set(index, val);
    }

    public String toString() {
        return pool + "\t" + getName() + "\t" + getContig() + "\t" + getStart() + "\t" + getEnd();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
