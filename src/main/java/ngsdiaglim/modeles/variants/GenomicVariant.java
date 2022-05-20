package ngsdiaglim.modeles.variants;

import htsjdk.samtools.util.Interval;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GenomicVariant {
    private final SimpleStringProperty contig = new SimpleStringProperty();
    private final SimpleIntegerProperty start = new SimpleIntegerProperty();
    private final SimpleIntegerProperty end = new SimpleIntegerProperty();
    private final SimpleStringProperty ref = new SimpleStringProperty();
    private final SimpleStringProperty alt = new SimpleStringProperty();

    public GenomicVariant(String contig, Integer start, Integer end, String ref, String alt) {
        this.contig.set(contig);
        this.start.setValue(start);
        this.end.setValue(end);
        this.ref.set(ref);
        this.alt.set(alt);
    }

    public String getContig() {
        return contig.get();
    }

    public SimpleStringProperty contigProperty() {
        return contig;
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
}
