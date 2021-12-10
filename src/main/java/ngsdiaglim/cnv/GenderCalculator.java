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

//            List<Integer> noChrYValues = new ArrayList<>();
            List<Integer> chrYValues = new ArrayList<>();

            int finalSampleIdx = sampleIdx;
            data.getCovcopRegions().values().forEach(amplicons -> amplicons.forEach(a -> {
                if (StringUtils.containsIgnoreCase(a.getContig(), "Y")) {
                    chrYValues.add(a.getRaw_values().get(finalSampleIdx));
                }
//                else {
//                    noChrYValues.add(a.getRaw_values().get(finalSampleIdx));
//                }
            }));

            if (chrYValues.size() > 0) {
//                double mean = MathUtils.meanOfInt(noChrYValues);
//                double std = MathUtils.std(noChrYValues, mean);
//                double p1 = mean - 2.33 * std;
//
//                System.out.println(sampleName);
//                System.out.println("mean : " + mean);
//                System.out.println("std : " + std);
//                System.out.println("p1 : " + p1);

                boolean highValue = chrYValues.parallelStream().anyMatch(v -> v != null && v > 50);
//                System.out.println("highValue : " + highValue);
//                System.out.println();
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
