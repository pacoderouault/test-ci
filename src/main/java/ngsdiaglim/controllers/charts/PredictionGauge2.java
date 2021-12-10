package ngsdiaglim.controllers.charts;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import ngsdiaglim.utils.NumberUtils;

import java.util.List;

public class PredictionGauge2 extends Region {

    private final double min;
    private final double max;
    private boolean reverse;
    private Stop[] stops;
    private final List<Double> stopPositions;
    private final List<PredictionGaugeLabel> labels;
    private final static String valueLabelClass = "predgauge-value-label";
    private final static String markerLabelClass = "predgauge-marker-label";
    private final static String markerClass = "predgauge-marker";
    private final SimpleObjectProperty<Float> score = new SimpleObjectProperty<>(null);
    private final double rectangleWidth = 150;
    private final double rectangleHeight = 7;
    private Canvas baseCanvas;
    private Canvas markerCanvas;
    private Font robotoMedium;
    private Font robotoRegular;

    public PredictionGauge2(double min, double max, boolean reverse, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        this.reverse = reverse;
        init();
    }
    public PredictionGauge2(double min, double max, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        init();
    }

    private void init() {
        robotoRegular = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 11);
        robotoMedium = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf"), 11);
        baseCanvas = new Canvas(rectangleWidth + 80, 45);
        markerCanvas = new Canvas(rectangleWidth + 80, 45);

        drawBaseCanvas();
        drawMarkerCanvas();
        getChildren().addAll(baseCanvas, markerCanvas);

        score.addListener((obs, oldV, newV) -> drawMarkerCanvas());
        visibleProperty().bind(score.isNotNull());
        minHeightProperty().bind(baseCanvas.heightProperty());
        maxHeightProperty().bind(baseCanvas.heightProperty());
        minWidthProperty().bind(baseCanvas.widthProperty());
        maxWidthProperty().bind(baseCanvas.widthProperty());
    }

    private void drawBaseCanvas() {

        GraphicsContext gc = baseCanvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(
                0,
                0,
                baseCanvas.getWidth(),
                baseCanvas.getHeight());

        // left label min value
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(robotoRegular);
        gc.setFill(Color.BLACK);
        gc.fillText(
                String.valueOf(NumberUtils.round(min, 3)),
                29.5,
                baseCanvas.getHeight() / 2f + rectangleHeight / 2f + 0.5
                );

        // rectangle
        gc.setFill(getRectangleGradient());
        gc.fillRect(
                33,
                baseCanvas.getHeight() / 2f + 0.5,
                rectangleWidth,
                rectangleHeight);

        // right label max value
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLACK);
        gc.fillText(
                String.valueOf(NumberUtils.round(max, 3)),
                35.5 + rectangleWidth,
                baseCanvas.getHeight() / 2f + rectangleHeight / 2f + 0.5
        );

        // pathologies thresholds labels
        if (labels != null) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.TOP);
            for (PredictionGaugeLabel label : labels) {
                double xLabel = convertScoreToPos(label.getValue());

                gc.fillRect(
                        33 + xLabel - 1.5,
                        baseCanvas.getHeight() / 2f + 0.5,
                        3,
                        rectangleHeight
                );

                gc.fillText(
                        String.valueOf(label.getText()),
                        33 + xLabel,
                        baseCanvas.getHeight() / 2f + rectangleHeight
                );
            }
        }
    }


    private void drawMarkerCanvas() {
        GraphicsContext gc = markerCanvas.getGraphicsContext2D();
        gc.setFill(Color.TRANSPARENT);
        gc.clearRect(
                0,
                0,
                markerCanvas.getWidth(),
                markerCanvas.getHeight());
        if (score.get() != null) {
            double xPos = convertScoreToPos(score.getValue());

            // text
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFont(robotoMedium);
            gc.setFill(Color.BLACK);

            String scoreStr = String.valueOf(NumberUtils.round(score.getValue(), 3));
            Bounds valueBounds = getBoundingBox(scoreStr, gc.getFont());
            //rectangle background
            double backgroundWidth = valueBounds.getWidth() + 6;
            double backgroundHeight = valueBounds.getHeight();
            gc.setFill(Color.valueOf("#34a5da"));
            gc.fillRoundRect(
                    33 + xPos - backgroundWidth / 2f,
                    baseCanvas.getHeight() / 2f - backgroundHeight - 3.5,
                    backgroundWidth,
                    backgroundHeight,
                    5,
                    5
            );

            double[] markerXpoints = new double[]{
                    33 + xPos - 5,
                    33 + xPos + 5,
                    33 + xPos};
            double[] markerYpoints = new double[]{
                    baseCanvas.getHeight() / 2f - 6,
                    baseCanvas.getHeight() / 2f - 6,
                    baseCanvas.getHeight() / 2f};
            gc.fillPolygon(markerXpoints, markerYpoints, markerYpoints.length);
            gc.setFill(Color.WHITE);
            gc.fillText(
                    scoreStr,
                    33 + xPos,
                    baseCanvas.getHeight() / 2f - backgroundHeight / 2f - 4
            );


        }
    }

    public void setScore(Float score) {
        this.score.set(score);
    }

    private LinearGradient getRectangleGradient() {
        final double saturation = 0.6d;
        final double brightness = 1d;

        int MAX_COLOR;
        int MIN_COLOR;
        if (reverse) {
            MAX_COLOR = 120;
            MIN_COLOR = 0;
        } else {
            MAX_COLOR = 0;
            MIN_COLOR = 120;
        }

        stops = new Stop[3];
        if (stopPositions == null || stopPositions.size() != 3) {
            stops[0] = new Stop(0, Color.hsb(MIN_COLOR, saturation, brightness));
            stops[1] = new Stop(0.5, Color.hsb((MIN_COLOR + (MAX_COLOR - MIN_COLOR) / 2d), saturation, brightness));
            stops[2] = new Stop(1, Color.hsb(MAX_COLOR, saturation, brightness));
        } else {
            stops[0] = new Stop(stopPositions.get(0) == null ? 0 : convertToNewRange(stopPositions.get(0)), Color.hsb(MIN_COLOR, saturation, brightness));
            stops[1] = new Stop(stopPositions.get(1) == null ? 0.5 : convertToNewRange(stopPositions.get(1)), Color.hsb((MIN_COLOR + (MAX_COLOR - MIN_COLOR) / 2d), saturation, brightness));
            stops[2] = new Stop(stopPositions.get(2) == null ? 1 : convertToNewRange(stopPositions.get(2)), Color.hsb(MAX_COLOR, saturation, brightness));
        }

        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
    }

    private double convertToNewRange(double value) {
        double oldMin = Math.min(min, max);
        double oldMax = Math.max(min, max);
        double oldRange = oldMax - oldMin;
        return (value - oldMin) / oldRange;
    }

    private double convertScoreToPos(double score) {
        double scaledScore = convertToNewRange(score);
        double pos;
        if (min > max) {
            pos =  - scaledScore * rectangleWidth;
        } else {
            pos = scaledScore * rectangleWidth;
        }
        if (pos < 0) pos = 0;
        else pos = Math.min(pos, rectangleWidth);
        return pos;
    }

    public Bounds getBoundingBox(String s, Font font) {
        Text t = new Text(s);
        t.setFont(font);
        t.applyCss();
        return t.getLayoutBounds();
    }
}
