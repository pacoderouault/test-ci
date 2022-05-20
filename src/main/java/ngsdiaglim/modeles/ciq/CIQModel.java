package ngsdiaglim.modeles.ciq;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.variants.Annotation;

import java.util.Optional;

public class CIQModel {

    private long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty barcode = new SimpleStringProperty();
    private final SimpleBooleanProperty active = new SimpleBooleanProperty();
    private final ObservableList<CIQHotspot> hotspots = FXCollections.observableArrayList();

    public CIQModel(long id, String name, String barcode, boolean isActive) {
        this.id = id;
        this.name.set(name);
        this.barcode.setValue(barcode);
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

    public String getBarcode() {
        return barcode.get();
    }

    public SimpleStringProperty barcodeProperty() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
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

    public ObservableList<CIQHotspot> getHotspots() {return hotspots;}

    public void setHotspots(ObservableList<CIQHotspot> hotspots) {
        this.hotspots.setAll(hotspots);
    }

    public CIQHotspot getHotspot(Annotation annotation) {
//        if (annotation.toString().equals("(JAK2) NP_001309123.1:p.Val617Phe")) {
//            System.out.println(hotspots.size());
//            System.out.println(annotation.getGenomicVariant().getContig());
//            System.out.println(annotation.getGenomicVariant().getStart());
//            System.out.println(annotation.getGenomicVariant().getRef());
//            System.out.println(annotation.getGenomicVariant().getAlt());
//        }
        Optional<CIQHotspot> opt = hotspots.stream().filter(h -> h.getContig().equalsIgnoreCase(annotation.getGenomicVariant().getContig())
                && h.getPosition() == annotation.getGenomicVariant().getStart()
                && h.getRef().equalsIgnoreCase(annotation.getGenomicVariant().getRef())
                && h.getAlt().equalsIgnoreCase(annotation.getGenomicVariant().getAlt())).findAny();
        return opt.orElse(null);
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CIQModel)) return false;

        CIQModel ciqModel = (CIQModel) o;

        return id == ciqModel.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
