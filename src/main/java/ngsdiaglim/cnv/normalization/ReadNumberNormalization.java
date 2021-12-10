package ngsdiaglim.cnv.normalization;

import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;

public class ReadNumberNormalization extends CNVNormalization {

    public ReadNumberNormalization(CovCopCNVData cnvData) {
        super(cnvData);
    }

    public void normalize() {

        for (int sampleIndex = 0; sampleIndex < cnvData.getSamples().size(); sampleIndex++) {
            for (String poolName : cnvData.getCovcopRegions().keySet()) {
                Double totalReads = 0d;
                for (CovCopRegion a : cnvData.getCovcopRegions().get(poolName)) {
                    if (a.getNormalized_values().get(sampleIndex) != null) {
                        totalReads += a.getNormalized_values().get(sampleIndex);
                    }
                }
                for (CovCopRegion a : cnvData.getCovcopRegions().get(poolName)) {
                    if (a.getNormalized_values().get(sampleIndex) != null) {
                        Double normalizedValue = valueCorrection(a.getNormalized_values().get(sampleIndex), totalReads);
                        a.getNormalized_values().set(sampleIndex, normalizedValue);
                    }
                }
            }
        }
    }


    protected Double valueCorrection(Double value, Double fitter) {
        return value / fitter * 100000000;
    }
}
