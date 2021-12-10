package ngsdiaglim.modeles.analyse;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import ngsdiaglim.database.DAOController;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Panel {

    private final long id;
    private String name;
    private boolean active;
    private File bedFile;
    private ObservableList<PanelRegion> regions;

    public Panel(long id, String name, boolean active, File bedFile) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.bedFile = bedFile;
    }

    public long getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {return active;}

    public void setActive(boolean active) {
        this.active = active;
    }

    public ObservableList<PanelRegion> getRegions() throws SQLException {
        if (regions == null) {
            loadRegions();
        }
        return regions;
    }

    public PanelRegion getRegion(String regionName) throws SQLException {
        Optional<PanelRegion> opt = getRegions().parallelStream().filter(p -> p.getName().equals(regionName)).findAny();
        return opt.orElse(null);
    }

    public PanelRegion getRegion(String contig, int start, int end) throws SQLException {
        Optional<PanelRegion> opt = getRegions().parallelStream().filter(p -> p.overlaps(contig, start, end)).findAny();
        return opt.orElse(null);
    }

    public List<PanelRegion> getRegions(String contig, int start, int end) throws SQLException {
        return getRegions().parallelStream().filter(p -> p.overlaps(contig, start, end)).collect(Collectors.toList());
    }

    private void loadRegions() throws SQLException {
        regions = DAOController.getPanelRegionDAO().getPanelRegions(id);
    }

    public int getSize() throws SQLException {
        return getRegions().stream().mapToInt(PanelRegion::getSize).sum();
    }

    public File getBedFile() {return bedFile;}

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Panel panel = (Panel) o;

        return id == panel.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
