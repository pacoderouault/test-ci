package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import ngsdiaglim.comparators.HotspotComparator;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.enumerations.HotspotType;

import java.util.Optional;

public class HotspotsSet {

    private final long id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final ObservableList<Hotspot> hotspots;
    private final SimpleBooleanProperty active = new SimpleBooleanProperty();
    private final static NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    public HotspotsSet(long id, String name, ObservableList<Hotspot> hotspots, boolean active) {
        this.id = id;
        this.name.set(name);
        this.hotspots = hotspots;
        this.active.set(active);

        hotspots.sort(new HotspotComparator());
    }

    public long getId() {return id;}

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableList<Hotspot> getHotspots() {return hotspots;}

    public boolean isActive() {
        return active.get();
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    @Override
    public String toString() {
        return name.get();
    }

//    public Hotspot getHotspot(Variant variant) {
//        Optional<Hotspot> hotspot = hotspots.parallelStream().filter(h -> {
//            if (h.getType().equals(HotspotType.POINT_MUTATION)) {
//                return h.getContig().equalsIgnoreCase(variant.getContig())
//                        && h.getStart() == variant.getStart()
//                        && h.getRef().equalsIgnoreCase(variant.getRef())
//                        && h.getAlt().equalsIgnoreCase(variant.getAlt());
//            } else {
//                return h.getContig().equalsIgnoreCase(variant.getContig())
//                        && h.getStart() <= variant.getEnd()
//                        && h.getEnd() >= variant.getStart();
//            }
//        }).findAny();
//        return hotspot.orElse(null);
//    }

//    public Hotspot getHotspot(Variant variant) {
//        Hotspot hotspot = getPositionHotspot(variant);
//        if (hotspot == null || hotspot.isHotspot()) return null;
//        else {
//    }

    /**
     * Recherche dichotomique
     * @param variant
     * @return
     */
    public Hotspot getHotspot(Variant variant) {
        int low = 0;
        int high = hotspots.size() - 1;
        // find the index of the variant position
        int pos = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            Hotspot h = hotspots.get(mid);
            int comp = compare(variant, h);
            if (comp < 0) {
                high = mid - 1;
            }
            else if (comp > 0) {
                low = mid + 1;
            }
            else {
                pos = mid;
                break;
            }
        }

        // now look if the the variant correspond to the hotspot at the index found
        // if not, look if there is hot spot at the same genomic position (flanking hotspots in the list)
        if (pos != -1) {
            Hotspot h = hotspots.get(pos);
            if (h.isHotspot(variant)) {
                return h;
            }
            else {
                // look for lower positions
                int m = pos - 1;
                while (m >= 0 && compare(variant, hotspots.get(m)) == 0) {
                    if (hotspots.get(m).isHotspot(variant)) {
                        return hotspots.get(m);
                    }
                    m--;
                }
                // look for higher positions
                m = pos + 1;
                while (m < hotspots.size() && compare(variant, hotspots.get(m)) == 0) {
                    if (hotspots.get(m).isHotspot(variant)) {
                        return hotspots.get(m);
                    }
                    m++;
                }
            }
        }
        return null;
    }

    // compare the genomic position of the variant against the hotspot
    private static int compare(Variant o1, Hotspot o2) {
        int comp;
        if (!o1.getContig().equalsIgnoreCase(o2.getContig())) {
            comp = naturalSortComparator.compare(o1.getContig(), o2.getContig());
        }
        else {
            if (o2.isOverlapping(o1.getContig(), o1.getStart(), o1.getEnd())) {
                return 0;
            }
            int start1 = o1.getStart();
            int start2 = o2.getStart();
            comp = Integer.compare(start1, start2);
        }
        return comp;
    }
}
