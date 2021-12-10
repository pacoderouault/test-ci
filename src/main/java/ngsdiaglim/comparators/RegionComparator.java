package ngsdiaglim.comparators;

import ngsdiaglim.modeles.biofeatures.Region;

import java.util.Comparator;

public class RegionComparator implements Comparator<Region> {

    private static final NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    @Override
    public int compare(Region o1, Region o2) {
        if (!o1.getContig().equalsIgnoreCase(o2.getContig())) {
            return naturalSortComparator.compare(o1.getContig(), o2.getContig());
        } else if (o1.getStart() != o2.getStart()) {
            return o1.getStart() - o2.getStart();
        } else {
            return o1.getEnd() - o2.getEnd();
        }
    }
}
