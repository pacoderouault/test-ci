package ngsdiaglim.controllers.charts;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import ngsdiaglim.utils.NumberUtils;

import java.util.List;

public class PredictionGauge3 extends Pane {

    private final double min;
    private final double max;
    private boolean reverse;
    private final List<Double> stopPositions;
    private final List<PredictionGaugeLabel> labels;
    private final SimpleObjectProperty<Float> score = new SimpleObjectProperty<>(null);
    private final double rectangleWidth = 150;
    private Rectangle rectangle;
    private final Label markerLabel = new Label();
    private final Polygon markerArrow = new Polygon();
    private final static String gaugeRangeLabelClass = "predictiongauge-range-label";
    private final static String gaugeRangeLabelLeftClass = "predictiongauge-range-label-left";
    private final static String gaugeRangeLabelRightClass = "predictiongauge-range-label-right";
    private final static String gaugeThresholdLabelClass = "predictiongauge-threshold-label";
    private final static String gaugeShapeClass = "predictiongauge-shape";
    private final static String markerLabelClass = "predictiongauge-marker-label";
    private final static String markerArrowClass = "predictiongauge-marker-arrow";

    public PredictionGauge3(double min, double max, boolean reverse, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        this.reverse = reverse;
        init();
    }

    public PredictionGauge3(double min, double max, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        init();
    }

    private void init() {
        getStyleClass().add("prediction-gauge-region");

        Label leftLabel = new Label(String.valueOf(NumberUtils.round(min, 2)));
        leftLabel.setWrapText(false);
        leftLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        leftLabel.getStyleClass().addAll(gaugeRangeLabelClass, gaugeRangeLabelLeftClass);

        Label rightLabel = new Label(String.valueOf(NumberUtils.round(max, 2)));
        rightLabel.setWrapText(false);
        rightLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        rightLabel.getStyleClass().addAll(gaugeRangeLabelClass, gaugeRangeLabelRightClass);

        double rectangleHeight = 7;
        rectangle = new Rectangle(rectangleWidth, rectangleHeight);
        rectangle.setFill(getRectangleGradient());
        rectangle.getStyleClass().add(gaugeShapeClass);
        getChildren().addAll(leftLabel, rectangle, rightLabel);

        leftLabel.layoutYProperty().bind(rectangle.layoutYProperty().subtract(leftLabel.heightProperty().divide(2f)).add(rectangle.heightProperty().divide(2f)));
        rectangle.layoutXProperty().bind(leftLabel.layoutXProperty().add(leftLabel.widthProperty()).add(3));
        rectangle.setLayoutY(20);
        rightLabel.layoutXProperty().bind(rectangle.layoutXProperty().add(rectangle.widthProperty()).add(3));
        rightLabel.layoutYProperty().bind(rectangle.layoutYProperty().subtract(leftLabel.heightProperty().divide(2f)).add(rectangle.heightProperty().divide(2f)));

        if (labels != null) {
            for (PredictionGaugeLabel label : labels) {
                Rectangle r = new Rectangle(2, rectangleHeight);
                r.getStyleClass().add(gaugeShapeClass);
                double xPos = convertScoreToPos(label.getValue());
                r.layoutYProperty().bind(rectangle.layoutYProperty());
                r.layoutXProperty().bind(rectangle.layoutXProperty().add(xPos).subtract(1));

                Label l = new Label(label.getText());
                l.getStyleClass().add(gaugeThresholdLabelClass);
                l.layoutYProperty().bind(rectangle.layoutYProperty().add(rectangle.heightProperty()).subtract(1));
                l.layoutXProperty().bind(rectangle.layoutXProperty().add(xPos).subtract(l.widthProperty().divide(2f)));
                getChildren().addAll(r, l);
            }
        }

        // Marker
        markerLabel.getStyleClass().add(markerLabelClass);
        markerLabel.layoutYProperty().bind(markerArrow.layoutYProperty().subtract(markerLabel.heightProperty()).add(3));
        markerLabel.layoutXProperty().bind(markerArrow.layoutXProperty().subtract(markerLabel.widthProperty().divide(2f)).add(5));
        markerArrow.getStyleClass().add(markerArrowClass);
        markerArrow.layoutYProperty().bind(rectangle.layoutYProperty().subtract(7));
        markerArrow.getPoints().setAll(
                0d, 0d,
                10d, 0d,
                5d, 7d
        );
        markerLabel.visibleProperty().bind(score.isNotNull());
        markerArrow.visibleProperty().bind(score.isNotNull());
        getChildren().addAll(markerArrow, markerLabel);

        score.addListener((obs, oldV, newV) -> {
//            if (score.get() != null) {
                updateMarker();
//            }
        });
        score.set(null);
        disableProperty().bind(score.isNull());
    }

    private void updateMarker() {
        applyCss();
        layout();
        double xPos = convertScoreToPos(score.get());
        String markerStr = score.get() == null ? null : String.valueOf(NumberUtils.round(score.get(), 3));
        markerLabel.setText(markerStr);
        markerArrow.layoutXProperty().unbind();
        markerArrow.layoutXProperty().bind(rectangle.layoutXProperty().add(xPos).subtract(5d));
//        markerArrow.setLayoutX(rectangle.getLayoutX() + xPos - markerArrow.getLayoutBounds().getWidth() / 2f);

    }

    public Float getScore() {
        return score.get();
    }

    public SimpleObjectProperty<Float> scoreProperty() {
        return score;
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

        Stop[] stops = new Stop[3];
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

    private double convertScoreToPos(Float score) {
        if (score == null) return 0;
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
}
