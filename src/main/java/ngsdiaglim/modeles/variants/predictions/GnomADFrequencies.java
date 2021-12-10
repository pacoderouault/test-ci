package ngsdiaglim.modeles.variants.predictions;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;

public class GnomADFrequencies {

    private SimpleObjectProperty<GnomadPopulationFreq> afr;
    private SimpleObjectProperty<GnomadPopulationFreq> amr;
    private SimpleObjectProperty<GnomadPopulationFreq> asj;
    private SimpleObjectProperty<GnomadPopulationFreq> eas;
    private SimpleObjectProperty<GnomadPopulationFreq> fin;
    private SimpleObjectProperty<GnomadPopulationFreq> nfe;
    private SimpleObjectProperty<GnomadPopulationFreq> sas;
    private SimpleObjectProperty<GnomadPopulationFreq> max;

    public GnomadPopulationFreq getAfr() {
        if (afr == null) return null;
        return afr.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> afrProperty() {
        return afr;
    }

    public void setAfr(GnomadPopulationFreq afr) {
        if (this.afr == null) this.afr = new SimpleObjectProperty<>();
//        this.afr.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.afr.set(afr);
    }

    public GnomadPopulationFreq getAmr() {
        if (amr == null) return null;
        return amr.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> amrProperty() {
        return amr;
    }

    public void setAmr(GnomadPopulationFreq amr) {
        if (this.amr == null) this.amr = new SimpleObjectProperty<>();
//        this.amr.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.amr.set(amr);
    }

    public GnomadPopulationFreq getAsj() {
        if (asj == null) return null;
        return asj.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> asjProperty() {
        return asj;
    }

    public void setAsj(GnomadPopulationFreq asj) {
        if (this.asj == null) this.asj = new SimpleObjectProperty<>();
//        this.asj.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.asj.set(asj);
    }

    public GnomadPopulationFreq getEas() {
        if (eas == null) return null;
        return eas.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> easProperty() {
        return eas;
    }

    public void setEas(GnomadPopulationFreq eas) {
        if (this.eas == null) this.eas = new SimpleObjectProperty<>();
//        this.eas.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.eas.set(eas);
    }

    public GnomadPopulationFreq getFin() {
        if (fin == null) return null;
        return fin.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> finProperty() {
        return fin;
    }

    public void setFin(GnomadPopulationFreq fin) {
        if (this.fin == null) this.fin = new SimpleObjectProperty<>();
//        this.fin.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.fin.set(fin);
    }

    public GnomadPopulationFreq getNfe() {
        if (nfe == null) return null;
        return nfe.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> nfeProperty() {
        return nfe;
    }

    public void setNfe(GnomadPopulationFreq nfe) {
        if (this.nfe == null) this.nfe = new SimpleObjectProperty<>();
//        this.nfe.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.nfe.set(nfe);
    }

    public GnomadPopulationFreq getSas() {
        if (sas == null) return null;
        return sas.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> sasProperty() {
        return sas;
    }

    public void setSas(GnomadPopulationFreq sas) {
        if (this.sas == null) this.sas = new SimpleObjectProperty<>();
//        this.sas.addListener((obs, oldV, newV) -> computeMaxGnomad());
        this.sas.set(sas);
    }

    private final Annotation annotation;

    public GnomADFrequencies(Annotation annotation) {
        this.annotation = annotation;
    }

    public void computeMaxGnomad() {
//        float maxFreq = -1;
        if (max == null || (afr != null && afr.get().getAf() > max.get().getAf())) max = afr;
        if (max == null || (amr != null && amr.get().getAf() > max.get().getAf())) max = amr;
        if (max == null || (asj != null && asj.get().getAf() > max.get().getAf())) max = asj;
        if (max == null || (eas != null && eas.get().getAf() > max.get().getAf())) max = eas;
        if (max == null || (fin != null && fin.get().getAf() > max.get().getAf())) max = fin;
        if (max == null || (nfe != null && nfe.get().getAf() > max.get().getAf())) max = nfe;
        if (max == null || (sas != null && sas.get().getAf() > max.get().getAf())) max = sas;
    }

    public GnomadPopulationFreq getMax() {
        if (max == null) return null;
        return max.get();
    }

    public SimpleObjectProperty<GnomadPopulationFreq> maxProperty() {
        return max;
    }
}
