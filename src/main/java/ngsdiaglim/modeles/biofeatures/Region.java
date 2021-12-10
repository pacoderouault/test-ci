package ngsdiaglim.modeles.biofeatures;

import ngsdiaglim.comparators.NaturalSortComparator;

public class Region implements Comparable<Region> {

    private String contig;
    private int start;
    private int end;
    private final String name;

    public Region(String contig, int start, int end, String name) {
        this.contig = contig;
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public String getContig() {return contig;}

    public void setContig(String contig) {
        this.contig = contig;
    }

    public int getStart() {return start;}

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {return end;}

    public void setEnd(int end) {
        this.end = end;
    }

    public String getName() {return name;}

    public int getSize() {
        return end - start;
    }

    public boolean overlaps(String contig, int start, int end) {
        return this.contig.equalsIgnoreCase(contig) &&
                this.start <= end && this.end >= start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;

        Region region = (Region) o;

        if (start != region.start) return false;
        if (end != region.end) return false;
        return contig != null ? contig.equalsIgnoreCase(region.contig) : region.contig == null;
    }

    @Override
    public int hashCode() {
        int result = contig != null ? contig.hashCode() : 0;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }


    @Override
    public int compareTo(Region o) {
        NaturalSortComparator naturalSortComparator = new NaturalSortComparator();
        int contigComp = naturalSortComparator.compare(this.contig, o.contig);
        if (contigComp != 0) return contigComp;
        else {
            if (o.start >= this.start && o.start < this.end) {
                return 0;
            }
            else if (o.start < this.start) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }
}
