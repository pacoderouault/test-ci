package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.DAO;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.Cytobands;
import ngsdiaglim.modeles.biofeatures.Region;

import java.io.IOException;
import java.sql.SQLException;

public class Variant {

    private long id;
    private final SimpleStringProperty contig = new SimpleStringProperty();
    private final SimpleIntegerProperty start = new SimpleIntegerProperty();
    private final SimpleIntegerProperty end = new SimpleIntegerProperty();
    private final SimpleIntegerProperty occurrence = new SimpleIntegerProperty();
    private final SimpleIntegerProperty occurrenceInRun = new SimpleIntegerProperty();
    private final SimpleStringProperty ref = new SimpleStringProperty();
    private final SimpleStringProperty alt = new SimpleStringProperty();
    private final SimpleObjectProperty<ACMG> acmg = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty pathogenicityConfirmed = new SimpleBooleanProperty();
    private final SimpleBooleanProperty falsePositiveConfirmed = new SimpleBooleanProperty();
    private final SimpleBooleanProperty falsePositive = new SimpleBooleanProperty();
//    private final SimpleBooleanProperty hotSpot = new SimpleBooleanProperty();
    private final SimpleObjectProperty<Hotspot> hotspot = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Region> cytoband ;
    private final VariantPathogenicityHistory pathogenicityHistory;
    private final VariantFalsePositiveHistory falsePositiveHistory;

    public Variant(String contig, int start, int end, String ref, String alt) {
        this.contig.set(contig);
        this.start.set(start);
        this.end.set(end);
        this.ref.set(ref);
        this.alt.set(alt);
        pathogenicityHistory = new VariantPathogenicityHistory(this);
        falsePositiveHistory = new VariantFalsePositiveHistory(this);
    }

    public Variant(long id, String contig, int start, int end, String ref, String alt) {
        this(contig, start, end, ref, alt);
        this.id = id;
    }

    public Variant(Variant other) {
        this.id = other.id;
        this.contig.set(other.getContig());
        this.start.set(other.getStart());
        this.end.set(other.getEnd());
        this.ref.set(other.getRef());
        this.alt.set(other.getAlt());
        this.cytoband = other.cytoband;
        this.pathogenicityHistory = other.pathogenicityHistory;
        this.falsePositiveHistory = other.falsePositiveHistory;
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
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

    public String getContigWithoutChr() {
        return contig.get().replaceFirst("chr", "");
    }

    public int getStart() {
        return start.get();
    }

    public SimpleIntegerProperty startProperty() {
        return start;
    }

    public void setStart(int start) {
        this.start.set(start);
    }

    public int getEnd() {
        return end.get();
    }

    public SimpleIntegerProperty endProperty() {
        return end;
    }

    public void setEnd(int end) {
        this.end.set(end);
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

    public ACMG getAcmg() {
        return acmg.get();
    }

    public SimpleObjectProperty<ACMG> acmgProperty() {
        return acmg;
    }

    public void setAcmg(ACMG acmg) {
        this.acmg.set(acmg);
    }

    public boolean isPathogenicityConfirmed() {
        return pathogenicityConfirmed.get();
    }

    public SimpleBooleanProperty pathogenicityConfirmedProperty() {
        return pathogenicityConfirmed;
    }

    public void setPathogenicityConfirmed(boolean pathogenicityConfirmed) {
        this.pathogenicityConfirmed.set(pathogenicityConfirmed);
    }

    public boolean isFalsePositive() {
        return falsePositive.get();
    }

    public SimpleBooleanProperty falsePositiveProperty() {
        return falsePositive;
    }

    public void setFalsePositive(boolean falsePositive) {
        this.falsePositive.set(falsePositive);
    }

    public boolean isFalsePositiveConfirmed() {
        return falsePositiveConfirmed.get();
    }

    public SimpleBooleanProperty falsePositiveConfirmedProperty() {
        return falsePositiveConfirmed;
    }

    public void setFalsePositiveConfirmed(boolean falsePositiveConfirmed) {
        this.falsePositiveConfirmed.set(falsePositiveConfirmed);
    }

    public Hotspot getHotspot() {
        return hotspot.get();
    }

    public SimpleObjectProperty<Hotspot> hotspotProperty() {
        return hotspot;
    }

    public void setHotspot(Hotspot hotspot) {
        this.hotspot.set(hotspot);
    }

    public boolean isHotspot() {
        return hotspot.get() != null;
    }

    public int getOccurrence() {
        return occurrence.get();
    }

    public SimpleIntegerProperty occurrenceProperty() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence.set(occurrence);
    }

    public int getOccurrenceInRun() {
        return occurrenceInRun.get();
    }

    public SimpleIntegerProperty occurrenceInRunProperty() {
        return occurrenceInRun;
    }

    public void setOccurrenceInRun(int occurrenceInRun) {
        this.occurrenceInRun.set(occurrenceInRun);
    }

    public Region getCytoband() throws IOException {
        return cytobandProperty().get();
    }

    public SimpleObjectProperty<Region> cytobandProperty() throws IOException {
        if (cytoband == null) {
            cytoband = new SimpleObjectProperty<>();
            cytoband.set(Cytobands.getCytoBand(new Region(contig.get(), start.get(), end.get(), null)));
        }
        return cytoband;
    }

    public void loadPathogenicityHistory() throws SQLException {
        pathogenicityHistory.setVariantPathogenicityHistory(DAOController.get().getVariantPathogenicityDAO().getVariantPathogenicityHistory(id));
    }

    public void loadFalsePositiveHistory() throws SQLException {
        falsePositiveHistory.setVariantFalsePositiveHistory(DAOController.get().getVariantFalsePositiveDAO().getVariantFalsePositiveHistory(id));
    }

    public VariantPathogenicityHistory getPathogenicityHistory() throws SQLException {
        if (pathogenicityHistory.getHistory() == null) {
            loadPathogenicityHistory();
        }
        return pathogenicityHistory;
    }

    public VariantFalsePositiveHistory getFalsePositiveHistory() throws SQLException {
        if (falsePositiveHistory.getHistory() == null) {
            loadFalsePositiveHistory();
        }
        return falsePositiveHistory;
    }


    @Override
    public String toString() {
        return getContig() + ":" + getStart() + getRef() +">" + getAlt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variant variant = (Variant) o;

        return id == variant.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
