package ngsdiaglim.cnv.normalization;

import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.enumerations.Gender;

public class GenderNormalization extends CNVNormalization {
    
    public GenderNormalization(CovCopCNVData cnvData) {
        super(cnvData);
    }

    @Override
    public void normalize() {
        int sampleIndex = 0;
        for (String sampleName : cnvData.getSamples().keySet()) {
            CNVSample sample = cnvData.getSamples().get(sampleName);
            if (sample.getGender().equals(Gender.MALE)) {
                int finalSampleIndex = sampleIndex;
                cnvData.getCovcopRegions().values().forEach(amplicons -> amplicons.forEach(a -> {
                    if ((a.getContig().equalsIgnoreCase("chrX")
                            || a.getContig().equalsIgnoreCase("X")) && a.getNormalized_values().get(finalSampleIndex) != null) {
                        Double normalizedValue = valueCorrection(a.getNormalized_values().get(finalSampleIndex), null);
                        a.addNormalizedValue(normalizedValue, finalSampleIndex);
                    }
                }));
            }
            sampleIndex++;
        }
    }

    @Override
    protected Double valueCorrection(Double value, Double fitter) {
        return value * 2.0;
    }
}
