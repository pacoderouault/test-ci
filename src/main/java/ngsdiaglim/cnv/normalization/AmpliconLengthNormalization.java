package ngsdiaglim.cnv.normalization;

import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.enumerations.FitterMethod;
import ngsdiaglim.utils.MathUtils;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AmpliconLengthNormalization extends CNVNormalization {

    private final LoessInterpolator loessInterpolator = new LoessInterpolator(0.30, 4, 10);
//    private final PolynomialCurveFitter polynomialFitter = PolynomialCurveFitter.create(2);
    private FitterMethod fitterMethod = FitterMethod.POLYNOMIAL;

    public AmpliconLengthNormalization(CovCopCNVData cnvData) {
        super(cnvData);
    }

    public AmpliconLengthNormalization(CovCopCNVData cnvData, FitterMethod fitterMethod) {
        super(cnvData);
        this.fitterMethod = fitterMethod;
    }

    public void normalize() {
        for (int sampleIndex = 0; sampleIndex < cnvData.getSamples().size(); sampleIndex++) {

            // for each pool construct the loess regression
            for (String poolName : cnvData.getCovcopRegions().keySet()) {
                int finalSampleIndex = sampleIndex;
                Map<Integer, List<Double>> length = new HashMap<>();
                cnvData.getCovcopRegions(poolName).forEach(a -> {
                    if (a.getNormalized_values().get(finalSampleIndex) != null) {
                        length.putIfAbsent(a.getSize(), new ArrayList<>());
                        length.get(a.getSize()).add(a.getNormalized_values().get(finalSampleIndex));
                    }
                });

                double[] xVals = new double[length.size()];
                double[] yVals = new double[length.size()];

                List<Integer> lengthList = length.keySet().stream().sorted().collect(Collectors.toList());
                for (int i = 0; i < lengthList.size(); i++) {
                    Integer len = lengthList.get(i);
                    List<Double> depth = length.get(len);
                    depth.sort(Double::compareTo);
                    Double medianDepth = MathUtils.median(depth);
                    if (medianDepth != null) {
                        xVals[i] = len;
                        yVals[i] = medianDepth;
                    }
                }

                if (fitterMethod.equals(FitterMethod.LOESS)) {
                    try {
                        PolynomialSplineFunction loess = loessInterpolator.interpolate(xVals, yVals);
                        cnvData.getCovcopRegions(poolName).forEach(a -> {
                            if (a.getNormalized_values().get(finalSampleIndex) != null) {
                                double fittedValue = loess.value(a.getSize());
                                double normalizedValue = valueCorrection(a.getNormalized_values().get(finalSampleIndex), fittedValue);
                                a.addNormalizedValue(normalizedValue, finalSampleIndex);
                            }
                        });
                    }
                    catch (NoDataException nde) {
                        cnvData.getCovcopRegions(poolName).forEach(a -> {
                            if (a.getNormalized_values().get(finalSampleIndex) != null) {
                                a.addNormalizedValue(null, finalSampleIndex);
                            }
                        });
                    }
                }
                else {
                    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
                    WeightedObservedPoints obs = new WeightedObservedPoints();
                    for (int i = 0; i < lengthList.size(); i++) {
                        obs.add(xVals[i], yVals[i]);
                    }
                    try {
                        PolynomialFunction func = new PolynomialFunction(fitter.fit(obs.toList()));
                        cnvData.getCovcopRegions(poolName).forEach(a -> {
                            if (a.getNormalized_values().get(finalSampleIndex) != null) {
                                double fittedValue = func.value(a.getSize());
                                double normalizedValue = valueCorrection(a.getNormalized_values().get(finalSampleIndex), fittedValue);
                                a.addNormalizedValue(normalizedValue, finalSampleIndex);
                            }
                        });
                    }
                    catch (NotStrictlyPositiveException e){
                        cnvData.getCovcopRegions(poolName).forEach(a -> {
                            if (a.getNormalized_values().get(finalSampleIndex) != null) {
                                a.addNormalizedValue(null, finalSampleIndex);
                            }
                        });
                    }
                }
            }
        }
    }


    protected Double valueCorrection(Double value, Double fitter) {
        return value / fitter * 100000;
    }
}
