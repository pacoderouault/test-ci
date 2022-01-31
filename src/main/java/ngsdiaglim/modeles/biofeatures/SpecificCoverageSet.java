package ngsdiaglim.modeles.biofeatures;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class SpecificCoverageSet {

    private long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final ObservableList<SpecificCoverage> specificCoverageList = FXCollections.observableArrayList();
    private final SimpleBooleanProperty active = new SimpleBooleanProperty();

    public SpecificCoverageSet(long id, String name, List<SpecificCoverage> specificCoverageList, boolean isActive) {
        this.id = id;
        this.name.set(name);
        this.specificCoverageList.setAll(specificCoverageList);
        this.active.set(isActive);
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

    public ObservableList<SpecificCoverage> getSpecificCoverageList() {return specificCoverageList;}

    public void setSpecifiCoverageList(List<SpecificCoverage> specificCoverageList) {
        this.specificCoverageList.setAll(specificCoverageList);
    }

    public boolean isActive() {
        return active.get();
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public List<SpecificCoverage> getOverlapingRegions(String contig, int start, int end, double depth) {
        return specificCoverageList.stream().filter(r -> depth < r.getMinCov() && r.overlaps(contig, start, end)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return nameProperty().get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecificCoverageSet)) return false;

        SpecificCoverageSet that = (SpecificCoverageSet) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
