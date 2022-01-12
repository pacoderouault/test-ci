package ngsdiaglim.cnv;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.modeles.analyse.Analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNVSample {

    private final Analysis analysis;
    private final SimpleStringProperty barcode = new SimpleStringProperty();
    private final SimpleObjectProperty<Gender> gender = new SimpleObjectProperty<>(Gender.FEMALE);
    private final SimpleBooleanProperty control = new SimpleBooleanProperty(false);
    private BoxplotData boxplotData;
    private Map<String, BoxplotData> boxplotDatabyPool = new HashMap<>();
    private final List<CNV> cnvList = new ArrayList<>();
    private final SimpleBooleanProperty visible = new SimpleBooleanProperty(true);
    private Double meanOfNormalValues;
    private Double stdOfNormalValues;
//    private List<Annotation> variants = new ArrayList<>()

    public CNVSample(Analysis analysis, String barcode) {
        this.analysis = analysis;
        this.barcode.set(barcode);
    }

    public Analysis getAnalysis() {return analysis;}

    public String getBarcode() {
        return barcode.get();
    }

    public SimpleStringProperty barcodeProperty() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public Gender getGender() {
        return gender.get();
    }

    public SimpleObjectProperty<Gender> genderProperty() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender.set(gender);
    }

    public boolean isControl() {
        return control.get();
    }

    public SimpleBooleanProperty controlProperty() {
        return control;
    }

    public void setControl(boolean control) {
        this.control.set(control);
    }

    public BoxplotData getBoxplotData() {return boxplotData;}

    public void setBoxplotData(BoxplotData boxplotData) {
        this.boxplotData = boxplotData;
    }

    public Map<String, BoxplotData> getBoxplotDatabyPool() {return boxplotDatabyPool;}

    public void setBoxplotDatabyPool(Map<String, BoxplotData> boxplotDatabyPool) {
        this.boxplotDatabyPool = boxplotDatabyPool;
    }

    public void addBoxPlotDataPool(String pool, BoxplotData boxplotData) {
        boxplotDatabyPool.put(pool, boxplotData);
    }

    public void cleanData() {
        boxplotData = null;
        boxplotDatabyPool = new HashMap<>();
    }

    public List<CNV> getCNV() {return cnvList;}

    public void addCNV(CNV cnv) {
        cnvList.add(cnv);
    }

    public boolean isVisible() {
        return visible.get();
    }

    public SimpleBooleanProperty visibleProperty() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public Double getMeanOfNormalValues() {return meanOfNormalValues;}

    public void setMeanOfNormalValues(Double meanOfNormalValues) {
        this.meanOfNormalValues = meanOfNormalValues;
    }

    public Double getStdOfNormalValues() {return stdOfNormalValues;}

    public void setStdOfNormalValues(Double stdOfNormalValues) {
        this.stdOfNormalValues = stdOfNormalValues;
    }

    @Override
    public String toString() {
        return barcode.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CNVSample cnvSample = (CNVSample) o;

        return barcode.equals(cnvSample.barcode);
    }

    @Override
    public int hashCode() {
        return barcode.hashCode();
    }
}
