package ngsdiaglim.stats;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuartilesCalculator {

    public static Quartiles getQuartiles(List<Integer> values) {

        List<Integer> noNullValues = values.parallelStream().filter(Objects::nonNull).collect(Collectors.toList());

        if (noNullValues.size() > 4) {
            noNullValues.sort(Integer::compareTo);

            double q1 = getFirstQuartile(noNullValues);
            double q2 = getMedian(noNullValues);
            double q3 = getThirdQuartile(noNullValues);
            return new Quartiles(q1, q2, q3, noNullValues);
        }
        else {
            return new Quartiles(null, null, null);
        }
    }


    /**
     * values must be sorted ascending
     * @param values
     */
    public static double getFirstQuartile(List<Integer> values) {
        double index = (values.size() + 3) / 4.0 - 1;
        if ((index % 1) == 0) {
            return values.get((int) index);
        }
        else {
            int modulo = (values.size() + 3) % 4;
            int idx = (values.size() + 3) / 4 - 1;

            if (modulo == 1) {
                return (3 * values.get(idx) + values.get(idx + 1)) / 4.0;
            }
            else if (modulo == 2 ) {
                return (values.get(idx) + values.get(idx + 1)) / 2.0;
            }
            else {
                return (values.get(idx) + 3 * values.get(idx + 1)) / 4.0;
            }
        }
    }


    /**
     * values must be sorted ascending
     * @param values
     */
    public static double getThirdQuartile(List<Integer> values) {
        double index = (values.size() * 3 + 1) / 4.0 - 1;
        if ((index % 1) == 0) {
            return values.get((int) index);
        }
        else {
            int modulo = (values.size() * 3 + 1) % 4;
            int idx = (values.size() * 3 + 1) / 4 - 1;

            if (modulo == 1) {
                return (3 * values.get(idx) + values.get(idx + 1)) / 4.0;
            }
            else if (modulo == 2 ) {
                return (values.get(idx) + values.get(idx + 1)) / 2.0;
            }
            else {
                return (values.get(idx) + 3 * values.get(idx + 1)) / 4.0;
            }
        }
    }

    /**
     * values must be sorted ascending
     * @param values
     */
    public static double getMedian(List<Integer> values) {
        double index = (values.size() + 1) / 2.0 - 1;
        if ((index % 1) == 0) {
            return values.get((int) index);
        }
        else {
            int idx = (int) index;
            return (values.get(idx) + values.get(idx + 1)) / 2.0;
        }
    }
}
