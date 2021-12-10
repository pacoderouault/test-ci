package ngsdiaglim.cnv.normalization;

import ngsdiaglim.cnv.CovCopCNVData;

public class LogTransformation extends CNVNormalization {

    public LogTransformation(CovCopCNVData cnvData) {
        super(cnvData);
    }

    public void normalize() {
        for (int sampleIndex = 0; sampleIndex < cnvData.getSamples().size(); sampleIndex++) {

            // for each pool construct the loess regression
            for (String poolName : cnvData.getCovcopRegions().keySet()) {
                int finalSampleIndex = sampleIndex;
                cnvData.getCovcopRegions(poolName).forEach(a -> {
                    if (a.getNormalized_values().get(finalSampleIndex) != null) {

                        double normalizedValue = valueCorrection(a.getNormalized_values().get(finalSampleIndex), null);
                        a.addNormalizedValue(normalizedValue, finalSampleIndex);
                    }
                });
            }
        }
    }


    protected Double valueCorrection(Double value, Double fitter) {
        if (value == 0d) {
            return 0d;
        }
        return Math.log(value);
    }
}
