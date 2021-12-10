package ngsdiaglim.cnv.normalization;

import ngsdiaglim.cnv.CovCopCNVData;

public abstract class CNVNormalization {

    protected final CovCopCNVData cnvData;

    public CNVNormalization(CovCopCNVData cnvData) {
        this.cnvData = cnvData;
    }

    public abstract void normalize() throws Exception;

    protected abstract Double valueCorrection(Double value, Double fitter);

    public void initNormalizedValues() {
        cnvData.getCovcopRegions().values().forEach(amplicons -> amplicons.forEach(a -> {
            a.getNormalized_values().clear();
            a.getzScores().clear();
            for (Integer i : a.getRaw_values()) {

                if (i == null) {
                    a.addNormalizedValue(null);
                    a.addZScore(null);
                }
                else {
                    Double d = i.doubleValue();
                    a.addNormalizedValue(d);
                }
            }
        }));
    }
}
