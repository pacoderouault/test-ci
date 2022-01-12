package ngsdiaglim.controllers.charts;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.*;

public class PredictionsChart extends Region {

    private Canvas canvas;
    private GraphicsContext ctx;
    private Pane pane;
    private final ObservableList<PredictionChartItem> items = FXCollections.observableArrayList();
    private final int size;
    private final int maxValue;
    private final int textSize = 120;
    private final double centerSize = 5;

    private double lastMouseX;
    private double lastMouseY;
    private static final int TOOLTIP_XOFFSET = 10;
    private static final int TOOLTIP_YOFFSET = 7;



    public PredictionsChart(int maxValue, int size) {
        this.maxValue = maxValue;
        this.size = size + textSize;
        initGraphics();

        items.addListener((ListChangeListener<PredictionChartItem>) c -> drawChart());
    }

    private void initGraphics() {

        canvas = new Canvas(size, size);

        ctx = canvas.getGraphicsContext2D();

        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.CENTER);

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    public void drawChart() {

        int noOfChartItems  = items.size();
        double minValue = 0;
        double stepSize = 360.0 / noOfChartItems;
        double startAngle = -90 -stepSize / 2;
        double center = size * 0.5;
        double currX, currY;
        double extrX, extrY;
        double valueX, valueY;
        double textX, textY;
        double valueDotWidth = 5;
        ctx.clearRect(0, 0, size, size);
        ctx.setFont(Font.font(10));
        ctx.setLineWidth(1);

        // pathogenic polygon
        double[] xValues = new double[items.size()];
        double[] yValues = new double[items.size()];

        // pathogenic values dot
        List<Point> pathogenicDots = new ArrayList<>();

        // concentric polygon dotted
        List<double[]> xValuesPolygonsDashed = new ArrayList<>();
        List<double[]> yValuesPolygonsDashed = new ArrayList<>();

        // concentric polygon solid
        List<double[]> xValuesPolygonsSolid = new ArrayList<>();
        List<double[]> yValuesPolygonsSolid = new ArrayList<>();

        // concentric plain polygon solid
        List<double[]> xValuesPolygonsFillSolid = new ArrayList<>();
        List<double[]> yValuesPolygonsFillSolid = new ArrayList<>();

        Map<String, Rectangle2D> tooltips = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            xValuesPolygonsDashed.add(new double[items.size()]);
            yValuesPolygonsDashed.add(new double[items.size()]);
        }

        xValuesPolygonsSolid.add(new double[items.size()]);
        yValuesPolygonsSolid.add(new double[items.size()]);
        xValuesPolygonsFillSolid.add(new double[items.size()]);
        yValuesPolygonsFillSolid.add(new double[items.size()]);

        ctx.setStroke(Color.DARKGREY);
        double radius = ((size - textSize) / 2.0);

        for (int i = 0 ; i < noOfChartItems ; i++) {
            PredictionChartItem item = items.get(i);
            double value = item.getValue();
            if (value < minValue) {
                value = minValue;
            }
            else {
                value = (value / maxValue) * radius;
            }
            int m = 0;
            for (double k = 0; k < radius; k += radius / 10) {
                currX = center + (k + centerSize) * Math.cos(Math.toRadians(startAngle + stepSize));
                currY = center + (k + centerSize) * Math.sin(Math.toRadians(startAngle + stepSize));
                xValuesPolygonsDashed.get(m)[i] = currX;
                yValuesPolygonsDashed.get(m)[i] = currY;
                m++;
            }

            ctx.setFill(Color.DARKGREY);
            startAngle += stepSize;
            double startX = center + centerSize * Math.cos(Math.toRadians(startAngle));
            double startY = center + centerSize * Math.sin(Math.toRadians(startAngle));
            extrX = center + (radius + centerSize) * Math.cos(Math.toRadians(startAngle));
            extrY = center + (radius + centerSize) * Math.sin(Math.toRadians(startAngle));

            // last concentric polygon
            xValuesPolygonsFillSolid.get(0)[i] = startX;
            yValuesPolygonsFillSolid.get(0)[i] = startY;
            xValuesPolygonsSolid.get(0)[i] = extrX;
            yValuesPolygonsSolid.get(0)[i] = extrY;

            // vertical line
            ctx.strokeLine(center, center, extrX, extrY);

            // text
            ctx.setFill(Color.web("4b4b4bff"));
            textX = center + (radius + 30) * Math.cos(Math.toRadians(startAngle));
            textY = center + (radius + 30) * Math.sin(Math.toRadians(startAngle));
            ctx.fillText(item.getName() + "\n" + item.getSubtitle(), textX, textY);
            Text t = new Text(item.getName() + "\n" + item.getSubtitle());
            tooltips.put(item.getTooltipText(), new Rectangle2D(textX - t.getLayoutBounds().getWidth() / 2,
                    textY - t.getLayoutBounds().getHeight() / 2,
                    t.getLayoutBounds().getWidth(),
                    t.getLayoutBounds().getHeight()));

            // values for pathogenic polygon
            valueX = center + (value + centerSize) * Math.cos(Math.toRadians(startAngle));
            xValues[i] = valueX;
            valueY = center + (value + centerSize) * Math.sin(Math.toRadians(startAngle));
            yValues[i] = valueY;
            if (item.getValue() >= 0) {
                pathogenicDots.add(new Point(valueX, valueY, item.getColor()));
            }


        }

