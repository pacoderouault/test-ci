package ngsdiaglim.comparators;

import ngsdiaglim.modeles.variants.Hotspot;

import java.util.Comparator;

public class HotspotComparator implements Comparator<Hotspot> {

    private static final NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    @Override
    public int compare(Hotspot o1, Hotspot o2) {
        if (!o1.getContig().equalsIgnoreCase(o2.getContig())) {
            return naturalSortComparator.compare(o1.getContig(), o2.getContig());
        } else if (o1.getStart() != o2.getStart()) {
            return o1.getStart() - o2.getStart();
        } else {
            return o1.getEnd() - o2.getEnd();
        }
    }
}

