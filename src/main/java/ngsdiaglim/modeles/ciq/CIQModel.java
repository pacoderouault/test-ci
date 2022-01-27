package ngsdiaglim.modeles.ciq;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import ngsdiaglim.modeles.variants.Variant;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.swing.text.html.Option;
import java.util.Optional;

public class CIQModel {

    private long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty barcode = new SimpleStringProperty();
    private final SimpleBooleanProperty active = new SimpleBooleanProperty();
    private ObservableList<CIQHotspot> hotspots;

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
        this.hotspots = hotspots;
    }

    public CIQHotspot getHotspot(Variant variant) {
        Optional<CIQHotspot> opt = hotspots.stream().filter(h -> h.getContig().equalsIgnoreCase(variant.getContig())
                && h.getPosition() == variant.getStart()
                && h.getRef().equalsIgnoreCase(variant.getRef())
                && h.getAlt().equalsIgnoreCase(variant.getAlt())).findAny();
        return opt.orElse(null);
    }

    @Override
    public String toString() {
        return barcode.get();
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
