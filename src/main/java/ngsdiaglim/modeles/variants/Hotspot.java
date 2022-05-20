package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.HotspotType;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Hotspot {

    private long id;
    private final SimpleStringProperty hotspotId = new SimpleStringProperty();
    private final SimpleStringProperty contig = new SimpleStringProperty();
    private final SimpleIntegerProperty start = new SimpleIntegerProperty();
    private final SimpleIntegerProperty end = new SimpleIntegerProperty();
    private final SimpleStringProperty ref = new SimpleStringProperty();
    private final SimpleStringProperty alt = new SimpleStringProperty();
    private final SimpleStringProperty gene = new SimpleStringProperty();
    private final SimpleStringProperty codingMut = new SimpleStringProperty();
    private final SimpleStringProperty proteinMut = new SimpleStringProperty();
    private final SimpleObjectProperty<HotspotType> type = new SimpleObjectProperty<>();


    public Hotspot(long id, String hotspotId, String contig, int start, int end, String ref, String alt, String gene, String codingMut, String proteinMut, HotspotType type) {
        this(hotspotId, contig, start, end, ref, alt, gene, codingMut, proteinMut, type);
        this.id = id;

    }

    public Hotspot(String hotspotId, String contig, int start, int end, String ref, String alt, String gene, String codingMut, String proteinMut, HotspotType type) {
        this.hotspotId.set(hotspotId);
        this.contig.set(contig);
        this.start.set(start);
        this.end.set(end);
        this.ref.set(ref);
        this.alt.set(alt);
        this.gene.set(gene);
        this.codingMut.set(codingMut);
        this.proteinMut.set(proteinMut);
        this.type.set(type);
    }

    public long getId() {return id;}

    public String getHotspotId() {
        return hotspotId.get();
    }

    public SimpleStringProperty hotspotIdProperty() {
        return hotspotId;
    }

    public String getContig() {
        return contig.get();
    }

    public SimpleStringProperty contigProperty() {
        return contig;
    }

    public int getStart() {
        return start.get();
    }

    public SimpleIntegerProperty startProperty() {
        return start;
    }

    public int getEnd() {
        return end.get();
    }

    public SimpleIntegerProperty endProperty() {
        return end;
    }

    public String getRef() {
        return ref.get();
    }

    public SimpleStringProperty refProperty() {
        return ref;
    }

    public String getAlt() {
        return alt.get();
    }

    public SimpleStringProperty altProperty() {
        return alt;
    }

    public String getGene() {
        return gene.get();
    }

    public SimpleStringProperty geneProperty() {
        return gene;
    }

    public String getCodingMut() {
        return codingMut.get();
    }

    public SimpleStringProperty codingMutProperty() {
        return codingMut;
    }

    public String getProteinMut() {
        return proteinMut.get();
    }

    public SimpleStringProperty proteinMutProperty() {
        return proteinMut;
    }

    public HotspotType getType() {
        return type.get();
    }

    public SimpleObjectProperty<HotspotType> typeProperty() {
        return type;
    }

    public boolean isOverlapping(String c, int s, int e) {
        return c.equalsIgnoreCase(contig.get()) && s <= end.get() && e >= start.get();
    }

    public boolean isHotspot(Genome genome, Variant variant) {
        GenomicVariant gv = variant.getGenomicVariant(genome);
        if (type.get().equals(HotspotType.REGION)) {
            return isOverlapping(gv.getContig(), gv.getStart(), gv.getEnd());
        }
        else {
            return gv.getContig().equalsIgnoreCase(contig.get())
                    && gv.getStart() == start.get()
                    && gv.getRef().equalsIgnoreCase(ref.get())
                    && gv.getAlt().equalsIgnoreCase(alt.get());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hotspotId", hotspotId)
                .append("contig", contig)
                .append("start", start)
                .append("end", end)
                .append("ref", ref)
                .append("alt", alt)
                .append("gene", gene)
                .append("type", type)
                .toString();
    }
}
