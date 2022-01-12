package ngsdiaglim.cnv;

import ngsdiaglim.stats.Quartiles;
import ngsdiaglim.stats.QuartilesCalculator;
import ngsdiaglim.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class QualityCalculator {

    private static final int threshold = 50;

    public static void calculateStatistics(CovCopCNVData data) {
//        QuartilesCalculator quartilesCalculator = new QuartilesCalculator();

        int sampleIdx = 0;

        for (String sampleName : data.getSamples().keySet()) {

            CNVSample cnvSample = data.getSamples().get(sampleName);
            cnvSample.cleanData();

            // all pools
            List<Integer> values = new ArrayList<>();
            int finalSampleIdx = sampleIdx;

            data.getCovcopRegions().values().forEach(amplicons -> amplicons.forEach(a -> {
                Integer v = a.getRaw_values().get(finalSampleIdx);
                if (v != null) {
                    values.add(v);
                }
            }));

            Quartiles allPoolsQuartiles = null;
            if (values.size() >= 4) {
                allPoolsQuartiles = QuartilesCalculator.getQuartiles(values);
            }

            Double mean = null;
            if (values.size() > 0) {
                mean = MathUtils.meanOfInt(values);
            }
            long lowAmplicons = numberOfAmpliconBelowThreshold(values);
            BoxplotData boxplotData = null;

            if (values.size() >= 4) {
                boxplotData = new BoxplotData(sampleName, allPoolsQuartiles, mean, lowAmplicons, values);
            }

            cnvSample.setBoxplotData(boxplotData);

            for (String pool : data.getCovcopRegions().keySet()) {
                List<Integer> valuesByPool = new ArrayList<>();
                data.getCovcopRegions().get(pool).forEach(a -> {
                    Integer v = a.getRaw_values().get(finalSampleIdx);
                    if (v != null) {
                        valuesByPool.add(a.getRaw_values().get(finalSampleIdx));
                    }
                });
                Quartiles quartilesByPool = null;
                if (values.size() >= 4) {
                    quartilesByPool = QuartilesCalculator.getQuartiles(valuesByPool);
                }

                Double meanByPool = null;
                if(valuesByPool.size() > 0) {
                    meanByPool = MathUtils.meanOfInt(valuesByPool);
                }
                long lowAmpliconsByPool = numberOfAmpliconBelowThreshold(valuesByPool);
                BoxplotData boxplotDataByPool = null;
                if (valuesByPool.size() >= 4) {
                    boxplotDataByPool = new BoxplotData(sampleName, quartilesByPool, meanByPool, lowAmpliconsByPool, valuesByPool);
                }
                cnvSample.addBoxPlotDataPool(pool, boxplotDataByPool);
            }
            sampleIdx++;
        }
    }

    public static long numberOfAmpliconBelowThreshold(List<Integer> values) {
        return values.parallelStream().filter(v -> v != null && v < threshold).count();
    }
}
