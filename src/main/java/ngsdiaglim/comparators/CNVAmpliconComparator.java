package ngsdiaglim.comparators;

import ngsdiaglim.cnv.CovCopRegion;

import java.util.Comparator;

public class CNVAmpliconComparator implements Comparator<CovCopRegion> {

    private static final NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    @Override
    public int compare(CovCopRegion o1, CovCopRegion o2) {
        int contigComp = naturalSortComparator.compare(o1.getContig(), o2.getContig());
        if (contigComp != 0) return contigComp;

        int startComp = Integer.compare(o1.getStart(), o2.getStart());
        if (startComp != 0) return startComp;

        return Integer.compare(o1.getEnd(), o2.getEnd());

    }
}
