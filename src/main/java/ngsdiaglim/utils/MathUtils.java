package ngsdiaglim.utils;

import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class MathUtils {

    public static double meanOfInt(List<Integer> values) {
        if (values.isEmpty()) return 0;
        int sum = 0;
        int size = 0;
        for (Integer v : values) {
            if (v != null) {
                sum += v;
                size += 1;
            }
        }
        return sum / (size * 1.0);

    }

    public static Double median(List<Double> values) {
        List<Double> notNullValues = values.stream().filter(d -> d != null && !d.isNaN() && !d.isInfinite()).sorted().collect(Collectors.toList());
        if (notNullValues.isEmpty()) {
            return null;
        }

        double index = (notNullValues.size() + 1) / 2.0 - 1;
        if ((index % 1) == 0) {
            return notNullValues.get((int) index);
        }
        else {
            int idx = (int) index;
            return (notNullValues.get(idx) + notNullValues.get(idx + 1)) / 2.0;
        }
    }

    public static double meanOfDouble(List<Double> values) {
        if (values.isEmpty()) return 0;
        double sum = 0d;
        int valuesNb = 0;
        for (Double v : values) {
            if (v != null) {
                sum += v;
                valuesNb++;
            }
        }
        if (valuesNb > 0) {
            System.out.println("sum : " + sum);
            System.out.println("valuesNb : " + valuesNb);
            return sum / (valuesNb * 1.0);
        }
        return 0;
    }


    /**
     * From a list of double return a pair of mean and standard deviation
     * @param values
     * @return Pair<Mean, Std>
     */
    public static Pair<Double, Double> findDeviation(List<Double> values) {
        double mean = meanOfDouble(values);
        double squareSum = 0;
        int valuesNb = 0;
        for (Double v : values) {
            if (v != null) {
                squareSum += Math.pow(v - mean, 2);
                valuesNb++;
            }
        }
        if (valuesNb > 0) {
            return new Pair<>(mean, Math.sqrt(squareSum / (valuesNb -1)));
        } else {
            return null;
        }
    }
}