        setToolTips(pane, tooltips);

        ctx.setLineDashes(3);
        ctx.setStroke(Color.LIGHTGREY);
        for (int i = 0; i < xValuesPolygonsDashed.size(); i++) {
            ctx.strokePolygon(xValuesPolygonsDashed.get(i), yValuesPolygonsDashed.get(i), xValuesPolygonsDashed.get(i).length);
        }

        ctx.setStroke(Color.DARKGREY);
        ctx.setFill(Color.web("4b4b4bff"));
        ctx.setLineDashes(0);
        for (int i = 0; i < xValuesPolygonsSolid.size(); i++) {
            ctx.strokePolygon(xValuesPolygonsSolid.get(i), yValuesPolygonsSolid.get(i), xValuesPolygonsSolid.get(i).length);
        }

        ctx.setFill(Color.ORANGE);
        ctx.setGlobalAlpha(0.6);
        ctx.fillPolygon(xValues, yValues, items.size());
        ctx.setGlobalAlpha(1);
        ctx.setStroke(Color.DARKORANGE);
        ctx.strokePolygon(xValues, yValues, items.size());

        for (Point p : pathogenicDots) {
            ctx.setFill(p.getColor());
            ctx.fillRoundRect(p.getX() - valueDotWidth / 2, p.getY() - valueDotWidth / 2, valueDotWidth, valueDotWidth, valueDotWidth, valueDotWidth);
        }

        ctx.setFill(Color.WHITE);
        ctx.setStroke(Color.DARKGREY);
        for (int i = 0; i < xValuesPolygonsFillSolid.size(); i++) {
            ctx.strokePolygon(xValuesPolygonsFillSolid.get(i), yValuesPolygonsFillSolid.get(i), xValuesPolygonsFillSolid.get(i).length);
            ctx.fillPolygon(xValuesPolygonsFillSolid.get(i), yValuesPolygonsFillSolid.get(i), xValuesPolygonsFillSolid.get(i).length);
        }
    }


