package ngsdiaglim.cnv.caller;

import ngsdiaglim.App;
import ngsdiaglim.cnv.CNV;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.cnv.normalization.*;
import ngsdiaglim.cnv.parsers.AmpliconMatrixParser;
import ngsdiaglim.cnv.parsers.CaptureDepthParser;
import ngsdiaglim.enumerations.CNVControlType;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.FileFormatException;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.stats.ZTest;
import ngsdiaglim.utils.MathUtils;
import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CovCopCNVCaller {

    private final CovCopCNVData cnvData;
    private final ReadNumberNormalization readNumberNormalization;
//    private final LogTransformation logTransformation;
    private final GenderNormalization genderNormalization;
    private final GCNormalization gcNormalization;
    private AmpliconLengthNormalization ampliconLengthNormalization;
    private Map<String, Set<Integer>> excludedValuesIndex;

    private int minNumberOfAmplicons;
    private double delThreshold;
    private double dupThreshold;
    private boolean autoMode;

    public CovCopCNVCaller(CovCopCNVData cnvData) {
        this.cnvData = cnvData;
        readNumberNormalization = new ReadNumberNormalization(cnvData);
//        logTransformation = new LogTransformation(cnvData);
        genderNormalization = new GenderNormalization(cnvData);
        gcNormalization = new GCNormalization(cnvData);

    }


    public void call() throws Exception {
        loadParameters();
        initData(cnvData);
        normalizeData();
        callCNVs();
        if (!cnvData.getControlType().equals(CNVControlType.EXTERNAL)) {
            getExcludedValues();
            initData(cnvData);
            normalizeData();
            callCNVs();
        }
        for (String barcode : cnvData.getSamples().keySet()) {
            int sampleIdx = cnvData.getSampleIndex(barcode);
            CNVSample sample = cnvData.getSamples().get(barcode);
            Pair<Double, Double> pair = computeNormalValuesStats(sampleIdx);
            sample.setMeanOfNormalValues(pair.getFirst());
            sample.setStdOfNormalValues(pair.getSecond());
        }
    }


    private void loadParameters() {
        minNumberOfAmplicons = Integer.parseInt(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_MIN_AMPLICONS));
        delThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DEL_THRESHOLD));
        dupThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DUP_THRESHOLD));
        autoMode = Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_AUTO_DETECTION));
    }


    private void initData(CovCopCNVData cnvData) {
        cnvData.getCovcopRegions().values().forEach(amplicons -> amplicons.forEach(a -> {
            a.getNormalized_values().clear();
            a.getzScores().clear();
            for (Integer i : a.getRaw_values()) {
                if (i == null) {
                    a.addNormalizedValue(null);
                }
                else {
                    Double d = i.doubleValue();
                    a.addNormalizedValue(d);
                }
                a.addZScore(null);
            }
        }));
    }


    private void normalizeData() throws Exception {

        readNumberNormalization.normalize();
        genderNormalization.normalize();
        gcNormalization.normalize();

        if (cnvData.getAlgorithm().equals(TargetEnrichment.AMPLICON)) {
            ampliconLengthNormalization = new AmpliconLengthNormalization(cnvData);
            ampliconLengthNormalization.normalize();
        }

        if (cnvData.getControlType().equals(CNVControlType.EXTERNAL)) {
            CovCopCNVData controlData = loadControls();
            initData(controlData);
            final ReadNumberNormalization readNumberNormalization = new ReadNumberNormalization(controlData);
            final GenderNormalization genderNormalization = new GenderNormalization(controlData);
            final GCNormalization gcNormalization = new GCNormalization(controlData);

            readNumberNormalization.normalize();
            genderNormalization.normalize();
            gcNormalization.normalize();
            if (cnvData.getAlgorithm().equals(TargetEnrichment.AMPLICON)) {
                final AmpliconLengthNormalization ampliconLengthNormalization = new AmpliconLengthNormalization(controlData);
                ampliconLengthNormalization.normalize();
            }

            final ControlGroupNormalization controlGroupNormalization = new ControlGroupNormalization(cnvData, controlData);
            controlGroupNormalization.normalize();

        } else {
            final SamplesNormalization samplesNormalization = new SamplesNormalization(cnvData);
            if (excludedValuesIndex != null && !excludedValuesIndex.isEmpty()) {
                samplesNormalization.normalize(excludedValuesIndex);
            } else {
                samplesNormalization.normalize();
            }
        }
    }


    private CovCopCNVData loadControls() throws FileFormatException, SQLException, IOException {
        if (cnvData.getAlgorithm().equals(TargetEnrichment.AMPLICON)) {
            return AmpliconMatrixParser.getCNVSamples(cnvData.getControlGroup().getMatrix_file(), cnvData.getGenome(), cnvData.getPanel());
        } else if (cnvData.getAlgorithm().equals(TargetEnrichment.CAPTURE)) {
            return CaptureDepthParser.getCNVSample(cnvData.getGenome(), cnvData.getControlGroup(), cnvData.getWindowsSize());
        }
        return null;
    }


    public void callCNVs() {
        loadParameters();
        if (autoMode) {
            zScoreDetection();
        }
        else {
            manualDetection();
        }
    }

    private void zScoreDetection() {
        int minNumberOfAmplicons = Integer.parseInt(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_MIN_AMPLICONS));
        CNVDetectionRobustZScore cnvDetectionRobustZScore = new CNVDetectionRobustZScore.Builder(cnvData)
                .alpha(0.99)
                .mergeCNV(true)
                .minConsecutiveAmplicons(minNumberOfAmplicons)
                .build();
        cnvDetectionRobustZScore.callCNVs();
    }

    private void manualDetection() {

        CNVDetectionManual cnvDetectionManual = new CNVDetectionManual.Builder(cnvData)
                .delThreshold(delThreshold)
                .dupThreshold(dupThreshold)
                .minConsecutiveAmplicons(minNumberOfAmplicons)
                .build();
        cnvDetectionManual.callCNVs();
    }


    private Pair<Double, Double> computeNormalValuesStats(int sampleIdx) {
        List<Double> cusumTotalYVals = new ArrayList<>();

        for (CovCopRegion region : cnvData.getAllCovcopRegionsAsList()) {
            Double value = region.getNormalized_values().get(sampleIdx);
            Double zScore = region.getzScores().get(sampleIdx);
            if (value != null) {
//                cusumTotalYVals.add(value);
                if (autoMode) {
                    if (!ZTest.zTest(zScore)) {
                        cusumTotalYVals.add(value);
                    }
                } else {
                    if (value > delThreshold && value < dupThreshold) {
                        cusumTotalYVals.add(value);
                    }
                }
            }
        }
        return MathUtils.findDeviation(cusumTotalYVals);
    }


    /**
     * Get the index (amplicon name : [list of patients index] of all the values insides CNVs
     */
    private void getExcludedValues() {
        excludedValuesIndex = new HashMap<>();
        for (String samplename : cnvData.getSamples().keySet()) {
            CNVSample sample = cnvData.getSamples().get(samplename);
            int sampleIndex = cnvData.getSampleIndex(samplename);
            for (CNV cnv : sample.getCNV()) {
                for (CovCopRegion amp : cnv.getAmpliconsList()) {
                    excludedValuesIndex.putIfAbsent(amp.getName(), new HashSet<>());
                    excludedValuesIndex.get(amp.getName()).add(sampleIndex);
                }
            }
        }
    }
}
