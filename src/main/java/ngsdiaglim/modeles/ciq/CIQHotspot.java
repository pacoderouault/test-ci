package ngsdiaglim.modeles.ciq;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.utils.NumberUtils;

public class CIQHotspot {

    private long id;
    private CIQModel ciq;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty contig = new SimpleStringProperty();
    private final SimpleIntegerProperty position = new SimpleIntegerProperty();
    private final SimpleStringProperty ref = new SimpleStringProperty();
    private final SimpleStringProperty alt = new SimpleStringProperty();
    private final SimpleFloatProperty vafTarget = new SimpleFloatProperty();

    public CIQHotspot(String name, String contig, int position, String ref, String alt, Float vafTarget) {
        this.name.set(name);
        this.contig.set(contig);
        this.position.set(position);
        this.ref.set(ref);
        this.alt.set(alt);
        this.vafTarget.set(vafTarget);
    }

    public CIQHotspot(long id, CIQModel ciq, String name, String contig, int position, String ref, String alt, Float vafTarget) {
        this(name, contig, position, ref, alt, vafTarget);
        this.id = id;
        this.ciq = ciq;
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public CIQModel getCiq() {return ciq;}

    public void setCiq(CIQModel ciq) {
        this.ciq = ciq;
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

    public String getContig() {
        return contig.get();
    }

    public SimpleStringProperty contigProperty() {
        return contig;
    }

    public void setContig(String contig) {
        this.contig.set(contig);
    }

    public int getPosition() {
        return position.get();
    }

    public SimpleIntegerProperty positionProperty() {
        return position;
    }

    public void setPosition(int position) {
        this.position.set(position);
    }

    public String getRef() {
        return ref.get();
    }

    public SimpleStringProperty refProperty() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref.set(ref);
    }

    public String getAlt() {
        return alt.get();
    }

    public SimpleStringProperty altProperty() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt.set(alt);
    }

    public float getVafTarget() {
        return vafTarget.get();
    }

    public SimpleFloatProperty vafTargetProperty() {
        return vafTarget;
    }

    public void setVafTarget(float vafTarget) {
        this.vafTarget.set(vafTarget);
    }

    public String getHGVS() {
        return contig.get() + ":" + position.get() + ref.get() + ">" + alt.get();
    }
    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CIQHotspot)) return false;

        CIQHotspot that = (CIQHotspot) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
