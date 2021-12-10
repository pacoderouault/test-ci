package ngsdiaglim.controllers.analysisview.cnv;

import de.gsi.chart.XYChart;
import de.gsi.chart.XYChartCss;
import de.gsi.chart.axes.AxisLabelOverlapPolicy;
import de.gsi.chart.axes.AxisMode;
import de.gsi.chart.axes.spi.CategoryAxis;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.marker.DefaultMarker;
import de.gsi.chart.plugins.Screenshot;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.LineStyle;
import de.gsi.chart.renderer.Renderer;
import de.gsi.chart.renderer.datareduction.MaxDataReducer;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.chart.renderer.spi.LabelledMarkerRenderer;
import de.gsi.chart.renderer.spi.utils.DefaultRenderColorScheme;
import de.gsi.dataset.spi.DefaultErrorDataSet;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import ngsdiaglim.App;
import ngsdiaglim.XYRangeIndicator;
import ngsdiaglim.cnv.CNV;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.cnv.caller.CNVDetectionRobustZScore;
import ngsdiaglim.controllers.charts.ColorsList;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.stats.ZTest;
import ngsdiaglim.utils.MathUtils;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CNVChart extends XYChart {

    private final CovCopCNVData cnvData;
    private final CNVSample sample;
    private final CategoryAxis xAxis;
    private final DefaultNumericAxis yAxis;
    private final Zoomer zoomer = new Zoomer();
    private final Screenshot screenshot = new Screenshot();
    private final List<String> categories = new ArrayList<>();
    private final ErrorDataSetRenderer regionRenderer = new ErrorDataSetRenderer();
    private final LabelledMarkerRenderer contigRenderer = new LabelledMarkerRenderer();
    private final ErrorDataSetRenderer loessRenderer = new ErrorDataSetRenderer();
    private final ErrorDataSetRenderer geneAverageRenderer = new ErrorDataSetRenderer();
    private final ErrorDataSetRenderer cusumPosRenderer = new ErrorDataSetRenderer();
    private final ErrorDataSetRenderer cusumNegRenderer = new ErrorDataSetRenderer();
    private DefaultErrorDataSet regionDataSet;
    private DoubleDataSet contigDataSet;
    private DoubleDataSet geneAverageDataSet;
    private final List<DefaultErrorDataSet> loessDataSets = new ArrayList<>();
    private int visibleMarkersCount;
    private static final double maxYValue = 2.9;
    private final boolean autoMode = Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_AUTO_DETECTION));
    private final double delThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DEL_THRESHOLD));
    private final double dupThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DUP_THRESHOLD));
    private final static LoessInterpolator loessInterpolator = new LoessInterpolator(0.25, 4, 1);
    private final static double height = 200;
    public final static Color deletionColor = Color.valueOf("#ffdcaaff");
    public final static Color duplicationColor = Color.valueOf("#e08e8eff");
    public final static Color cusumPosColor = Color.valueOf("#87008731");
    public final static Color cusumNegColor = Color.valueOf("#0081ff37");

    private final SimpleBooleanProperty geneAverageVisible = new SimpleBooleanProperty();
    private final SimpleBooleanProperty loessVisible = new SimpleBooleanProperty();
    private final SimpleBooleanProperty cusumVisible = new SimpleBooleanProperty();

    private final static int zindexCusumNeg = 0;
    private final static int zindexCusumPos = 1;
    private final static int zindexRegion = 2;
    private final static int zindexContig = 3;
    private final static int zindexLoess = 4;
    private final static int zindexGeneAverage = 5;

    private final int sampleIdx;

    public CNVChart(CovCopCNVData cnvData, CNVSample sample) {
        super(new CategoryAxis(""), new DefaultNumericAxis(App.getBundle().getString("cnvnormalizedview.chart.yaxisname")));
        this.cnvData = cnvData;
        this.sample = sample;
        this.sampleIdx = cnvData.getSampleIndex(sample.getBarcode());
        this.xAxis = (CategoryAxis) getXAxis();
        this.yAxis = (DefaultNumericAxis) getYAxis();
        init();

    }

//    public XYChart getChart() {return chart;}

//    public CategoryAxis getxAxis() {return xAxis;}

