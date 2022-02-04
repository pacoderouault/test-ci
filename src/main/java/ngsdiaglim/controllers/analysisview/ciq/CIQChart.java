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
import de.gsi.chart.ui.geometry.Side;
import de.gsi.dataset.spi.DefaultErrorDataSet;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import ngsdiaglim.App;
import ngsdiaglim.controllers.analysisview.cnv.MaxDataReducer2;
import ngsdiaglim.controllers.analysisview.cnv.ScreenShotPlugin;
import ngsdiaglim.modeles.ciq.CIQVariantDataSet;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.DateFormatterUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CIQChart extends XYChart {

    private final SimpleObjectProperty<CIQVariantDataSet> dataset = new SimpleObjectProperty<>();
    private final CategoryAxis xAxis;
    private final DefaultNumericAxis yAxis;
    private final List<String> categories = new ArrayList<>();
    private final ErrorDataSetRenderer recordRenderer = new ErrorDataSetRenderer();
    private DefaultErrorDataSet recordDataSet;
    private final Zoomer2 zoomer = new Zoomer2();
    private TargetVAFIndicator targetVafIndicator;
    private MeanIndicator meanTargetIndicator;
    private SDLowIndicator lowSDIndicatorMin;
    private SDLowIndicator lowSDIndicatorMax;
    private SDHighIndicator highSDIndicatorMin;
    private SDHighIndicator highSDIndicatorMax;
    private XRangeIndicator yRange1;
    private final DataPointTooltip3 dataPointTooltip = new DataPointTooltip3();
    private final ScreenShotPlugin screenshot = new ScreenShotPlugin();

    public CIQChart(CIQVariantDataSet dataset) {

        super(new CategoryAxis(App.getBundle().getString("ciqchart.lb.axis.x")), new DefaultNumericAxis(App.getBundle().getString("ciqchart.lb.axis.y")));

        this.xAxis = (CategoryAxis) getXAxis();
        this.yAxis = (DefaultNumericAxis) getYAxis();

        init();
        this.dataset.set(dataset);
        drawChart();

//        dataset.addListener((obs, oldV, newV) -> {
//            if (newV != null) {
//                drawChart();
//            } else {
//                clearChart();
//            }
//        });
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


        initRecordRenderer();

        yRange1 = new XRangeIndicator(xAxis, 0, 0,"");

        recordDataSet = new DefaultErrorDataSet("RegionDataSet");
        recordRenderer.getDatasets().add(recordDataSet);

        targetVafIndicator = new TargetVAFIndicator(yAxis, 0, "");
        meanTargetIndicator = new MeanIndicator(yAxis, 0, "Moyenne");
        lowSDIndicatorMin = new SDLowIndicator(yAxis, 0, "Moy - 2 SD");
        lowSDIndicatorMax = new SDLowIndicator(yAxis, 0, "Moy + 2 SD");
        highSDIndicatorMin = new SDHighIndicator(yAxis, 0, "Moy - 3 SD");
        highSDIndicatorMax = new SDHighIndicator(yAxis, 0, "Moy + 3 SD");
        getRenderers().addAll(recordRenderer);
        getPlugins().addAll(screenshot, targetVafIndicator, meanTargetIndicator, lowSDIndicatorMin, lowSDIndicatorMax, highSDIndicatorMin, highSDIndicatorMax, dataPointTooltip);

        setZoomPlugin();
    }

    private void initYAxis() {
        yAxis.setAutoRanging(false);
//        yAxis.setAutoRangeRounding(false);
//        yAxis.setAutoGrowRanging(false);
        yAxis.setUnit(null);
    }

    private void initXAxis() {
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SHIFT_ALT);
//        xAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        xAxis.setCategories(categories);
        xAxis.setUnit(null);
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public Number fromString(String string) {
                for (int i = 0; i < xAxis.getCategories().size(); i++) {
                    if (xAxis.getCategories().get(i).equalsIgnoreCase(string)) {
                        return i;
                    }
                }
                throw new IllegalArgumentException("Category not found.");
            }

            @Override
            public String toString(Number object) {
                final int index = Math.round(object.floatValue());
                if (index < 0 || index >= xAxis.getCategories().size()) {
                    return "";
                }
                return xAxis.getCategories().get(index);
            }
        });

        xAxis.maxMajorTickLabelCountProperty().bind(xAxis.maxProperty().subtract(xAxis.minProperty()));
    }

    private void updateYAxis() {
        yAxis.set(
                Math.max(0, dataset.get().getMean() - 4 * dataset.get().getSd()),
                dataset.get().getMean() + 4 * dataset.get().getSd()
        );
    }

    private void updateXAxis() {
//        xAxis.setMaxMajorTickLabelCount(Math.mdataset.get().getCiqRecords().size() - 1);
//        xAxis.tick
//        // show only the 30 last records
        if (dataset.get().getCiqRecords().size() > 30) {
            xAxis.set(dataset.get().getCiqRecords().size() - 30.5, dataset.get().getCiqRecords().size() - 0.5);
        } else {
            xAxis.set(-0.5, dataset.get().getCiqRecords().size() - 0.5);
        }
        xAxis.getCategories().clear();
    }


    private void updateSDMarkers() {
        Object[] lowMinValue = {NumberUtils.round(dataset.get().getLowMinValue(), 3)};
        Object[] lowMaxValue = {NumberUtils.round(dataset.get().getLowMaxValue(), 3)};
        Object[] highMinValue = {NumberUtils.round(dataset.get().getHighMinValue(), 3)};
        Object[] highMaxValue = {NumberUtils.round(dataset.get().getHighMaxValue(), 3)};
        Object[] mean = {NumberUtils.round(dataset.get().getMean(), 3)};
        lowSDIndicatorMin.setText(BundleFormatter.format("ciq.chart.lb.lowSDmin", lowMinValue));
        lowSDIndicatorMax.setText(BundleFormatter.format("ciq.chart.lb.lowSDmax", lowMaxValue));
        highSDIndicatorMin.setText(BundleFormatter.format("ciq.chart.lb.highSDmin", highMinValue));
        highSDIndicatorMax.setText(BundleFormatter.format("ciq.chart.lb.highSDmax", highMaxValue));
        meanTargetIndicator.setText(BundleFormatter.format("ciq.chart.lb.mean", mean));
    }

    private void setZoomPlugin() {
        zoomer.setAxisMode(AxisMode.XY);
        zoomer.setSliderVisible(false);
        zoomer.setAddButtonsToToolBar(false);
        zoomer.setPannerEnabled(true);
        getPlugins().add(zoomer);
    }

    public void drawChart() {

//        zoomer.clear();

        updateXAxis();
        updateYAxis();

        updateSDMarkers();

        recordDataSet.clearData();
//        targetVafDataSet.clearData();
//        recordDataSet.setStyle("strokeColor=#4f4f4f;fillColor=#4f4f4f;");

        setTitle(dataset.get().getCiqHotspot().getName());
//        System.out.println(dataset.get().getCiqHotspot().getName());
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

        targetVafIndicator.setValue(dataset.get().getCiqHotspot().getVafTarget());
        meanTargetIndicator.setValue(dataset.get().getMean());
        lowSDIndicatorMin.setValue(dataset.get().getMean() - 2 * dataset.get().getSd());
        lowSDIndicatorMax.setValue(dataset.get().getMean() + 2 * dataset.get().getSd());
        highSDIndicatorMin.setValue(dataset.get().getMean() - 3 * dataset.get().getSd());
        highSDIndicatorMax.setValue(dataset.get().getMean() + 3 * dataset.get().getSd());

//        redrawCanvas();

    }

    private void initRecordRenderer() {
        recordRenderer.setPolyLineStyle(LineStyle.NONE);
        recordRenderer.setErrorType(ErrorStyle.NONE);
        recordRenderer.setMarker(DefaultMarker.CIRCLE);
        recordRenderer.setMarkerSize(5);
        recordRenderer.setRendererDataReducer(new MaxDataReducer2());

    }

    private void setDataStyle(DefaultErrorDataSet dataSet, int regionIdx, CIQVariantRecord record) {
        if (record.isDefined()) {
            dataSet.addDataStyle(regionIdx, "strokeColor=#a2a2a2;");
        }
        else if (!record.isAccepted()) {
            dataSet.addDataStyle(regionIdx, "strokeColor=red;");
        } else {
            if (dataset.get().isInside2SD(record.getVaf())) {
                dataSet.addDataStyle(regionIdx, "strokeColor=#4f4f4f;");
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
//        recordDataSet.clearData();
//        targetVafIndicator.setValue(0);
//        meanTargetIndicator.setValue(0);
//        lowSDIndicatorMin.setValue(0);
//        lowSDIndicatorMax.setValue(0);
//        highSDIndicatorMin.setValue(0);
//        highSDIndicatorMax.setValue(0);
//        setTitle(null);
    }

    public void screenshotToFile(File file) {
        screenshot.setDirectory(file.getParent());
        screenshot.setPattern(file.getName());
        screenshot.screenshotToFile(false);
    }

    public void screenshotToClipboard() {
        screenshot.screenshotToClipboard();
    }
}
