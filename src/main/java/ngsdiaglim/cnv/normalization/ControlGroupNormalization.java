package ngsdiaglim.cnv.normalization;

import javafx.collections.ObservableList;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.exceptions.FileFormatException;
import ngsdiaglim.utils.MathUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControlGroupNormalization extends CNVNormalization {

    private final CovCopCNVData controlData;

    public ControlGroupNormalization(CovCopCNVData cnvData, CovCopCNVData controlData) {
        super(cnvData);
        this.controlData = controlData;
    }

    public void normalize() throws IOException, FileFormatException {
        normalize(null);
    }

    public void normalize(Map<String, Set<Integer>> excludedValuesIndex) throws IOException, FileFormatException {


        for (String poolName : cnvData.getCovcopRegions().keySet()) {

            if (!controlData.getCovcopRegions().containsKey(poolName)) {
                throw new FileFormatException("No pool name " + poolName + " found in control matrix");
            }


            for (int regionIdx = 0; regionIdx< cnvData.getCovcopRegions(poolName).size(); regionIdx++) {
                CovCopRegion region = cnvData.getCovcopRegions(poolName).get(regionIdx);
                CovCopRegion ctrlRegion = controlData.getCovcopRegions(poolName).get(regionIdx);

                if (!region.equals(ctrlRegion)) {
                    throw new FileFormatException("The regions between Controls and Samples are not matching.");
                }

                List<Double> controlValues = new ArrayList<>();
                for (Double ctrValue : ctrlRegion.getNormalized_values()) {
                    if (ctrValue != null) {
                        controlValues.add(ctrValue);
                    }
                }
                controlValues.sort(Double::compareTo);
                Double controlMedian = MathUtils.median(controlValues);
                for (int i = 0; i < cnvData.getSamples().size(); i++) {
                    Double normalizedValue;
                    if (controlMedian != null && controlMedian != 0) {
                        normalizedValue = valueCorrection(region.getNormalized_values().get(i), controlMedian);
                    }
                    else {
                        normalizedValue = 0d;
                    }

                    region.addNormalizedValue(normalizedValue, i);
                }
            }
        }
    }

    protected Double valueCorrection(Double value, Double fitter) {
        if (value == null) {
            return value;
        }
        return NumberUtils.round(value / fitter, 2);
    }
}
