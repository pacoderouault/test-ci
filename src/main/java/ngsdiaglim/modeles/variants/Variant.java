package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.Cytobands;
import ngsdiaglim.modeles.biofeatures.Region;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Variant {

    private long id;
    private final HashMap<Genome, GenomicVariant> genomeGenomicVariantMap = new HashMap<>();
    private final SimpleObjectProperty<GenomicVariant> grch37PositionVariant;
    private final SimpleObjectProperty<GenomicVariant> grch38PositionVariant;
    private final SimpleIntegerProperty occurrence = new SimpleIntegerProperty();
    private List<Long> analysesInRun = new ArrayList<>();
    private final SimpleIntegerProperty occurrenceInRun = new SimpleIntegerProperty();
    private final SimpleObjectProperty<ACMG> acmg = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty pathogenicityConfirmed = new SimpleBooleanProperty();
    private final SimpleBooleanProperty falsePositiveConfirmed = new SimpleBooleanProperty();
    private final SimpleBooleanProperty falsePositive = new SimpleBooleanProperty();
//    private final SimpleBooleanProperty hotSpot = new SimpleBooleanProperty();
    private final SimpleObjectProperty<Hotspot> hotspot = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Region> cytoband ;
    private final VariantPathogenicityHistory pathogenicityHistory;
    private final VariantFalsePositiveHistory falsePositiveHistory;

    public Variant(GenomicVariant grch37Variant, GenomicVariant grch38Variant) {
        this.grch37PositionVariant = new SimpleObjectProperty<>(grch37Variant);
        this.grch38PositionVariant = new SimpleObjectProperty<>(grch38Variant);
        genomeGenomicVariantMap.put(Genome.GRCh37, grch37PositionVariant.get());
        genomeGenomicVariantMap.put(Genome.GRCh38, grch38PositionVariant.get());
        pathogenicityHistory = new VariantPathogenicityHistory(this);
        falsePositiveHistory = new VariantFalsePositiveHistory(this);
    }

    public Variant(long id, GenomicVariant grch37Variant, GenomicVariant grch38Variant) {
        this(grch37Variant, grch38Variant);
        this.id = id;
    }

    public Variant(Variant other) {
        this.id = other.id;
        this.grch37PositionVariant = other.grch37PositionVariant;
        this.grch38PositionVariant = other.grch38PositionVariant;
        this.cytoband = other.cytoband;
        this.pathogenicityHistory = other.pathogenicityHistory;
        this.falsePositiveHistory = other.falsePositiveHistory;
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public GenomicVariant getGrch37PositionVariant() {
        return grch37PositionVariant.get();
    }

    public GenomicVariant getGenomicVariant(Genome genome) {return genomeGenomicVariantMap.getOrDefault(genome, getGrch37PositionVariant());}

    public SimpleObjectProperty<GenomicVariant> grch37PositionVariantProperty() {
        return grch37PositionVariant;
    }

    public GenomicVariant getGrch38PositionVariant() {
        return grch38PositionVariant.get();
    }

    public SimpleObjectProperty<GenomicVariant> grch38PositionVariantProperty() {
        return grch38PositionVariant;
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

    public List<Long> getAnalysesInRun() {return analysesInRun;}

    public void setAnalysesInRun(List<Long> analysesInRun) {
        this.analysesInRun = analysesInRun;
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
            cytoband.set(Cytobands.getCytoBand(new Region(grch37PositionVariant.get().getContig(), grch37PositionVariant.get().getStart(), grch37PositionVariant.get().getEnd(), null)));
        }
        return cytoband;
    }

    public void loadPathogenicityHistory() throws SQLException {
        pathogenicityHistory.setVariantPathogenicityHistory(DAOController.getVariantPathogenicityDAO().getVariantPathogenicityHistory(id));
    }

    public void loadFalsePositiveHistory() throws SQLException {
        falsePositiveHistory.setVariantFalsePositiveHistory(DAOController.getVariantFalsePositiveDAO().getVariantFalsePositiveHistory(id));
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


    public String toString() {
        return grch37PositionVariant.get().getContig() + ":" + grch37PositionVariant.get().getStart() + grch37PositionVariant.get().getRef() +">" + grch37PositionVariant.get().getAlt();
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
