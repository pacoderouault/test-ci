package ngsdiaglim.modeles.analyse;

import ngsdiaglim.modeles.biofeatures.Region;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class PanelRegion {

    private long id;
    private long panel_id;
    private final String contig;
    private final int start;
    private final int end;
    private final String name;
    private String poolAmplification;

    public PanelRegion(String contig, int start, int end, String name) {
        this.contig = contig;
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public PanelRegion(long id, long panel_id, String contig, int start, int end, String name, String poolAmplification) {
        this(contig, start, end, name);
        this.id = id;
        this.panel_id = panel_id;
        this.poolAmplification = poolAmplification;
    }

    public long getId() {return id;}

    public long getPanel_id() {return panel_id;}

    public String getContig() {return contig;}

    public int getStart() {return start;}

    public int getEnd() {return end;}

    public String getName() {return name;}

    public int getSize() {
        return end - start + 1;
    }

    public boolean overlaps(Region region) {
        return overlaps(region.getContig(), region.getStart(), region.getEnd());
    }

    public boolean overlaps(String contig, int start, int end) {
        return this.contig.equalsIgnoreCase(contig) && this.end >= start && this.start <= end;
    }

    public String getPoolAmplification() {return poolAmplification;}

    public void setPoolAmplification(String poolAmplification) {
        this.poolAmplification = poolAmplification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PanelRegion that = (PanelRegion) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        return Objects.equals(contig, that.contig);
    }

    @Override
    public int hashCode() {
        int result = contig != null ? contig.hashCode() : 0;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("contig", contig)
                .append("start", start)
                .append("end", end)
                .toString();
    }
}
