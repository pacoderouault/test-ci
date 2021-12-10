package ngsdiaglim;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.List;

import de.gsi.chart.Chart;
import de.gsi.chart.XYChartCss;
import de.gsi.chart.axes.AxisMode;
import de.gsi.chart.marker.DefaultMarker;
import de.gsi.chart.plugins.*;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.spi.LabelledMarkerRenderer;
//import de.gsi.dataset.spi.DoubleDataSet;
import de.gsi.dataset.DataSet;
import de.gsi.dataset.spi.DoubleDataSet;
import de.gsi.dataset.spi.DoubleErrorDataSet;
import de.gsi.dataset.spi.FragmentedDataSet;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import de.gsi.chart.XYChart;
import de.gsi.chart.axes.AxisLabelOverlapPolicy;
import de.gsi.chart.axes.spi.CategoryAxis;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.renderer.LineStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.DefaultErrorDataSet;
import javafx.util.StringConverter;
import ngsdiaglim.controllers.analysisview.cnv.DataPointTooltip2;
import ngsdiaglim.utils.NumberUtils;


/**
 * @author rstein
 */
public class CategoryAxisSample extends Application {
    private static final int N_SAMPLES = 4000;

    @Override
    public void start(final Stage primaryStage) {
        CSSFX.start();
        final VBox root = new VBox();

        XYChart chat1 = getChart();

        root.getChildren().add(chat1);

        final Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
        primaryStage.setTitle(this.getClass().getSimpleName());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(evt -> Platform.exit());
        primaryStage.show();

//        final AxisSynchronizer2 sync2 = new AxisSynchronizer2();
//        sync2.add((CategoryAxis) chat1.getXAxis());
//        sync2.add((CategoryAxis) chat2.getXAxis());

    }


