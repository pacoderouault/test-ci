package ngsdiaglim.controllers.analysisview.ciq;

import de.gsi.chart.XYChart;
import de.gsi.chart.XYChartCss;
import de.gsi.chart.axes.AxisLabelOverlapPolicy;
import de.gsi.chart.axes.AxisMode;
import de.gsi.chart.axes.spi.CategoryAxis;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.marker.DefaultMarker;
import de.gsi.chart.plugins.*;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.LineStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.chart.renderer.spi.LabelledMarkerRenderer;
import de.gsi.dataset.spi.DefaultErrorDataSet;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import ngsdiaglim.App;
import ngsdiaglim.cnv.caller.CNVDetectionRobustZScore;
import ngsdiaglim.controllers.analysisview.cnv.DataPointTooltip2;
import ngsdiaglim.controllers.analysisview.cnv.MaxDataReducer2;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.modeles.ciq.CIQVariantDataSet;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.DateFormatterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CIQChart extends XYChart {

    private final SimpleObjectProperty<CIQVariantDataSet> dataset = new SimpleObjectProperty<>();
    private final CategoryAxis xAxis;
    private final DefaultNumericAxis yAxis;
    private final List<String> categories = new ArrayList<>();
    private final ErrorDataSetRenderer recordRenderer = new ErrorDataSetRenderer();
    private DefaultErrorDataSet recordDataSet;
    private DoubleDataSet targetVafDataSet;
    private final Zoomer zoomer = new Zoomer();
    private final LabelledMarkerRenderer targetVafRenderer = new LabelledMarkerRenderer();
    private MeanIndicator meanTargetIndicator;
    private SDLowIndicator lowSDIndicatorMin;
    private SDLowIndicator lowSDIndicatorMax;
    private SDHighIndicator highSDIndicatorMin;
    private SDHighIndicator highSDIndicatorMax;
    private XRangeIndicator yRange1;
    private DataPointTooltip3 dataPointTooltip = new DataPointTooltip3();
    public CIQChart() {

        super(new CategoryAxis(App.getBundle().getString("ciqchart.lb.axis.x")), new DefaultNumericAxis(App.getBundle().getString("ciqchart.lb.axis.y")));

        this.xAxis = (CategoryAxis) getXAxis();
        this.yAxis = (DefaultNumericAxis) getYAxis();

        init();

        dataset.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                drawChart();
            } else {
                clearChart();
            }
        });
    }

    public CIQVariantDataSet getDataset() {
        return dataset.get();
    }

    public SimpleObjectProperty<CIQVariantDataSet> datasetProperty() {
        return dataset;
    }

    public void setDataset(CIQVariantDataSet dataset) {
        this.dataset.set(dataset);
    }

    private void init() {
        setPrefHeight(300);
        setLegendVisible(false);
        setHorizontalGridLinesVisible(false);
        setVerticalGridLinesVisible(false);
        setAnimated(false);

        // disable toolbar
        setTriggerDistance(0);

        initXAxis();
        initYAxis();
        setZoomPlugin();

        initRecordRenderer();

        yRange1 = new XRangeIndicator(xAxis, 0, 0,"");

        recordDataSet = new DefaultErrorDataSet("RegionDataSet");
        recordRenderer.getDatasets().add(recordDataSet);

        targetVafDataSet = new DoubleDataSet("ContigDataSet");
        targetVafDataSet.setStyle("strokeColor=#000;fillColor=#000;" + XYChartCss.FONT_SIZE+ "=11;");
        targetVafRenderer.getDatasets().add(targetVafDataSet);
        targetVafRenderer.enableHorizontalMarker(true);
        targetVafRenderer.enableVerticalMarker(false);

        meanTargetIndicator = new MeanIndicator(yAxis, 0, "Moyenne");
        lowSDIndicatorMin = new SDLowIndicator(yAxis, 0, "Moy - 2 SD");
        lowSDIndicatorMax = new SDLowIndicator(yAxis, 0, "Moy + 2 SD");
        highSDIndicatorMin = new SDHighIndicator(yAxis, 0, "Moy - 3 SD");
        highSDIndicatorMax = new SDHighIndicator(yAxis, 0, "Moy + 3 SD");


        getRenderers().addAll(targetVafRenderer, recordRenderer);
        getPlugins().addAll(meanTargetIndicator, lowSDIndicatorMin, lowSDIndicatorMax, highSDIndicatorMin, highSDIndicatorMax, dataPointTooltip);
    }

    private void initYAxis() {
        yAxis.setAutoRanging(false);
        yAxis.setAutoRangeRounding(false);
        yAxis.setAutoGrowRanging(false);
        yAxis.setUnit(null);
    }

    private void initXAxis() {
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        xAxis.setCategories(categories);
        xAxis.setUnit(null);
    }

    private void updateYAxis() {
        yAxis.set(
                Math.max(0, dataset.get().getCiqHotspot().getVafTarget() - 4 * dataset.get().getSd()),
                dataset.get().getCiqHotspot().getVafTarget() + 4 * dataset.get().getSd()
        );
    }

    private void updateXAxis() {
        xAxis.setMaxMajorTickLabelCount(dataset.get().getCiqRecords().size() - 1);
        xAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
//        // show only the 30 last records
        if (dataset.get().getCiqRecords().size() > 30) {
            xAxis.set(dataset.get().getCiqRecords().size() - 30.5, dataset.get().getCiqRecords().size() - 0.5);
        } else {
            xAxis.set(-0.5, dataset.get().getCiqRecords().size() - 0.5);
        }
        xAxis.getCategories().clear();
    }

    private void setZoomPlugin() {
//        zoomer.setAxisMode(AxisMode.XY);
        zoomer.setSliderVisible(false);
        zoomer.setAddButtonsToToolBar(true);
        zoomer.setPannerEnabled(true);
        getPlugins().add(zoomer);
    }

    public void drawChart() {

        zoomer.clear();

        updateXAxis();
        updateYAxis();

        recordDataSet.clearData();
        targetVafDataSet.clearData();
//        recordDataSet.setStyle("strokeColor=#4f4f4f;fillColor=#4f4f4f;");



        int recordIndex = 0;
        for (CIQVariantRecord record : dataset.get().getCiqRecords()) {
            xAxis.getCategories().add(record.getAnalysis().getName() + "_" + recordIndex);
            recordDataSet.add(recordIndex, record.getVaf(), 0d, 0d);
            String label = record.getAnalysis().getName() + "\n"
                    + DateFormatterUtils.formatLocalDate(record.getAnalysis().getRun().getDate())
                    + "\nVAF : " + record.getVafRounded();
            if (record.getLastHistory() != null) {
                label += "\n" + record.getLastHistory().getNewState().getText();
            }
            recordDataSet.addDataLabel(recordIndex, label);
            setDataStyle(recordDataSet, recordIndex, record);
            recordIndex++;
        }

        targetVafDataSet.add(0, dataset.get().getCiqHotspot().getVafTarget(), "VAF ciblee");

        meanTargetIndicator.setValue(dataset.get().getMean());
        lowSDIndicatorMin.setValue(dataset.get().getMean() - 2 * dataset.get().getSd());
        lowSDIndicatorMax.setValue(dataset.get().getMean() + 2 * dataset.get().getSd());
        highSDIndicatorMin.setValue(dataset.get().getMean() - 3 * dataset.get().getSd());
        highSDIndicatorMax.setValue(dataset.get().getMean() + 3 * dataset.get().getSd());

//        final TargetVAFIndicator vafTargetIndicator = new TargetVAFIndicator(yAxis, ciqHotspot.getVafTarget(), "VAF ciblee");
//        getPlugins().add(vafTargetIndicator);

//        final MeanIndicator meanTargetIndicator = new MeanIndicator(yAxis, dataset.getMean(), "Moyenne");
//        getPlugins().add(meanTargetIndicator);
//
//        final SDLowIndicator lowSDIndicatorMin = new SDLowIndicator(yAxis, dataset.getMean() - 2 * dataset.getSd(), "Moy - 2 SD");
//        final SDLowIndicator lowSDIndicatorMax = new SDLowIndicator(yAxis, dataset.getMean() + 2 * dataset.getSd(), "Moy + 2 SD");
//        getPlugins().addAll(lowSDIndicatorMin, lowSDIndicatorMax);
//
//        final SDHighIndicator highSDIndicatorMin = new SDHighIndicator(yAxis, dataset.getMean() - 3 * dataset.getSd(), "Moy - 3 SD");
//        final SDHighIndicator highSDIndicatorMax = new SDHighIndicator(yAxis, dataset.getMean() + 3 * dataset.getSd(), "Moy + 3 SD");
//        getPlugins().addAll(highSDIndicatorMin, highSDIndicatorMax);


//        LabelledMarkerRenderer targetVafRenderer = new LabelledMarkerRenderer();
//        targetVafRenderer.enableHorizontalMarker(true);
//        targetVafRenderer.enableVerticalMarker(false);
//        targetVafDataSet.add(2, ciqHotspot.getVafTarget(), "VAF ciblee");
//        targetVafRenderer.getDatasets().add(targetVafDataSet);

//        final YWatchValueIndicator vafTargetIndicator = new YWatchValueIndicator(yAxis, dataset.getCiqHotspot().getVafTarget());
//        vafTargetIndicator.setPreventOcclusion(true);
//        final YWatchValueIndicator meanIndicator = new YWatchValueIndicator(yAxis, dataset.getMean());
//        meanIndicator.setPreventOcclusion(true);
//        meanIndicator.setId("valA");

//        final YWatchValueIndicator lowSdMinIndicator = new YWatchValueIndicator(yAxis, dataset.getMean() - 2 * dataset.getSd());
//        final YWatchValueIndicator lowSdMaxIndicator = new YWatchValueIndicator(yAxis, dataset.getMean() + 2 * dataset.getSd());
//        final YWatchValueIndicator highSdMinIndicator = new YWatchValueIndicator(yAxis, dataset.getMean() - 3 * dataset.getSd());
//        final YWatchValueIndicator highSdMaxIndicator = new YWatchValueIndicator(yAxis, dataset.getMean() + 3 * dataset.getSd());
//        getPlugins().addAll(vafTargetIndicator, meanIndicator, lowSdMinIndicator, lowSdMaxIndicator, highSdMinIndicator, highSdMaxIndicator);


    }

    private void initRecordRenderer() {
        recordRenderer.setPolyLineStyle(LineStyle.NONE);
        recordRenderer.setErrorType(ErrorStyle.NONE);
        recordRenderer.setMarker(DefaultMarker.CIRCLE);
        recordRenderer.setMarkerSize(5);
        recordRenderer.setRendererDataReducer(new MaxDataReducer2());

    }

    private void setDataStyle(DefaultErrorDataSet dataSet, int regionIdx, CIQVariantRecord record) {
        if (!record.isAccepted()) {
            dataSet.addDataStyle(regionIdx, "strokeColor=red;");
        } else {
            if (dataset.get().isInside2SD(record.getVaf())) {
                dataSet.addDataStyle(regionIdx, "strokeColor=green;");
            } else if (dataset.get().isInside3SD(record.getVaf())) {
                dataSet.addDataStyle(regionIdx, "strokeColor=orange;");
            } else {
                dataSet.addDataStyle(regionIdx, "strokeColor=red;");
            }
        }
    }

    public void highLightDatapoint(CIQVariantRecord record) {
        int idx = dataset.get().getCiqRecords().indexOf(record);
        if (idx >= 0) {
            yRange1.setLowerBound(idx - 0.5);
            yRange1.setUpperBound(idx + 0.5);
            if (!getPlugins().contains(yRange1)) {
                getPlugins().add(0, yRange1);
            }
        } else {
            getPlugins().remove(yRange1);
        }
    }

    public void unhighlight() {
        getPlugins().remove(yRange1);
    }

    private void clearChart() {
        recordDataSet.clearData();
        targetVafDataSet.clearData();
        meanTargetIndicator.setValue(0);
        lowSDIndicatorMin.setValue(0);
        lowSDIndicatorMax.setValue(0);
        highSDIndicatorMin.setValue(0);
        highSDIndicatorMax.setValue(0);
    }
}
