package ngsdiaglim.cnv;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import ngsdiaglim.comparators.RegionComparator;
import ngsdiaglim.enumerations.CNVControlType;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.analyse.Panel;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CovCopCNVData {

    private LinkedHashMap<String, CNVSample> samples;
    private final ObservableList<CNVSample> visibleSamples = FXCollections.observableArrayList();
    private ObservableMap<String, ObservableList<CovCopRegion>> covcopRegions;
    private final ObservableList<CovCopRegion> covcopRegionList = FXCollections.observableArrayList();
    private TargetEnrichment algorithm;
    private CNVControlType controlType = CNVControlType.NONE;
    private final Panel panel;
    private CNVControlGroup controlGroup;
    private ObservableList<CNVSample> controlSamples;
    private int windowsSize;

    public CovCopCNVData(Panel panel) {
        this.panel = panel;
    }

    public LinkedHashMap<String, CNVSample> getSamples() {return samples;}

    public void setSamples(LinkedHashMap<String, CNVSample> samples) {
        this.samples = samples;
        this.samples.values().forEach(s -> s.visibleProperty().addListener((o, oldV, newV) -> setVisibleSamples()));
        setVisibleSamples();
    }

    private void setVisibleSamples() {
        visibleSamples.setAll(samples.values().stream().filter(CNVSample::isVisible).collect(Collectors.toList()));
    }

    public ObservableList<CNVSample> getVisibleSamples() {
        return visibleSamples;
    }

    public ObservableMap<String, ObservableList<CovCopRegion>> getCovcopRegions() {return covcopRegions;}

    public ObservableList<CovCopRegion> getCovcopRegions(String poolName) {
        return covcopRegions.get(poolName);
    }

    public void setCovcopRegions(ObservableMap<String, ObservableList<CovCopRegion>> covcopRegions) {
        this.covcopRegions = covcopRegions;
    }

//    public List<CovCopRegion> getCovcopRegionList() {return covcopRegionList;}

    public ObservableList<CovCopRegion> getAllCovcopRegionsAsList() {
        if (covcopRegionList.isEmpty()) {
            covcopRegions.values().forEach(covcopRegionList::addAll);
            covcopRegionList.sort(new RegionComparator());
        }
        return covcopRegionList;
    }

    public int getSampleIndex(String sampleName) {
        int i = 0;
        for (String s : samples.keySet()) {
            if (s.equals(sampleName)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void deletePool(String poolName, CNVSample sample) {
        if (covcopRegions.containsKey(poolName)) {
            int sampleIndex = getSampleIndex(sample.getBarcode());
            covcopRegions.get(poolName).parallelStream().forEach(a -> a.setRaw_value(sampleIndex, null));
        }
    }

    public void deletePools(CNVSample sample) {
        int sampleIndex = getSampleIndex(sample.getBarcode());
        covcopRegions.values().forEach(amplicons -> amplicons.parallelStream().forEach(a -> a.setRaw_value(sampleIndex, null)));

    }

    public TargetEnrichment getAlgorithm() {return algorithm;}

    public void setAlgorithm(TargetEnrichment algorithm) {
        this.algorithm = algorithm;
    }

    public CNVControlType getControlType() {return controlType;}

    public void setControlType(CNVControlType controlType) {
        this.controlType = controlType;
    }

    public Panel getPanel() {return panel;}

    public CNVControlGroup getControlGroup() {return controlGroup;}

    public void setControlGroup(CNVControlGroup controlGroup) {
        this.controlGroup = controlGroup;
    }

    public ObservableList<CNVSample> getControlSamples() {return controlSamples;}

    public void setControlSamples(ObservableList<CNVSample> controlSamples) {
        this.controlSamples = controlSamples;
    }

    public int getWindowsSize() {return windowsSize;}

    public void setWindowsSize(int windowsSize) {
        this.windowsSize = windowsSize;
    }
}