//    public void drawChartRound() {
//        int noOfChartItems  = items.size();
//
//        double minValue = 0;
//        double stepSize = 360.0 / noOfChartItems;
//        double startAngle = -90;
//        double center = size * 0.5;
//
//        double extrX, extrY;
//        double valueX, valueY;
//        double textX, textY;
//
//        ctx.clearRect(0, 0, size, size);
//        ctx.setFill(Color.web("#000000dd"));
//        ctx.fillRect(0, 0, size, size);
//        ctx.setFont(Font.font(12));
//        ctx.setFill(Color.BLUE);
//        ctx.setFill(Color.RED);
//
//        double[] xValues = new double[items.size()];
//        double[] yValues = new double[items.size()];
//
//
//
//        ctx.setStroke(Color.WHITE);
//        double radius = (size - textSize) / 2.0;
//        for (double m = minValue; m <= radius; m += radius / 10) {
//            double width = m * 2;
//            double x = center - m;
//            ctx.strokeOval(x, x, width, width);
//        }
//
//        for (int i = 0 ; i < noOfChartItems ; i++) {
//
//            ChartItem item = items.get(i);
//
//            double value = item.getValue();
//            value = (value / maxValue) * ((size - textSize) / 2.0);
//
//            ctx.setStroke(Color.WHITE);
//
//            ctx.setLineWidth(1);
//            ctx.setLineDashes(0);
//            startAngle += stepSize;
//            extrX = center + radius * Math.cos(Math.toRadians(startAngle));
//            extrY = center + radius * Math.sin(Math.toRadians(startAngle));
//            ctx.strokeLine(center, center, extrX, extrY);
//
//            textX = center + (radius + 20) * Math.cos(Math.toRadians(startAngle));
//            textY = center + (radius + 20) * Math.sin(Math.toRadians(startAngle));
//
//            Text t = new Text("test");
//            ctx.setT
//
//            ctx.fillText(item.getName() + "\n" + item.getSubtitle(), textX, textY);
//
//            // values for polygon
//            valueX = center + value * Math.cos(Math.toRadians(startAngle));
//            xValues[i] = valueX;
//            valueY = center + value * Math.sin(Math.toRadians(startAngle));
//            yValues[i] = valueY;
//        }
//
//        ctx.strokePolygon(xValues, yValues, items.size());
//    }

    public double getMinValue() { return items.stream().mapToDouble(PredictionChartItem::getValue).min().getAsDouble(); }

    public double getMaxValue() { return items.stream().mapToDouble(PredictionChartItem::getValue).max().getAsDouble(); }

    private void setToolTips(Node node, Map<String, Rectangle2D> tooltips) {
        Duration openDelay = Duration.millis(200);
        Duration hideDelay = Duration.millis(5000);
        Tooltip toolTip = new Tooltip();
        toolTip.setShowDelay(Duration.ZERO);
        Timeline hideTimer = new Timeline();
        hideTimer.getKeyFrames().add(new KeyFrame(hideDelay));
        hideTimer.setOnFinished(event -> toolTip.hide());

        Timeline activationTimer = new Timeline();
        activationTimer.getKeyFrames().add(new KeyFrame(openDelay));
        activationTimer.setOnFinished(event -> {
            Bounds nodeScreenBounds = node.localToScreen(node.getBoundsInLocal());
            double nMx = nodeScreenBounds.getMinX();
            double nMy = nodeScreenBounds.getMinY();
            toolTip.setText("");
            tooltips.forEach((str, bounds) -> {
                double mnX = nMx + bounds.getMinX();
                double mnY = nMy + bounds.getMinY();
                double mxX = mnX + bounds.getWidth();
                double mxY = mnY + bounds.getHeight();
                if (lastMouseX >= mnX && lastMouseX <= mxX && lastMouseY >= mnY && lastMouseY <= mxY) {
                    toolTip.setText(str);
                }
            });
            if (!toolTip.getText().isEmpty()) {
                toolTip.show(node.getScene().getWindow(), lastMouseX + TOOLTIP_XOFFSET, lastMouseY + TOOLTIP_YOFFSET);
                hideTimer.playFromStart();
            }
        });

        node.setOnMouseMoved(e -> {
            double buffPx = 2;
            double eX = e.getScreenX();
            double eY = e.getScreenY();
            // Not hiding for slight mouse movements while tooltip is showing
            if (hideTimer.getStatus() == Animation.Status.RUNNING) {
                if (lastMouseX - buffPx <= eX && lastMouseX + buffPx >= eX && lastMouseY - buffPx <= eY && lastMouseY + buffPx >= eY) {
                    return;
                }
            }
            lastMouseX = e.getScreenX();
            lastMouseY = e.getScreenY();
            toolTip.hide();
            hideTimer.stop();
            activationTimer.playFromStart();
        });

        node.setOnMouseExited(e -> {
            toolTip.hide();
            activationTimer.stop();
            hideTimer.stop();
        });
    }

    public void setChartItems(Collection<PredictionChartItem> chartItems) {
        items.setAll(chartItems);
    }
}
