package ngsdiaglim.stats;

import ngsdiaglim.cnv.caller.CNVDetectionRobustZScore;
import ngsdiaglim.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;


public class MAD {

    private static final double madCoeff = 0.6745;
//    private static final double madCoeff = 1.4826;

    public static Double getMAD(List<Double> values) {
        values.sort(Double::compareTo);
        Double median = MathUtils.median(values);
        List<Double> correctedValues = new ArrayList<>();
        for (Double d : values) {
            correctedValues.add(Math.abs(d - median));
        }
        correctedValues.sort(Double::compareTo);
        return MathUtils.median(correctedValues);
    }

    public static double getZScore(double median, double mad, double value) {
        return (madCoeff * (value - median)) / mad;
    }

    public static double maxValue(double median, double mad) {
        return median + (CNVDetectionRobustZScore.dupThreshold * mad);
    }

    public static double minValue(double median, double mad) {
        return median - (CNVDetectionRobustZScore.dupThreshold * mad);
    }
}
