package ngsdiaglim.cnv.normalization;

import javafx.collections.ObservableList;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.utils.MathUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SamplesNormalization extends CNVNormalization {

    public SamplesNormalization(CovCopCNVData cnvData) {
        super(cnvData);
    }

    public void normalize() throws IOException {
        normalize(null);
    }

    public void normalize(Map<String, Set<Integer>> excludedValuesIndex) throws IOException {

        List<Integer> controlsIndex = getControlSamples();
        for (ObservableList<CovCopRegion> amplicons : cnvData.getCovcopRegions().values()) {
            for (CovCopRegion a : amplicons) {
                if (controlsIndex.size() > 0) {
                    List<Double> samplesValues = new ArrayList<>();
                    for (Integer i : controlsIndex) {
                        if (a.getNormalized_values().get(i) != null) {
                            samplesValues.add(a.getNormalized_values().get(i));
                        }
                    }

                    samplesValues.sort(Double::compareTo);
                    Double median = MathUtils.median(samplesValues);
                    for (int i = 0; i < cnvData.getSamples().size(); i++) {
                        Double normalizedValue;
                        if (median != null && median != 0) {
                            normalizedValue = valueCorrection(a.getNormalized_values().get(i), median);
                        }
                        else {
                            normalizedValue = 0d;
                        }

                        a.addNormalizedValue(normalizedValue, i);
                    }
                }
                else {
                    List<Double> medianList = new ArrayList<>();
                    for (int i = 0; i < cnvData.getSamples().size(); i++) {
                        List<Double> samplesValues = new ArrayList<>();
                        for (int j = 0; j < cnvData.getSamples().size(); j++) {
                            if (i != j && a.getNormalized_values().get(j) != null) {
                                if (excludedValuesIndex == null || !excludedValuesIndex.containsKey(a.getName()) || !excludedValuesIndex.get(a.getName()).contains(j)) {
                                    samplesValues.add(a.getNormalized_values().get(j));
                                }
                            }
                        }

                        if (samplesValues.isEmpty()) {
                            // include all values
                            for (int j = 0; j < cnvData.getSamples().size(); j++) {
                                if (i != j && a.getNormalized_values().get(j) != null) {
                                    samplesValues.add(a.getNormalized_values().get(j));
                                }
                            }
                        }

                        samplesValues.sort(Double::compareTo);
                        Double median = MathUtils.median(samplesValues);

                        medianList.add(median);
                    }
                    for (int i = 0; i < cnvData.getSamples().size(); i++) {
                        if (a.getNormalized_values().get(i) != null) {
                            Double normalizedValue;
                            if (medianList.get(i) != null && medianList.get(i) != 0) {
                                normalizedValue = valueCorrection(a.getNormalized_values().get(i), medianList.get(i));
                                if (normalizedValue < 0) {
                                    normalizedValue = 0d;
                                }
                            } else {
                                normalizedValue = 0d;
                            }

                            a.addNormalizedValue(normalizedValue, i);
                        }
                    }
                }
            }
        }
    }

    private List<Integer> getControlSamples() {
        int sampleIndex = 0;
        List<Integer> controlsIndex = new ArrayList<>();
        for (String sampleName : cnvData.getSamples().keySet()) {
            CNVSample sample = cnvData.getSamples().get(sampleName);
            if (sample.isControl()) controlsIndex.add(sampleIndex);
            sampleIndex++;
        }
        return controlsIndex;
    }

    protected Double valueCorrection(Double value, Double fitter) {
        if (value == null) {
            return value;
        }
        return NumberUtils.round(value / fitter, 2);
    }
}
