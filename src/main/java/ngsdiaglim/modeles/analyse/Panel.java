package ngsdiaglim.modeles.analyse;

import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import ngsdiaglim.database.DAOController;

import java.sql.SQLException;

public class Panel {

    private final long id;
    private String name;
    private boolean active;
    private ObservableList<PanelRegion> regions;

    public Panel(long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
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

    private void loadRegions() throws SQLException {
        regions = DAOController.get().getPanelRegionDAO().getPanelRegions(id);
    }


    public int getSize() throws SQLException {
        return getRegions().stream().mapToInt(PanelRegion::getSize).sum();
    }


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
