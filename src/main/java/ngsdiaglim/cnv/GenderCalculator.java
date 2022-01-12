package ngsdiaglim.cnv;


import ngsdiaglim.enumerations.Gender;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GenderCalculator {

    public static void calculateGender(CovCopCNVData data) {
        int sampleIdx = 0;
        for (String sampleName : data.getSamples().keySet()) {
            CNVSample cnvSample = data.getSamples().get(sampleName);

            List<Integer> chrYValues = new ArrayList<>();

            int finalSampleIdx = sampleIdx;
            data.getCovcopRegions().values().forEach(amplicons -> amplicons.forEach(a -> {
                if (StringUtils.containsIgnoreCase(a.getContig(), "Y")) {
                    chrYValues.add(a.getRaw_values().get(finalSampleIdx));
                }
            }));

            if (chrYValues.size() > 0) {
                boolean highValue = chrYValues.parallelStream().anyMatch(v -> v != null && v > 50);
                if (highValue) {
                    cnvSample.setGender(Gender.MALE);
                }
                else {
                    cnvSample.setGender(Gender.FEMALE);
                }


            }
            sampleIdx++;
        }

    }

}