    private XYChart getChart() {
        final CategoryAxis xAxis = new CategoryAxis("months");
        xAxis.setTickLabelRotation(90);
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        xAxis.setMaxMajorTickLabelCount(N_SAMPLES + 1);
        xAxis.setTickLabelsVisible(false);
        xAxis.setAutoRanging(false);

        final DefaultNumericAxis yAxis = new DefaultNumericAxis("yAxis");

        yAxis.set(-0.9, 3);
        yAxis.setAutoUnitScaling(false);
        yAxis.setAutoRanging(false);
        yAxis.setAutoRangeRounding(false);
        yAxis.setAutoGrowRanging(false);
        yAxis.setMinorTickCount(0);
        yAxis.setMaxMajorTickLabelCount(7);
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
        yAxis.setPadding(new Insets(0, 0, 20, 0));
        final XYChart lineChartPlot = new XYChart(xAxis, yAxis);
//        lineChartPlot.getCanvas().widthProperty().addListener(((observableValue, number, t1) -> System.out.println(t1)));
        // set them false to make the plot faster
        lineChartPlot.setHorizontalGridLinesVisible(true);

        lineChartPlot.setVerticalGridLinesVisible(false);
        lineChartPlot.setAnimated(false);
        lineChartPlot.getRenderers().clear();
//         lineChartPlot.getRenderers().add(new ReducingLineRenderer());
        final ErrorDataSetRenderer renderer = new ErrorDataSetRenderer();
        renderer.setPolyLineStyle(LineStyle.NONE);
        renderer.setErrorType(ErrorStyle.NONE);
        renderer.setMarkerSize(1.5);




//        Platform.runLater(() -> {
//            final int[] visibleMarkersCount = {N_SAMPLES};
//            xAxis.maxProperty().addListener((o, oldV, newV) -> {
//                int newVisibleMarkersCount = (int) (xAxis.maxProperty().get() - xAxis.minProperty().get());
////                System.out.println(newVisibleMarkersCount);
////                if (newVisibleMarkersCount != visibleMarkersCount[0]) {
////                    visibleMarkersCount[0] = newVisibleMarkersCount;
////
////                    double minSize = 1.5;
////                    double maxSize = 8;
////                    double minMarkerCount = 80;
////                    double maxMarkerCount = 500;
////                    if (newVisibleMarkersCount < minMarkerCount) {
////                        renderer.setMarkerSize(maxSize);
////                    } else if (newVisibleMarkersCount > maxMarkerCount) {
////                        renderer.setMarkerSize(minSize);
////                    } else {
////                        double size = ((maxMarkerCount - minMarkerCount) - newVisibleMarkersCount) * (maxSize - minSize) / (maxMarkerCount - minMarkerCount) + minSize;
////                        renderer.setMarkerSize(size);
////                    }
////                }
//                if (newVisibleMarkersCount != visibleMarkersCount[0]) {
//                    visibleMarkersCount[0] = newVisibleMarkersCount;
//
//                    double minSize = 2;
//                    double maxSize = 6;
////                    double minMarkerCount = 80;
//                    double maxMarkerCount = 600;
////                    if (newVisibleMarkersCount < minMarkerCount) {
////                        regionRenderer.setMarkerSize(maxSize);
////                    } else if (newVisibleMarkersCount > maxMarkerCount) {
////                        regionRenderer.setMarkerSize(minSize);
////                    } else {
////                        double size = ((maxMarkerCount - minMarkerCount) - newVisibleMarkersCount) * (maxSize - minSize) / (maxMarkerCount - minMarkerCount) + minSize;
//                    double size = maxSize - (newVisibleMarkersCount * maxSize / maxMarkerCount);
//                    if (size < minSize) {
//                        size = minSize;
//                    } else if (size > maxSize) {
//                        size = maxSize;
//                    }
//                    renderer.setMarkerSize(size);
////                    }
//                }
//            });
////
//////            xAxis.setMin(500);
//////            xAxis.setMax(601);
//        });


        renderer.setMarker(DefaultMarker.CIRCLE);
//        renderer.setPolyLineStyle(LineStyle.HISTOGRAM);



        lineChartPlot.legendVisibleProperty().set(false);

        lineChartPlot.getPlugins().add(new ParameterMeasurements());
//        lineChartPlot.getPlugins().add(new EditAxis());
        final Zoomer zoomer = new Zoomer();
        zoomer.setAxisMode(AxisMode.X);
        zoomer.setSliderVisible(false);
        zoomer.setAddButtonsToToolBar(false);
        zoomer.setPannerEnabled(true);
//        zoomer.setAnimated(false);

//        Platform.runLater(() -> {
//            zoomer.getRangeSlider().lowValueChangingProperty().addListener((o, oldV, newV) -> {
//                System.out.println("lowValueProperty : " + newV);
////            if (!newV.equals(oldV)) {
////            System.out.println(xAxis.minProperty());
////            System.out.println(xAxis.maxProperty());
//                double visibleMarkersCount = xAxis.maxProperty().get() - xAxis.minProperty().get();
//                double minSize = 1.5;
//                double maxSize = 8;
//                double minMarkerCount = 100;
//                double maxMarkerCount = 1000;
//                if (visibleMarkersCount < minMarkerCount) {
//                    renderer.setMarkerSize(maxSize);
//                } else if (visibleMarkersCount > maxMarkerCount) {
//                    renderer.setMarkerSize(minSize);
//                } else {
//                    double size = ((maxMarkerCount - minMarkerCount) - visibleMarkersCount) * (maxSize - minSize) / (maxMarkerCount - minMarkerCount);
//                    System.out.println(size);
//                    renderer.setMarkerSize(size);
//                }
////            }
//            });
//        });

//        renderer.markerSizeProperty().bind();
        lineChartPlot.getPlugins().add(zoomer);
        lineChartPlot.getPlugins().add(new DataPointTooltip2());
        final DefaultErrorDataSet dataSet = new DefaultErrorDataSet("myData");

//        System.out.println(dataSet.getCapacity());
//        System.out.println(dataSet.increaseCapacity(10000));
//        System.out.println(dataSet.getCapacity());
        final DoubleDataSet dataSet2 = new DoubleDataSet("myData2");
        final XYRangeIndicator xRange = new XYRangeIndicator(xAxis, 800.5, 830.5, 10, 10, "PMP22", Color.RED);

        final XYRangeIndicator xRange2 = new XYRangeIndicator(xAxis, 830.5, 868.5, 10, 10, "KIF1A", Color.BLUE);
        final XYRangeIndicator xRange3 = new XYRangeIndicator(xAxis, 868.5, 896.5, 10, 10, "TRPV6", Color.GREEN);
        lineChartPlot.getPlugins().addAll(xRange, xRange2, xRange3);
//        dataSet.setStyle();
//        dataSet.




        final DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        final List<String> categories = new ArrayList<>(Arrays.asList(Arrays.copyOf(dfs.getShortMonths(), 12)));
        for (int i = categories.size(); i < CategoryAxisSample.N_SAMPLES; i++) {
            categories.add("Month" + (i + 1));
        }

        // setting the category via axis forces the axis' category
        // N.B. disable this if you want to use the data set's categories
        xAxis.setCategories(categories);

        double y = 0;
        for (int n = 0; n < CategoryAxisSample.N_SAMPLES; n++) {
//            y += RandomDataGenerator.random() - 0.5;
//            y = Math.random() * ( 0.8 - 1.2 );
            Random random = new Random();
            int nb = 900+random.nextInt(1100-900);
            y = nb / 1000.0;
            final double ex = 0.0;
            final double ey = 0.0;
            if (n == 10) {

            } else {
                dataSet.add(n, y, ex, ey);
            }

            if (n == 3) {
//                System.out.println("style : ");
//                System.out.println(dataSet.getStyle());

                dataSet.addDataStyle(n, "strokeColor=pink; fillColor=green;");
//                System.out.println(dataSet.getStyle());
            }

            if (n == 0) {
                dataSet2.add(n-0.5, n+200, "DataLabel#" + n);
                dataSet2.addDataStyle(0, "strokeColor=grey;fillColor=grey;" + XYChartCss.FONT_SIZE
                        + "=11;");
                // alt:
                // dataSet.addDataStyle(n, "strokeColor:red");
            }
//            dataSet.addDataLabel(n, "SpecialCategory#" + n);
        }

        // setting the axis categories to null forces the first data set's
        // category
        // enable this if you want to use the data set's categories
        // xAxis.setCategories(null);
//        final ErrorDataSetRenderer renderer2 = new ErrorDataSetRenderer();

//        renderer2.setPolyLineStyle(LineStyle.NORMAL);
//        renderer2.setErrorType(ErrorStyle.NONE);
//        renderer2.setMarkerSize(1.5);
        FragmentedDataSet fragmentedDataSet = new FragmentedDataSet("FragmentedDataSet");


        XRangeIndicator ri;
        XYRangeIndicator rie;
        lineChartPlot.getRenderers().add(renderer);
        LabelledMarkerRenderer labelledMarkerRenderer = new LabelledMarkerRenderer();
//        labelledMarkerRenderer.
        labelledMarkerRenderer.getDatasets().add(dataSet2);
        lineChartPlot.getRenderers().add(labelledMarkerRenderer);


//        fragmentedDataSet.add(dataSetF1);
//        fragmentedDataSet.add(dataSetF2);

//        for (int i = 0; i < 3; i++) {
//        DoubleErrorDataSet dataSetE1 = new DoubleErrorDataSet("Set#");
//        dataSetE1.add(10, 2);
//        dataSetE1.add(100, 2);
//        DoubleErrorDataSet dataSetE2 = new DoubleErrorDataSet("Set#");
//        dataSetE2.add(101, 1.3);
//        dataSetE2.add(130, 1.3);
//            for (int n = 0; n < 100; n++) {
//                dataSetE.add(n + i * N_SAMPLES, i, 0.15, 0.15);
//            }
//        fragmentedDataSet.add(dataSetE1);
//        fragmentedDataSet.add(dataSetE2);
//        }
        renderer.getDatasets().addAll(dataSet);


//        CandleStickRenderer candleStickRenderer = new CandleStickRenderer();
//        final  DoubleErrorDataSet dataSetF1 = new DoubleErrorDataSet("myData1");
//        dataSetF1.add(10, 2, -0.3, 0.4);
//        dataSetF1.add(100, 2, -0.3, 0.4);
//
//        final DoubleErrorDataSet dataSetF2 = new DoubleErrorDataSet("myData2");
//        dataSetF2.add(100, 1.3, -0.3, 0.4);
//        dataSetF2.add(300, 1.3, -0.3, 0.4);
//
//        fragmentedDataSet.add(dataSetF1);
//        fragmentedDataSet.add(dataSetF2);


        for (int i = 0; i < 3; i++) {
            DoubleErrorDataSet dataSetF = new DoubleErrorDataSet("Set#" + i);
            for (int n = 0; n < N_SAMPLES; n++) {
                dataSetF.add(n + i * N_SAMPLES, 0.5 * i + Math.cos(Math.toRadians(1.0 * n)), 0.15, 0.15);
            }
            fragmentedDataSet.add(dataSetF);
        }

        ErrorDataSetRenderer renderer2 = new ErrorDataSetRenderer() {
            @Override
            public List<DataSet> render(final GraphicsContext gc, final Chart renderChart, final int dataSetOffset,
                                        final ObservableList<DataSet> datasets) {
                ObservableList<DataSet> filteredDataSets = FXCollections.observableArrayList();
                int dsIndex = 0;
                System.out.println("TEST");
                for (DataSet ds : datasets) {
                    System.out.println(ds);
                    if (ds instanceof FragmentedDataSet) {
                        System.out.println("COUCPU");
                        final FragmentedDataSet fragDataSet = (FragmentedDataSet) ds;
                        for (DataSet innerDataSet : fragDataSet.getDatasets()) {
                            innerDataSet.setStyle(XYChartCss.DATASET_INDEX + '=' + dsIndex);
                            filteredDataSets.add(innerDataSet);
                        }
                    } else {
                        ds.setStyle(XYChartCss.DATASET_INDEX + '=' + dsIndex);
                        filteredDataSets.add(ds);
                    }
                    dsIndex++;
                }
                super.render(gc, renderChart, dataSetOffset, filteredDataSets);

                return filteredDataSets;
            }
        };
        renderer2.getDatasets().addAll(fragmentedDataSet);
        lineChartPlot.getRenderers().add(renderer2);
//        candleStickRenderer.getDatasets().addAll(dataSetF1, dataSetF2);
//        lineChartPlot.getRenderers().add(candleStickRenderer);
//        System.out.println(xAxis.getMin() + " : " + xAxis.getMax());
        xAxis.set(240, 260);
//        System.out.println(xAxis.getMin() + " : " + xAxis.getMax());
//        lineChartPlot.updateAxisRange();
        return lineChartPlot;
    }


