package ngsdiaglim.cnv.normalization;

import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.enumerations.FitterMethod;
import ngsdiaglim.modeles.FastaSequenceGetter;
import ngsdiaglim.utils.DNAUtils;
import ngsdiaglim.utils.MathUtils;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GCNormalization extends CNVNormalization {

    private final LoessInterpolator loessInterpolator = new LoessInterpolator(0.30, 4, 10);
    private final PolynomialCurveFitter polynomialFitter = PolynomialCurveFitter.create(2);
    private FitterMethod fitterMethod = FitterMethod.POLYNOMIAL;
    private final Map<String, Double> gcContentMap = new HashMap<>();


    public GCNormalization(CovCopCNVData cnvData) {
        super(cnvData);
    }

    public GCNormalization(CovCopCNVData cnvData, FitterMethod fitterMethod) {
        super(cnvData);
        this.fitterMethod = fitterMethod;
    }

    public void normalize() throws Exception {
        getGCContent();

        for (int sampleIndex = 0; sampleIndex < cnvData.getSamples().size(); sampleIndex++) {

           // for each pool construct the loess regression
            for (String poolName : cnvData.getCovcopRegions().keySet()) {
                int finalSampleIndex = sampleIndex;
                Map<Double, List<Double>> gcContent = new HashMap<>();
                cnvData.getCovcopRegions(poolName).forEach(a -> {
                    if (a.getNormalized_values().get(finalSampleIndex) != null) {
                        gcContent.putIfAbsent(gcContentMap.get(a.getName()), new ArrayList<>());
                        gcContent.get(gcContentMap.get(a.getName())).add(a.getNormalized_values().get(finalSampleIndex));
                    }
                });

                double[] xVals = new double[gcContent.size()];
                double[] yVals = new double[gcContent.size()];

                List<Double> gcList = gcContent.keySet().stream().sorted().collect(Collectors.toList());
                for (int i = 0; i < gcList.size(); i++) {
                    Double gc = gcList.get(i);
                    List<Double> depth = gcContent.get(gc);
                    depth.sort(Double::compareTo);
                    Double medianDepth = MathUtils.median(depth);
                    xVals[i] = gc;
                    yVals[i] = medianDepth == null ? 0 : medianDepth;
                }

                if (fitterMethod.equals(FitterMethod.LOESS)) {
                    PolynomialSplineFunction loess = loessInterpolator.interpolate(xVals, yVals);
                    cnvData.getCovcopRegions(poolName).forEach(a -> {
                        if (a.getNormalized_values().get(finalSampleIndex) != null) {
                            double fittedValue = loess.value(gcContentMap.get(a.getName()));
                            double normalizedValue = valueCorrection(a.getNormalized_values().get(finalSampleIndex), fittedValue);
                            a.addNormalizedValue(normalizedValue, finalSampleIndex);
                        }
                    });
                }
                else {
                    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
                    WeightedObservedPoints obs = new WeightedObservedPoints();
                    for (int i = 0; i < gcList.size(); i++) {
                        obs.add(xVals[i], yVals[i]);
                    }
                    try {
                        PolynomialFunction func = new PolynomialFunction(fitter.fit(obs.toList()));

                        cnvData.getCovcopRegions(poolName).forEach(a -> {
                            if (a.getNormalized_values().get(finalSampleIndex) != null) {
                                double fittedValue = func.value(gcContentMap.get(a.getName()));
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

    private void getGCContent() throws Exception {
        FastaSequenceGetter fastaSequenceGetter = new FastaSequenceGetter(new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.REFERENCE_HG19.name())));
        for (ObservableList<CovCopRegion> amplicons : cnvData.getCovcopRegions().values()) {
            for (CovCopRegion a : amplicons) {
                String seq = fastaSequenceGetter.getSequence(a.getContig(), a.getStart(), a.getEnd());
                if (seq != null) {
                    double gc = NumberUtils.round(DNAUtils.getGCContent(seq), 2);
                    gcContentMap.put(a.getName(), gc);
                }
                else {
                    throw new IOException("Impossible to reach the reference fasta file.");
                }
            }
        }
    }
}
