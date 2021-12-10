package ngsdiaglim.modeles.analyse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.variants.Annotation;

import java.util.Collection;

public class AnnotationSangerCheck {

    private final Annotation annotation;
    private final ObservableList<SangerCheck> sangerChecks = FXCollections.observableArrayList();

    public AnnotationSangerCheck(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {return annotation;}

    public ObservableList<SangerCheck> getSangerChecks() {return sangerChecks;}

    public void setSangerChecks(Collection<SangerCheck> sangerChecks) {
        this.sangerChecks.setAll(sangerChecks);
    }

    public SangerCheck getLastState() {
        if (sangerChecks.isEmpty()) return null;
        return sangerChecks.get(0);
    }
}
