package ngsdiaglim.modeles.analyse;

import java.util.Objects;

public class PanelRegion {

    private long id;
    private long panel_id;
    private String contig;
    private int start;
    private int end;
    private String name;

    public PanelRegion(String contig, int start, int end, String name) {
        this.contig = contig;
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public PanelRegion(long id, long panel_id, String contig, int start, int end, String name) {
        this(contig, start, end, name);
        this.id = id;
        this.panel_id = panel_id;
    }

    public long getId() {return id;}

    public long getPanel_id() {return panel_id;}

    public String getContig() {return contig;}

    public int getStart() {return start;}

    public int getEnd() {return end;}

    public String getName() {return name;}

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

    public int getSize() {
        return end - start + 1;
    }
}
