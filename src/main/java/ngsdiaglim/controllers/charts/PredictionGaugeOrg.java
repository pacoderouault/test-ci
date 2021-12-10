package ngsdiaglim.controllers.charts;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class PredictionGaugeOrg extends VBox {

    private final double min;
    private final double max;
    private final SimpleDoubleProperty gaugeHeight = new SimpleDoubleProperty(10);
    private final SimpleDoubleProperty gaugeWidth = new SimpleDoubleProperty(200);
    private final SimpleObjectProperty<Float> score = new SimpleObjectProperty<>(null);
    private final List<Double> stopPositions;
    private final List<PredictionGaugeLabel> labels;
    private Stop[] stops;
    private final Rectangle rectangle = new Rectangle();
    private boolean reverse = false;
    private final VBox markerBox = new VBox();
    private final Label markerLabel = new Label();
    private final Pane pane = new Pane();
    private final static String predictionGaugeClass = "predgauge-gauge";
    private final static String valueLabelClass = "predgauge-value-label";
    private final static String valueLabelLeftClass = "predgauge-value-label-left";
    private final static String valueLabelRightClass = "predgauge-value-label-right";
    private final static String gaugeLabelClass = "predgauge-gauge-label";
    private final static String gaugeBaseline = "predgauge-baseline";
    private final static String markerLabelClass = "predgauge-marker-label";
    private final static String markerArrowClass = "predgauge-arrow-label";
    private final static String markerClass = "predgauge-marker";

    public PredictionGaugeOrg(double min, double max, boolean reverse, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        this.reverse = reverse;
        init();
    }
    public PredictionGaugeOrg(double min, double max, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        init();
    }

    private void init() {
        Label leftLabel = new Label(String.valueOf(NumberUtils.round(min, 2)));
        leftLabel.setWrapText(false);
        leftLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        Label rightLabel = new Label(String.valueOf(NumberUtils.round(max, 2)));
        rightLabel.setWrapText(false);
        rightLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        leftLabel.getStyleClass().addAll(valueLabelClass, valueLabelLeftClass);
        rightLabel.getStyleClass().addAll(valueLabelClass, valueLabelRightClass);
        HBox baseBox = new HBox();
        baseBox.setAlignment(Pos.CENTER_LEFT);
        baseBox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);
        baseBox.setSpacing(3);
        baseBox.getStyleClass().add(gaugeBaseline);
        baseBox.getChildren().addAll(leftLabel, pane, rightLabel);
//        baseBox.setStyle("-fx-background-color: green;");
        markerLabel.getStyleClass().add(markerLabelClass);
        Polygon marker = new Polygon();
        marker.getStyleClass().add(markerArrowClass);

        generateStops();

        fillRectangle();
        rectangle.heightProperty().bind(gaugeHeight);
        rectangle.widthProperty().bind(gaugeWidth);



        markerBox.setAlignment(Pos.TOP_CENTER);
        markerBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        markerBox.getChildren().addAll(markerLabel, marker);
        marker.getPoints().setAll(
                0d, 0d,
                10d, 0d,
                5d, 5d
        );
        markerBox.translateYProperty().bind(rectangle.layoutYProperty().subtract(markerBox.heightProperty()));
        markerBox.getStyleClass().add(markerClass);

        pane.getStyleClass().add(predictionGaugeClass);
        pane.getChildren().addAll(rectangle, markerBox);
        getChildren().addAll(baseBox);

        generateLabels();

        rectangle.heightProperty().addListener((obs -> draw()));
        rectangle.widthProperty().addListener((obs -> draw()));

        visibleProperty().bind(this.score.isNotNull());
        scoreProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                setMarkerPosition();
            }
        });

        getStyleClass().add("gaugetest");
    }

    private void draw() {
        setMarkerPosition();
    }

    private void setMarkerPosition() {
        markerLabel.setText(String.valueOf(score.get()));
        applyCss();
        layout();
//        markerBox.translateXProperty(rectangle.layoutXProperty());
//        System.out.println("rectangle.layoutXProperty() : " + rectangle.layoutXProperty());
//        markerBox.setTranslateX(rectangle.getLayoutX() + convertScoreToPos(score.get()) - (markerLabel.getWidth() / 2f));
        markerBox.translateXProperty().bind(rectangle.layoutXProperty().add(convertScoreToPos(score.get())).subtract(markerLabel.heightProperty().divide(2f)));
    }

    public float getScore() {
        return score.get();
    }

    public SimpleObjectProperty<Float> scoreProperty() {
        return score;
    }

    public void setScore(Float score) {
        this.score.setValue(score);
    }

    public double getGaugeHeight() {
        return gaugeHeight.get();
    }

    public SimpleDoubleProperty gaugeHeightProperty() {
        return gaugeHeight;
    }

    public void setGaugeHeight(double gaugeHeight) {
        this.gaugeHeight.set(gaugeHeight);
    }

    public double getGaugeWidth() {
        return gaugeWidth.get();
    }

    public SimpleDoubleProperty gaugeWidthProperty() {
        return gaugeWidth;
    }

    public void setGaugeWidth(double gaugeWidth) {
        this.gaugeWidth.set(gaugeWidth);
    }

    private void generateStops() {

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
    }

    private void generateLabels() {
        if (labels != null) {
            Platform.runLater(() -> {
                for (PredictionGaugeLabel label : labels) {
                    VBox labelBox = generateLabelBox(label.getText());
                    pane.getChildren().add(labelBox);
                    applyCss();
                    layout();
                    labelBox.translateYProperty().bind(rectangle.layoutYProperty());
                    labelBox.setTranslateX(rectangle.getLayoutX() + convertScoreToPos(label.getValue()) - (labelBox.getWidth() / 2f));
                }
            });
        }
    }

    private VBox generateLabelBox(String text) {
        VBox labelBox = new VBox();
        labelBox.setAlignment(Pos.TOP_CENTER);
//        labelBox.setStyle("-fx-background-color: lightblue;");
        labelBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        labelBox.getStyleClass().add(gaugeLabelClass);
        Label l = new Label(text);
        Rectangle labelMarker = new Rectangle(2, rectangle.getHeight());

        labelBox.getChildren().addAll(labelMarker, l);
        return labelBox;
    }

    private void fillRectangle() {
        LinearGradient gradient =  new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        rectangle.setFill(gradient);
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
        if (max < min) {
            pos = rectangle.getWidth() - scaledScore * rectangle.getWidth();
        } else {
            pos = scaledScore * rectangle.getWidth();
        }
        if (pos < 0) pos = 0;
        else pos = Math.min(pos, rectangle.getWidth());
        return pos;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("min", min)
                .append("max", max)
                .append("score", score)
                .append("stopPositions", stopPositions)
                .append("labels", labels)
                .append("stops", stops)
                .append("reverse", reverse)
                .toString();
    }
}
