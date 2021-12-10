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
import de.gsi.chart.renderer.RendererDataReducer;
import de.gsi.chart.renderer.datareduction.MaxDataReducer;
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
import ngsdiaglim.utils.NumberUtils;


/**
 * @author rstein
 */
public class ChartTest extends Application {
    private static final int N_SAMPLES = 100;

    @Override
    public void start(final Stage primaryStage) {
        CSSFX.start();
        final VBox root = new VBox();

        XYChart chat1 = getChart2();

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


    private XYChart getChart2() {
        final CategoryAxis xAxis = new CategoryAxis("months");
        xAxis.setTickLabelRotation(90);
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        xAxis.setMaxMajorTickLabelCount(N_SAMPLES + 1);
        xAxis.setTickLabelsVisible(true);
        xAxis.setAutoRanging(true);

        final DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        final List<String> categories = new ArrayList<>(Arrays.asList(Arrays.copyOf(dfs.getShortMonths(), 12)));
        for (int i = categories.size(); i < N_SAMPLES; i++) {
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
//        System.out.println(chart.getGridRenderer().getHorizontalMajorGrid().getStyleClass().add("testline"));
        chart.getGridRenderer().getStylesheets().clear();
        chart.getGridRenderer().getScene().getStylesheets().clear();

//        System.out.println(chart.getGridRenderer().getHorizontalMajorGrid().getStyleClass().setAll("testline"));
//        System.out.println(chart.getGridRenderer().getHorizontalMajorGrid().getStyleClass());

        final Zoomer zoomer = new Zoomer();
        zoomer.setAxisMode(AxisMode.X);
        zoomer.setSliderVisible(false);
        zoomer.setAddButtonsToToolBar(false);
        zoomer.setPannerEnabled(true);
        chart.getPlugins().add(zoomer);

        final ErrorDataSetRenderer renderer = new ErrorDataSetRenderer();
        renderer.setPolyLineStyle(LineStyle.NONE);
        renderer.setErrorType(ErrorStyle.NONE);
//        renderer.setRendererDataReducer(new MaxDataReducer());
        renderer.setMarkerSize(1.5);


        final ErrorDataSetRenderer renderer2 = new ErrorDataSetRenderer();
        renderer.setPolyLineStyle(LineStyle.NORMAL);
        renderer.setErrorType(ErrorStyle.ERRORBARS);
//        renderer.setRendererDataReducer(new MaxDataReducer());
//        renderer.setDrawMarker(false);
//        renderer.setAllowNaNs(true);

//        DefaultErrorDataSet dataSet2 = new DefaultErrorDataSet("FragmentedDataSet");
//        dataSet2.setStyle("strokeWidth=3;");
//        dataSet2.add(10, 2 );
//        dataSet2.add(99.4, 2 );
//        dataSet2.add(99.5, Double.NaN );
//        dataSet2.add(99.6, 1.3 );
//        dataSet2.add(300, 1.3 );

        final DefaultErrorDataSet dataSet = new DefaultErrorDataSet("myData2");
        double y = 0;
        for (int n = 0; n < N_SAMPLES; n++) {
//            Random random = new Random();
//            int nb = 900+random.nextInt(1100-900);
//            y = nb / 1000.0;
            y = 1;
            final double ex = 0.5;
            final double ey = 1;
            dataSet.add(n, y, ex, ey);

        }
//        renderer2.getDatasets().addAll(fragmentedDataSet);
        renderer.getDatasets().addAll(dataSet);
//        renderer2.getDatasets().addAll(dataSet2);
//        chart.getRenderers().clear();
        chart.getRenderers().addAll(renderer, renderer2);
//        chart.getDatasets().addAll(fragmentedDataSet);

        Screenshot screenshot = new Screenshot();
        chart.getPlugins().add(screenshot);
//        screenshot.
        return chart;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }
}