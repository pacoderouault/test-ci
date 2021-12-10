package ngsdiaglim.controllers.analysisview.cnv;

import de.gsi.chart.renderer.RendererDataReducer;
import de.gsi.dataset.utils.ProcessingProfiler;

public class MaxDataReducer2 implements RendererDataReducer {
    
    private static final int DEFAULT_MAX_POINTS_COUNT = 10000;

    // TODO: check again algorithm with original implementation... some error was introduced
    private int reduce(final double[] xValues, final double[] yValues, final double[] xPointErrorsPos,
                       final double[] xPointErrorsNeg, final double[] yPointErrorsPos, final double[] yPointErrorsNeg,
                       final String[] styles, final boolean[] pointSelected, final int indexMin, final int indexMax,
                       final int maxPointsCount) {
        final long start = ProcessingProfiler.getTimeStamp();
        final int size = indexMax - indexMin;
        int count = 0;
        int n = indexMin;
        int nLastAdded = n;
        MaxDataReducer2.moveInPlace(xValues, yValues, xPointErrorsPos, xPointErrorsNeg, yPointErrorsPos, yPointErrorsNeg,
                styles, pointSelected, n++, count++);
        final double d = (double) size / (double) maxPointsCount;
        int b = 1;
        final int limit = (int) Math.round(b * d);

        // difference w.r.t. last point
        double delta = Math.abs(yValues[n] - yValues[count - 1]);
        while (n < indexMax) {
            if (n - nLastAdded >= limit) {
                nLastAdded = n;
                MaxDataReducer2.moveInPlace(xValues, yValues, xPointErrorsPos, xPointErrorsNeg, yPointErrorsPos,
                        yPointErrorsNeg, styles, pointSelected, n++, count++);
                // System.err.println(String.format("add point(%d)=%f",count,delta));
                // yValues[count-1] += delta;
                b++;
                // limit = (int) Math.round(b * d);
            } else {
                final double delta1 = Math.abs(yValues[n] - yValues[count - 1]);
                if (delta1 > delta) {
                    delta = delta1;
                }
            }
            n++;
        }

        if (ProcessingProfiler.getDebugState()) {
            ProcessingProfiler.getTimeDiff(start,
                    String.format("data reduction (from %d to %d)", indexMax - indexMin, count));
        }
        return count;
    }

    @Override
    public int reducePoints(final double[] xValues, final double[] yValues, final double[] xPointErrorsPos,
                            final double[] xPointErrorsNeg, final double[] yPointErrorsPos, final double[] yPointErrorsNeg,
                            final String[] styles, final boolean[] pointSelected, final int indexMin, final int indexMax) {
        final int size = indexMax - indexMin;
        if (size <= MaxDataReducer2.DEFAULT_MAX_POINTS_COUNT) {
            // just shift the data set to front
            MaxDataReducer2.shiftDataToFront(xValues, indexMin, indexMax);
            MaxDataReducer2.shiftDataToFront(yValues, indexMin, indexMax);

            if (xPointErrorsPos != null) {
                // may be null due to a CachedDataPoint optimisation
                MaxDataReducer2.shiftDataToFront(xPointErrorsPos, indexMin, indexMax);
            }
            if (xPointErrorsNeg != null) {
                // may be null due to a CachedDataPoint optimisation
                MaxDataReducer2.shiftDataToFront(xPointErrorsNeg, indexMin, indexMax);
            }
            MaxDataReducer2.shiftDataToFront(yPointErrorsPos, indexMin, indexMax);
            MaxDataReducer2.shiftDataToFront(yPointErrorsNeg, indexMin, indexMax);

            MaxDataReducer2.shiftDataToFront(styles, indexMin, indexMax);
            MaxDataReducer2.shiftDataToFront(pointSelected, indexMin, indexMax);

            return size;
        }

        return reduce(xValues, yValues, //
                xPointErrorsPos == null ? new double[indexMax - indexMin] : xPointErrorsPos, //
                xPointErrorsNeg == null ? new double[indexMax - indexMin] : xPointErrorsNeg, //
                yPointErrorsPos, yPointErrorsNeg, styles, pointSelected, indexMin, indexMax,
                MaxDataReducer2.DEFAULT_MAX_POINTS_COUNT);
    }

    private static void moveInPlace(final double[] xValues, final double[] yValues, final double[] xPointErrorsPos,
                                    final double[] xPointErrorsNeg, final double[] yPointErrorsPos, final double[] yPointErrorsNeg,
                                    final String[] styles, final boolean[] pointSelected, final int fromIndex, final int toIndex) {

        xValues[toIndex] = xValues[fromIndex];
        yValues[toIndex] = yValues[fromIndex];

//        xPointErrorsPos[toIndex] = xPointErrorsPos[fromIndex];
        xPointErrorsPos[toIndex] = fromIndex >= xPointErrorsPos.length ? xPointErrorsPos[xPointErrorsPos.length - 1] : xPointErrorsPos[fromIndex];
//        xPointErrorsNeg[toIndex] = xPointErrorsNeg[fromIndex];
        xPointErrorsNeg[toIndex] = fromIndex >= xPointErrorsNeg.length ? xPointErrorsNeg[xPointErrorsNeg.length - 1] : xPointErrorsNeg[fromIndex];
        yPointErrorsPos[toIndex] = yPointErrorsPos[fromIndex];
        yPointErrorsNeg[toIndex] = yPointErrorsNeg[fromIndex];

        styles[toIndex] = styles[fromIndex];
        pointSelected[toIndex] = pointSelected[fromIndex];
    }

    private static void shiftDataToFront(final boolean[] data, final int indexMin, final int indexMax) {
        final int size = indexMax - indexMin;
        System.arraycopy(data, indexMin, data, 0, size);
    }

    private static void shiftDataToFront(final double[] data, final int indexMin, final int indexMax) {
        final int size = indexMax - indexMin;
        System.arraycopy(data, indexMin, data, 0, size);
    }

    private static void shiftDataToFront(final String[] data, final int indexMin, final int indexMax) {
        final int size = indexMax - indexMin;
        System.arraycopy(data, indexMin, data, 0, size);
    }

    /*
     * old implementation
     *
     * @Override public List<XYChart.Data<Number, Number>> reduce(ChartData<Number,Number> data, Range<Double>
     * dataRange, int maxPointsCount) { if (data.size() <= maxPointsCount) { List<XYChart.Data<Number, Number>> list =
     * new ArrayList<>(); for (int i=0;i<data.size();i++) list.add(data.get(i)); return list; }
     * List<XYChart.Data<Number, Number>> reduced = new ArrayList<>(maxPointsCount); int n = 0;
     * reduced.add(data.get(n++)); double d = (double)data.size() / (double)maxPointsCount; int b = 1; int limit =
     * (int)Math.round(b*d); XYChart.Data<Number,Number> point = data.get(n++); double delta =
     * Math.abs(point.getYValue().doubleValue() - reduced.get(reduced.size()-1).getYValue().doubleValue()); while (n <
     * data.size()) { if (n >= limit) { reduced.add(point); point = data.get(n); b++; limit = (int)Math.round(b*d); }
     * else { XYChart.Data<Number,Number> p = data.get(n); double delta1 = Math.abs(p.getYValue().doubleValue() -
     * reduced.get(reduced.size()-1).getYValue().doubleValue()); if (delta1 > delta) { delta = delta1; point = p; } }
     * n++; } return reduced; }
     */
}

