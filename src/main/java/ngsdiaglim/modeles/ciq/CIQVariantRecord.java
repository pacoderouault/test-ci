package ngsdiaglim.modeles.ciq;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.CIQRecordState;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.utils.NumberUtils;

import java.sql.SQLException;
import java.util.List;

public class CIQVariantRecord {

    private long id;
    private final CIQHotspot hotspot;
    private final Analysis analysis;
    private final SimpleIntegerProperty dp = new SimpleIntegerProperty();
    private final SimpleIntegerProperty ao = new SimpleIntegerProperty();
    private final SimpleFloatProperty vaf = new SimpleFloatProperty();
    private final ObservableList<CIQRecordHistory> history = FXCollections.observableArrayList();

    public CIQVariantRecord(long id, CIQHotspot hotspot, Analysis analysis, int dp, int ao, float vaf, List<CIQRecordHistory> history) {
        this(hotspot, analysis, dp, ao, vaf, history);
        this.id = id;
    }

    public CIQVariantRecord(CIQHotspot hotspot, Analysis analysis, int dp, int ao, float vaf, List<CIQRecordHistory> history) {
        this.hotspot = hotspot;
        this.analysis = analysis;
        this.dp.set(dp);
        this.ao.set(ao);
        this.vaf.set(vaf);
        if (!history.isEmpty()) {
            this.history.setAll(history);
        }
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public Analysis getAnalysis() {return analysis;}

    public CIQHotspot getHotspot() {return hotspot;}

    public int getDp() {
        return dp.get();
    }

    public SimpleIntegerProperty dpProperty() {
        return dp;
    }

    public void setDp(int dp) {
        this.dp.set(dp);
    }

    public int getAo() {
        return ao.get();
    }

    public SimpleIntegerProperty aoProperty() {
        return ao;
    }

    public void setAo(int ao) {
        this.ao.set(ao);
    }

    public float getVaf() {
        return vaf.get();
    }

    public SimpleFloatProperty vafProperty() {
        return vaf;
    }

    public void setVaf(float vaf) {
        this.vaf.set(vaf);
    }

    public double getVafRounded() {
        return NumberUtils.round(vaf.get(), 3);
    }

    public double getVafRounded(int decimal) {
        return NumberUtils.round(vaf.get(), decimal);
    }

    public boolean isAccepted() {
        CIQRecordHistory lastState = getLastHistory();
        return lastState != null && lastState.getNewState().equals(CIQRecordState.ACCEPTED);
    }

    public ObservableList<CIQRecordHistory> getHistory() {return history;}

    public CIQRecordHistory getLastHistory() {
        if (history.isEmpty()) return null;
        return history.get(history.size() - 1);
    }

    public void loadHistory() throws SQLException {
        history.setAll(DAOController.getCiqRecordHistoryDAO().getRecordHistory(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CIQVariantRecord)) return false;

        CIQVariantRecord that = (CIQVariantRecord) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