//    public DefaultNumericAxis getyAxis() {return yAxis;}

    public ErrorDataSetRenderer getRegionRenderer() {return regionRenderer;}

    public DefaultErrorDataSet getRegionDataSet() {return regionDataSet;}

    public ErrorDataSetRenderer getLoessRenderer() {return loessRenderer;}

    public DoubleDataSet getContigDataSet() {return contigDataSet;}

    public DoubleDataSet getGeneAverageDataSet() {return geneAverageDataSet;}

    public List<DefaultErrorDataSet> getLoessDataSets() {return loessDataSets;}

    public boolean isGeneAverageVisible() {
        return geneAverageVisible.get();
    }

    public SimpleBooleanProperty geneAverageVisibleProperty() {
        return geneAverageVisible;
    }

    public void setGeneAverageVisible(boolean geneAverageVisible) {
        this.geneAverageVisible.set(geneAverageVisible);
    }

    public boolean isLoessVisible() {
        return loessVisible.get();
    }

    public SimpleBooleanProperty loessVisibleProperty() {
        return loessVisible;
    }

    public void setLoessVisible(boolean loessVisible) {
        this.loessVisible.set(loessVisible);
    }

    public boolean isCusumVisible() {
        return cusumVisible.get();
    }

    public SimpleBooleanProperty cusumVisibleProperty() {
        return cusumVisible;
    }

    public void setCusumVisible(boolean cusumVisible) {
        this.cusumVisible.set(cusumVisible);
    }

    private void init() {
        initChart();
        initYAxis();
        initXAxis();
        setZoomPlugin();
        setScreenshotPlugin();
        HBox.setHgrow(this, Priority.ALWAYS);

        geneAverageVisible.addListener((obs, oldV, newV) -> {
            if (newV) {
                addRenderer(geneAverageRenderer, zindexGeneAverage);
            } else {
                getRenderers().remove(geneAverageRenderer);
            }
        });

        loessVisible.addListener((obs, oldV, newV) -> {
            if (newV) {
                addRenderer(loessRenderer, zindexLoess);
            } else {
                getRenderers().remove(loessRenderer);
            }
        });
        cusumVisible.addListener((obs, oldV, newV) -> {
            if (newV) {
                addRenderer(cusumNegRenderer, zindexCusumNeg);
                addRenderer(cusumPosRenderer, zindexCusumPos);
            } else {
                getRenderers().removeAll(cusumNegRenderer, cusumPosRenderer);
            }
        });
    }


    private void initChart() {
        setLegendVisible(false);
        setHorizontalGridLinesVisible(true);
        setVerticalGridLinesVisible(false);
        setAnimated(false);
        getRenderers().clear();
        setMinHeight(height);
        setMaxHeight(height);
        getGridRenderer().getHorizontalMajorGrid().setStroke(Color.LIGHTGREY);
        getGridRenderer().getHorizontalMinorGrid().setVisible(true);
        // disable toolbar
        setTriggerDistance(0);
    }


    private void initXAxis() {
//        xAxis.setTickLabelRotation(90);
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        xAxis.setMaxMajorTickLabelCount(cnvData.getAllCovcopRegionsAsList().size() + 1);
        xAxis.setTickLabelsVisible(false);
        xAxis.setAutoRanging(true);
        xAxis.setCategories(categories);
    }


    private void initYAxis() {
        yAxis.set(-0.5, 3.5);
        yAxis.setAutoUnitScaling(false);
        yAxis.setAutoRanging(false);
        yAxis.setAutoRangeRounding(false);
        yAxis.setAutoGrowRanging(false);
        yAxis.setMinorTickCount(2);
        yAxis.setMaxMajorTickLabelCount(10);
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return String.valueOf(NumberUtils.round(number, 1));
            }
            @Override
            public Number fromString(String s) {
                return org.apache.commons.lang3.math.NumberUtils.createNumber(s);
            }
        });
        yAxis.setPadding(new Insets(0, 0, 0, 0));
    }


    private void setZoomPlugin() {
        zoomer.setAxisMode(AxisMode.X);
        zoomer.setSliderVisible(false);
        zoomer.setAddButtonsToToolBar(true);
        zoomer.setPannerEnabled(true);
        getPlugins().add(zoomer);
    }


    private void setScreenshotPlugin() {
        getPlugins().add(screenshot);
    }

    public void screenshotToFile(File fileName) {
        screenshot.setDirectory(fileName.getParent());
        screenshot.setPattern(fileName.getName());
        screenshot.screenshotToFile(false);
    }

    public void screenshotToClipboard() {
        screenshot.screenshotToClipboard();
    }

    private void initRegionRenderer() {
        regionRenderer.setPolyLineStyle(LineStyle.NONE);
        regionRenderer.setErrorType(ErrorStyle.NONE);
        regionRenderer.setMarker(DefaultMarker.CIRCLE);
        regionRenderer.setMarkerSize(1.5);
        regionRenderer.setRendererDataReducer(new MaxDataReducer2());
        xAxis.maxProperty().addListener((o, oldV, newV) -> setMarkerSize());
    }


    private void initLoessRenderer() {
        loessRenderer.setDrawMarker(false);
    }


    private void initGeneAverageRenderer() {
        geneAverageRenderer.setPolyLineStyle(LineStyle.NORMAL);
        geneAverageRenderer.setErrorType(ErrorStyle.ERRORSURFACE);
        geneAverageRenderer.setDrawMarker(false);
        geneAverageRenderer.setRendererDataReducer(new MaxDataReducer2());
    }


    private void initCusumRenderer() {
        DefaultRenderColorScheme.fillStylesProperty().clear();
        DefaultRenderColorScheme.fillStylesProperty().add(cusumPosColor);
        DefaultRenderColorScheme.fillStylesProperty().add(cusumNegColor);

        cusumPosRenderer.setPolyLineStyle(LineStyle.NONE);
        cusumPosRenderer.setErrorType(ErrorStyle.ERRORSURFACE);
        cusumPosRenderer.setDrawMarker(false);
        cusumNegRenderer.setPolyLineStyle(LineStyle.NONE);
        cusumNegRenderer.setErrorType(ErrorStyle.ERRORSURFACE);
        cusumNegRenderer.setDrawMarker(false);
        cusumNegRenderer.setRendererDataReducer(new MaxDataReducer2());
    }

    private void setMarkerSize() {
        int newVisibleMarkersCount = (int) (xAxis.maxProperty().get() - xAxis.minProperty().get());
        if (newVisibleMarkersCount != visibleMarkersCount) {
            visibleMarkersCount = newVisibleMarkersCount;
            double minSize = 1.5;
            double maxSize = 6;
            double maxMarkerCount = 500;
            double size = maxSize - newVisibleMarkersCount * maxSize / maxMarkerCount;
            if (size < minSize) {
                size = minSize;
            } else if (size > maxSize) {
                size = maxSize;
            }
            regionRenderer.setMarkerSize(size);
        }
    }


    public void drawChart() {

        initRegionRenderer();
        initLoessRenderer();
        initGeneAverageRenderer();
        initCusumRenderer();

        regionDataSet = new DefaultErrorDataSet("RegionDataSet");
        regionDataSet.setStyle("strokeColor=#b8b8b8; fillColor=#b8b8b8;");
        contigDataSet = new DoubleDataSet("ContigDataSet");
        geneAverageDataSet = new DoubleDataSet("ContigGeneAverageDataSet");
        geneAverageDataSet.setStyle("strokeColor=#5f708a;strokeWidth=4;");
        contigDataSet.setStyle("strokeColor=#696969;fillColor=#696969;" + XYChartCss.FONT_SIZE+ "=11;");
        DefaultErrorDataSet cusumPosDataSet = new DefaultErrorDataSet("CusumPosDataSet");
//        cusumPosDataSet.setStyle("fillColor=red");
        DefaultErrorDataSet cusumNegDataSet = new DefaultErrorDataSet("CusumNegDataSet");
//        cusumNegDataSet.setStyle("fillColor=blue");

        String contigLabel = "";
        String geneLabel = "";
        int startGene = 0;
        int geneNb = 0;
        int regionIdx = 0;
        double siHigh = 0.0;
        double siLow = 0.0;
        List<Double> regressionXValues = new ArrayList<>();
        List<Double> regressionYValues = new ArrayList<>();
        List<Double> geneValues = new ArrayList<>();

        for (CovCopRegion region : cnvData.getAllCovcopRegionsAsList()) {
            xAxis.getCategories().add(region.getName());
            Double value = region.getNormalized_values().get(sampleIdx);
            Double zScore = region.getzScores().get(sampleIdx);

            //////////// CONTIG //////////
            if (!region.getContig().equals(contigLabel)) {
                contigDataSet.add(regionIdx-0.5, 0, region.getContig());
                siHigh = 0.0;
                siLow = 0.0;
                cusumPosDataSet.add(regionIdx-0.5, 1, 0, 0);
                cusumNegDataSet.add(regionIdx-0.5, 1, 0, 0);
            }

            if (value != null) {
                if (value > maxYValue) {
                    value = maxYValue;
                } else if (value < 0) {
                    value = 0d;
                }

                regionDataSet.add(regionIdx, value, 0d, 0d);
                String label = "pool : "  + region.getPool() + "; " +
                        region.getContig() + ":" + region.getStart() + "-" + region.getEnd() + "\n" +
                        region.getName() + " (" + value + ")";
                regionDataSet.addDataLabel(regionIdx, label);
                setDataStyle(regionDataSet, regionIdx, value, zScore);

                //////////// CUSUM //////////
                boolean isOutlier = isOutliers(regionIdx);
                // High CUSUM
                double sHigh;
                if (isOutlier) {
                    sHigh = siHigh;
                }
                else {
                    sHigh = siHigh + value - sample.getMeanOfNormalValues() - sample.getStdOfNormalValues() * 0.5;
                }
                siHigh = Math.max(0.0, sHigh);
                double sucumValHigh = siHigh;
                if (sucumValHigh > maxYValue) {
                    sucumValHigh = maxYValue;
                }
                cusumPosDataSet.add(regionIdx, 1, 0, sucumValHigh);

                // LOW CUSUM
                double sLow;
                if (isOutlier) {
                    sLow = siLow;
                }
                else {
                    sLow = siLow + value - sample.getMeanOfNormalValues() + sample.getStdOfNormalValues() * 0.5;
                }
                siLow = Math.min(0.0, sLow);

                double sucumValLow = -siLow;
                if (sucumValLow < 0) {
                    sucumValLow = 0.0;
                }
                cusumNegDataSet.add(regionIdx, 1, sucumValLow, 0);
            } else {
                regionDataSet.add(regionIdx, Double.NaN, 0d, 0d);
            }




            //////////// GENE ////////////
            if (!region.getGene().equals(geneLabel) || regionIdx == cnvData.getAllCovcopRegionsAsList().size() - 1) {
                double endGene = regionIdx;
                if (regionIdx == cnvData.getAllCovcopRegionsAsList().size() - 1) {
                    endGene++;
                }
                final XYRangeIndicator xRange = new XYRangeIndicator(xAxis, startGene-0.5, endGene-0.5, 5, 10, geneLabel, ColorsList.getGeneColor(geneNb));
                getPlugins().add(xRange);

                ////////// MEAN OF GENE //////////
                if (regionIdx > 0) {
                    double geneMean = NumberUtils.round(MathUtils.meanOfDouble(geneValues), 2);
                    geneAverageDataSet.add(startGene-0.5, geneMean);
                    geneAverageDataSet.add(endGene-0.5, geneMean);

                    geneValues.clear();
                }

                geneLabel = region.getGene();
                startGene = regionIdx;
                geneNb++;
            }


            //////////// LOESS //////////
            if (!region.getContig().equals(contigLabel) || regionIdx == cnvData.getAllCovcopRegionsAsList().size() - 1) {
                double[] loessXvalues = new double[regressionYValues.size()];
                double[] loessYvalues = new double[regressionYValues.size()];
                try {
                    for (int i = 0; i < regressionYValues.size(); i++) {
                        loessXvalues[i] = regressionXValues.get(i);
                        loessYvalues[i] = regressionYValues.get(i);
                    }


                    double[] loessValues = loessInterpolator.smooth(loessXvalues, loessYvalues);
                    DefaultErrorDataSet loessDataSet = new DefaultErrorDataSet("loess");
                    loessDataSet.setStyle("strokeColor=#30713c");

                    for (int i = 0; i < loessValues.length; i++) {
                        loessDataSet.add(loessXvalues[i], loessValues[i], 0, 0);
                    }
                    loessRenderer.getDatasets().add(loessDataSet);
                    loessDataSets.add(loessDataSet);
                }
                catch (Exception ignored) {
                }

                regressionXValues.clear();
                regressionYValues.clear();
            }

            regressionXValues.add((double) regionIdx);
            regressionYValues.add(value);
            contigLabel = region.getContig();
            geneValues.add(value);
            regionIdx++;
        }

        drawCNVs();

        regionRenderer.getDatasets().add(regionDataSet);
        contigRenderer.getDatasets().add(contigDataSet);
        cusumNegRenderer.getDatasets().add(cusumNegDataSet);
        cusumPosRenderer.getDatasets().add(cusumPosDataSet);
        geneAverageRenderer.getDatasets().add(geneAverageDataSet);

        getRenderers().clear();
        if (isCusumVisible()) {
            getRenderers().addAll(cusumNegRenderer, cusumPosRenderer);
        }
        getRenderers().addAll(regionRenderer, contigRenderer);
        if (isLoessVisible()) {
            getRenderers().add (loessRenderer);
        }
        if (isGeneAverageVisible()) {
            getRenderers().add(geneAverageRenderer);
        }

        // my own DataPointTooltip to fix tooltip update bug
        getPlugins().add(new DataPointTooltip2());
    }


    private void addRenderer(Renderer renderer, int zIndex) {
        if (zIndex > getRenderers().size()) {
            getRenderers().add(renderer);
        } else {
            getRenderers().add(zIndex, renderer);
        }
    }


    private void drawCNVs() {
        for (CNV cnv : sample.getCNV()) {
            final CNVRangeIndicator xRange = new CNVRangeIndicator(
                    xAxis,
                    cnv.getFirstAmpliconIndex()-0.5,
                    cnv.getLastAmpliconIndex()-0.5,
                    cnv);
            getPlugins().add(0, xRange);
        }
        getCanvasForeground().toFront();
        pluginsArea.toFront();
        getCanvas().toFront();


        getCanvas().setMouseTransparent(true);

    }


    private void setDataStyle(DefaultErrorDataSet dataSet, int regionIdx, Double value, Double zScore) {
        if (autoMode) {
            if (zScore != null && zScore <= CNVDetectionRobustZScore.delThreshold) {
                dataSet.addDataStyle(regionIdx, "strokeColor=#f99e1c; fillColor=#f99e1c;");
            } else if (zScore != null && zScore >= CNVDetectionRobustZScore.dupThreshold) {
                dataSet.addDataStyle(regionIdx, "strokeColor=#e24545; fillColor=#e24545;");
            }
        } else {
            if (value <= delThreshold) {
                dataSet.addDataStyle(regionIdx, "strokeColor=#f99e1c; fillColor=#f99e1c;");
            } else if (value >= dupThreshold) {
                dataSet.addDataStyle(regionIdx, "strokeColor=#e24545; fillColor=#e24545;");
            }
        }
    }


    public boolean isOutliers(int regionIdx) {
        CovCopRegion curr = cnvData.getAllCovcopRegionsAsList().get(regionIdx);
        Double zScore = curr.getzScores().get(sampleIdx);
        Double value = curr.getNormalized_values().get(sampleIdx);

        if (value != null && (value > 1.5 || value < 0.5) && zScore != null && ZTest.zTest(zScore)) {
            // previous amp
            CovCopRegion prev = regionIdx > 0 ? cnvData.getAllCovcopRegionsAsList().get(regionIdx - 1) : null;
            if (prev != null) {
                Double prevzScore = prev.getzScores().get(sampleIdx);
                if (prevzScore != null && (zScore * prevzScore) >= 0 && ZTest.zTest(prevzScore)) {
                    return false;
                }
            }

            // next amp
            CovCopRegion next = regionIdx < cnvData.getAllCovcopRegionsAsList().size() - 1 ? cnvData.getAllCovcopRegionsAsList().get(regionIdx + 1) : null;
            if (next != null) {
                Double nextzScore = next.getzScores().get(sampleIdx);
                return nextzScore == null || !((zScore * nextzScore) >= 0) || !ZTest.zTest(nextzScore);
            }
            return true;
        }
        return false;
    }


    public void test() {
        DefaultRenderColorScheme.fillStylesProperty().clear();
        DefaultRenderColorScheme.fillStylesProperty().add(Color.ORANGE);
        DefaultRenderColorScheme.fillStylesProperty().add(Color.PURPLE);
        requestLayout();
    }


    @FXML
    public void resetZoom() {
        xAxis.set(-0.5, cnvData.getAllCovcopRegionsAsList().size() + 1);
        xAxis.forceRedraw();
    }
}
