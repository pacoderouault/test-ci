package ngsdiaglim.cnv;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.analyse.Panel;

import java.io.File;
import java.util.Collection;

public class CNVControlGroup {

    private final long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<TargetEnrichment> algorithm = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Panel> panel = new SimpleObjectProperty<>();
    private File matrix_file;
    private final ObservableList<CNVControl> controlsList = FXCollections.observableArrayList();
    private final File path;

    public CNVControlGroup(long id, String name, TargetEnrichment algorithm, Panel panel, File matrix_file, File path) {
        this.id = id;
        this.name.set(name);
        this.algorithm.set(algorithm);
        this.panel.set(panel);
        this.matrix_file = matrix_file;
        this.path = path;
    }

    public long getId() {return id;}

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public TargetEnrichment getAlgorithm() {
        return algorithm.get();
    }

    public SimpleObjectProperty<TargetEnrichment> algorithmProperty() {
        return algorithm;
    }

    public void setAlgorithm(TargetEnrichment algorithm) {
        this.algorithm.set(algorithm);
    }

    public Panel getPanel() {
        return panel.get();
    }

    public SimpleObjectProperty<Panel> panelProperty() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel.set(panel);
    }

    public File getMatrix_file() {return matrix_file;}

    public void setMatrix_file(File matrix_file) {
        this.matrix_file = matrix_file;
    }

    public ObservableList<CNVControl> getControlsList() {return controlsList;}

    public void setControls(Collection<CNVControl> controls) {
        controlsList.setAll(controls);
    }

    public File getPath() {return path;}

    @Override
    public String toString() {
        return name.get();
    }
}