    private XYChart getChart2() {
        final CategoryAxis xAxis = new CategoryAxis("months");
        xAxis.setTickLabelRotation(90);
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        xAxis.setMaxMajorTickLabelCount(N_SAMPLES + 1);
        xAxis.setTickLabelsVisible(true);
        xAxis.setAutoRanging(true);

        final DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        final List<String> categories = new ArrayList<>(Arrays.asList(Arrays.copyOf(dfs.getShortMonths(), 12)));
        for (int i = categories.size(); i < CategoryAxisSample.N_SAMPLES; i++) {
            categories.add("Month" + (i + 1));
        }

        // setting the category via axis forces the axis' category
        // N.B. disable this if you want to use the data set's categories
        xAxis.setCategories(categories);

        final DefaultNumericAxis yAxis = new DefaultNumericAxis("yAxis");

        yAxis.set(-0.9, 3);
        yAxis.setAutoUnitScaling(false);
        yAxis.setAutoRanging(false);
        yAxis.setAutoRangeRounding(false);
        yAxis.setAutoGrowRanging(false);
        yAxis.setMinorTickCount(0);
        yAxis.setMaxMajorTickLabelCount(7);
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
        yAxis.setPadding(new Insets(0, 0, 20, 0));

        final XYChart chart = new XYChart(xAxis, yAxis);
        chart.setHorizontalGridLinesVisible(true);
        chart.setVerticalGridLinesVisible(false);
        chart.setAnimated(false);
        chart.getRenderers().clear();

        final Zoomer zoomer = new Zoomer();
        zoomer.setAxisMode(AxisMode.X);
        zoomer.setSliderVisible(false);
        zoomer.setAddButtonsToToolBar(false);
        zoomer.setPannerEnabled(true);
        chart.getPlugins().add(zoomer);

        final ErrorDataSetRenderer renderer = new ErrorDataSetRenderer();
        renderer.setPolyLineStyle(LineStyle.NONE);
        renderer.setErrorType(ErrorStyle.NONE);
        renderer.setMarkerSize(1.5);


//        ErrorDataSetRenderer renderer2 = new ErrorDataSetRenderer() {
//            @Override
//            public List<DataSet> render(final GraphicsContext gc, final Chart renderChart, final int dataSetOffset,
//                                        final ObservableList<DataSet> datasets) {
//                ObservableList<DataSet> filteredDataSets = FXCollections.observableArrayList();
//                int dsIndex = 0;
//                for (DataSet ds : datasets) {
//                    System.out.println("test");
//                    if (ds instanceof FragmentedDataSet) {
//                        final FragmentedDataSet fragDataSet = (FragmentedDataSet) ds;
//                        for (DataSet innerDataSet : fragDataSet.getDatasets()) {
//                            innerDataSet.setStyle(XYChartCss.DATASET_INDEX + '=' + dsIndex);
//                            filteredDataSets.add(innerDataSet);
//                        }
//                    } else {
//                        ds.setStyle(XYChartCss.DATASET_INDEX + '=' + dsIndex);
//                        filteredDataSets.add(ds);
//                    }
//                    dsIndex++;
//                }
//                super.render(gc, renderChart, dataSetOffset, filteredDataSets);
//
//                return filteredDataSets;
//            }
//        };


//        FragmentedDataSet fragmentedDataSet = new FragmentedDataSet("FragmentedDataSet");
//        final  DoubleErrorDataSet dataSetF1 = new DoubleErrorDataSet("myData1");
//        dataSetF1.add(10, 2 );
//        dataSetF1.add(100, 2 );
//        final DoubleErrorDataSet dataSetF2 = new DoubleErrorDataSet("myData2");
//        dataSetF2.add(100, 1.3 );
//        dataSetF2.add(300, 1.3 );
//        fragmentedDataSet.add(dataSetF1);
//        fragmentedDataSet.add(dataSetF2);

        final DoubleDataSet dataSet = new DoubleDataSet("myData2");
        double y = 0;
        for (int n = 0; n < CategoryAxisSample.N_SAMPLES; n++) {
            Random random = new Random();
            int nb = 900+random.nextInt(1100-900);
            y = nb / 1000.0;
            final double ex = 0.0;
            final double ey = 0.0;
//            if (n == 10) {
//
//            } else {
            System.out.println(n + " => " + y);
                dataSet.add(n, y, ex, ey);
//            }
        }
//        renderer2.getDatasets().add(fragmentedDataSet);
        renderer.getDatasets().add(dataSet);
//        chart.getRenderers().clear();
        chart.getRenderers().addAll(renderer);
//        chart.getDatasets().addAll(dataSet);

        return chart;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }
}