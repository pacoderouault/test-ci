package ngsdiaglim.controllers.charts;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ngsdiaglim.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class PredictionGauge extends Region {

    private final double min;
    private final double max;
    private boolean reverse;
    private final Rectangle rectangle = new Rectangle(100, 10);
    private Stop[] stops;
    private final List<Double> stopPositions;
    private final List<PredictionGaugeLabel> labels;
    private final static String valueLabelClass = "predgauge-value-label";
    private final static String markerLabelClass = "predgauge-marker-label";
    private final static String markerClass = "predgauge-marker";
    private Group gaugePane = new Group();
    private VBox markerBox = new VBox();
    private Label markerLabel = new Label("0.5");
    private final SimpleObjectProperty<Float> score = new SimpleObjectProperty<>(null);

    public PredictionGauge(double min, double max, boolean reverse, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
        this.min = min;
        this.max = max;
        this.stopPositions = stopPositions;
        this.labels = labels;
        this.reverse = reverse;
        init();
    }
    public PredictionGauge(double min, double max, List<Double> stopPositions, List<PredictionGaugeLabel> labels) {
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

        fillRectangle();



        Polygon marker = new Polygon();
        marker.getPoints().setAll(
                0d, 0d,
                10d, 0d,
                5d, 5d
        );
        markerBox.setAlignment(Pos.TOP_CENTER);
        markerBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        markerBox.getChildren().addAll(markerLabel, marker);

        getChildren().addAll(leftLabel, rectangle, markerBox, rightLabel);
        applyCss();
        layout();
        markerBox.setLayoutY(rectangle.getLayoutY() - markerBox.getHeight());
        markerBox.setStyle("-fx-background-color: red;");
        HBox baseBox = new HBox();
        baseBox.setAlignment(Pos.CENTER_LEFT);
        baseBox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);
        getChildren().add(gaugePane);

        getChildren().addAll(baseBox);

        Platform.runLater(() -> {
            applyCss();
            layout();

            rectangle.setLayoutX(leftLabel.getWidth() + 3);
            applyCss();
            layout();
            markerBox.setLayoutY(rectangle.getLayoutY() - markerBox.getHeight());
            markerBox.setLayoutX(rectangle.getLayoutX() - markerBox.getWidth() / 2f);
            applyCss();
            layout();
            rightLabel.setLayoutX(rectangle.getLayoutX() + rectangle.getWidth() + 3);
            applyCss();
            layout();
            leftLabel.setLayoutY(rectangle.getLayoutY() + rectangle.getHeight() / 2f - leftLabel.getHeight() / 2f);
            rightLabel.setLayoutY(rectangle.getLayoutY() + rectangle.getHeight() / 2f - rightLabel.getHeight() / 2f);

            generateLabels();
        });

        score.addListener((obs, oldV, newV) -> {
            setMarkerPosition();
        });
        visibleProperty().bind(score.isNotNull());

        Platform.runLater(() -> {
            double s = Double.sum(min, max) / 2d;
            setScore((float) s);
        });
    }


    private void fillRectangle() {

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

        LinearGradient gradient =  new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        rectangle.setFill(gradient);
    }


    private void generateLabels() {
        if (labels != null) {
            Platform.runLater(() -> {
                for (PredictionGaugeLabel label : labels) {
                    VBox labelBox = generateLabelBox(label.getText());
                    getChildren().add(labelBox);
                    applyCss();
                    layout();
                    labelBox.setLayoutY(rectangle.getLayoutY());
                    labelBox.setLayoutX(rectangle.getLayoutX() + convertScoreToPos(label.getValue()) - (labelBox.getWidth() / 2f));
                }
            });
        }
    }

    private VBox generateLabelBox(String text) {
        VBox labelBox = new VBox();
        labelBox.setAlignment(Pos.TOP_CENTER);
//        labelBox.setStyle("-fx-background-color: lightblue;");
        labelBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        Label l = new Label(text);
        Rectangle labelMarker = new Rectangle(2, rectangle.getHeight());

        labelBox.getChildren().addAll(labelMarker, l);
        return labelBox;
    }

    public void setScore(Float score) {
        this.score.set(score);
    }

    private void setMarkerPosition() {
        if (score.get() != null) {
            markerLabel.setText(String.valueOf(score.get()));
            applyCss();
            layout();
            markerBox.setLayoutX(rectangle.getLayoutX() + convertScoreToPos(score.get()) - markerBox.getWidth() / 2f);
        }
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
        if (reverse) {
            pos = rectangle.getWidth() - scaledScore * rectangle.getWidth();
        } else {
            pos = scaledScore * rectangle.getWidth();
        }
        if (pos < 0) pos = 0;
        else pos = Math.min(pos, rectangle.getWidth());
        return pos;
    }


}
