package ngsdiaglim.controllers.charts;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.stats.Quartiles;
import ngsdiaglim.utils.NumberUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ViolinChartRun extends VBox {

    private final Pane pane = new Pane();
    private final Group violinGroup = new Group();
    private final LinkedHashMap<String, CNVSample> cnvSamples;
    private final double chartSpacing = 10d;
    private double chartHeight;
    private Double minValue = null;
    private Double maxValue = null;
    private int maxDensity = 0;
    private double tickWidth = 9;
    private double boxplotWidth = 100;
    private double boxWidth = 21;
    private double tickSpacing;

    private Font tickFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 11);
    private Font sampleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf"), 11);

    private final SimpleObjectProperty<CNVSample> selectedSample = new SimpleObjectProperty<>();

    public ViolinChartRun(LinkedHashMap<String, CNVSample> cnvSamples, double chartHeight) {
        this.cnvSamples = cnvSamples;
        this.chartHeight = chartHeight;
        defineBoundary();
        this.setPadding(new Insets(30, 0, 0, 50));
        this.getChildren().setAll(pane);
        tickSpacing = calculateTickSpacing();
    }

    /** get the minimum and maximum values of the quartiles for boxplot boundaries
     *
     */
    private void defineBoundary() {
        for (CNVSample cnvSample: cnvSamples.values()) {
            if (cnvSample.getBoxplotData() != null) {
                if (minValue == null || cnvSample.getBoxplotData().getQuartiles().getPl() < minValue) {
                    minValue = cnvSample.getBoxplotData().getQuartiles().getPl();
                }
                if (maxValue == null || cnvSample.getBoxplotData().getQuartiles().getPr() > maxValue) {
                    maxValue = cnvSample.getBoxplotData().getQuartiles().getPr();
                }
            }
        }
        if (minValue == null) minValue = 0d;
        if (maxValue == null) maxValue = 20000d;
    }

    public void drawBoxPlot() {

        Line yaxis = new Line(snap(0), valueToPixel(minValue) + 5 , snap(0), valueToPixel(maxValue) - 10);
        violinGroup.getChildren().add(yaxis);

        // yaxis ticks
        for (double i = minValue; i <= maxValue + tickSpacing; i++) {
            if (i % tickSpacing == 0) {
                Line tick = new Line();
                tick.setFill(Color.BLACK);
                tick.setStartX(snap(-3));
                tick.setStartY(valueToPixel(i));
                tick.setEndX(snap(0));
                tick.setEndY(valueToPixel(i));

                Text t = new Text(String.valueOf((int) i));
                t.getStyleClass().add("violin-chart-y-tick");
                t.setFont(tickFont);
                t.setX(0 - 5 - t.getLayoutBounds().getWidth());
                t.setY(valueToPixel(i) + t.getLayoutBounds().getHeight() / 4.0);

                violinGroup.getChildren().addAll(tick, t);
            }
        }

        Line xaxis = new Line(snap(0), snap(valueToPixel(minValue) + 5), (cnvSamples.size() + 1) * boxplotWidth,  snap(valueToPixel(minValue) + 5));
        violinGroup.getChildren().add(xaxis);

        double xPos = boxplotWidth + .5;
        int sampleIdx = 0;

        // get maximum density for comparable width violin
        for (String sampleName : cnvSamples.keySet()) {
            if (cnvSamples.get(sampleName).getBoxplotData() != null) {
                List<Integer> values = cnvSamples.get(sampleName).getBoxplotData().getValues();
                HashMap<Integer, Integer> valuesBin = new HashMap<>();
                for (Integer v : values) {
                    int bin = v / (int) tickSpacing;
                    valuesBin.putIfAbsent(bin, 0);
                    int density = valuesBin.get(bin) + 1;
                    valuesBin.put(bin, density);
                    maxDensity = Math.max(maxDensity, density);
                }
            }
        }

        for (String sampleName : cnvSamples.keySet()) {
            CNVSample cnvSample = cnvSamples.get(sampleName);

            Group group = new Group();
            if (cnvSample.getBoxplotData() != null) {

                Quartiles quartiles = cnvSample.getBoxplotData().getQuartiles();

                // Rectangle for sample click selection
                Rectangle rectangleClickHandler = new Rectangle();
                rectangleClickHandler.setFill(Color.TRANSPARENT);
                rectangleClickHandler.setX(snap(xPos - boxplotWidth / 2.0));
                rectangleClickHandler.setY(valueToPixel(maxValue) - 3);
                rectangleClickHandler.setWidth(snap(rectangleClickHandler.getX(), boxplotWidth));
                rectangleClickHandler.setHeight(snap(rectangleClickHandler.getY(), valueToPixel(minValue) - valueToPixel(maxValue) + 6));

                group.setOnMouseEntered(e -> rectangleClickHandler.setFill(Color.valueOf("baded7ff")));
                group.setOnMouseExited(e -> setSelectionRectangleColor(rectangleClickHandler, cnvSample));
                group.setOnMouseClicked(e -> selectedSample.setValue(cnvSample));

                selectedSampleProperty().addListener((obs, oldV, newV) -> setSelectionRectangleColor(rectangleClickHandler, cnvSample));

                group.getChildren().addAll(rectangleClickHandler);


                // violin
                List<Integer> values = cnvSample.getBoxplotData().getValues();
                HashMap<Integer, Integer> valuesBin = new HashMap<>();
                for (Integer v : values) {
                    int bin = v / (int)tickSpacing;
                    valuesBin.putIfAbsent(bin, 0);
                    int density = valuesBin.get(bin) + 1;
                    valuesBin.put(bin, density);
                }

                double finalXPos = xPos;
                Polygon violin = new Polygon();
                violin.setFill(ColorsList.getColor(sampleIdx).brighter());
                violin.setStroke(ColorsList.getColor(sampleIdx).darker());
                violin.getPoints().add(finalXPos + xValueToPixel(0));
                violin.getPoints().add(valueToPixel(0));

//                int finalMaxDensity = maxDensity;
                valuesBin.keySet().stream().sorted().forEach(bin -> {
                    violin.getPoints().add(finalXPos + xValueToPixel(valuesBin.get(bin)));
                    double binValue = bin *tickSpacing;
                    if (binValue > maxValue) {
                        binValue = maxValue.intValue();
                    }
                    violin.getPoints().add(valueToPixel(binValue));

                });

                valuesBin.keySet().stream().sorted(Comparator.reverseOrder()).forEach(bin -> {
                    violin.getPoints().add(finalXPos - xValueToPixel(valuesBin.get(bin)));
                    double binValue = bin *tickSpacing;
                    if (binValue > maxValue) {
                        binValue = maxValue.intValue();
                    }
                    violin.getPoints().add(valueToPixel(binValue));
                });

                violin.getPoints().add(finalXPos + xValueToPixel(0));
                violin.getPoints().add(valueToPixel(0));

                group.getChildren().add(violin);

                // fence vertical line
                Line line = new Line();
                line.setStrokeWidth(2);
                line.setStroke(Color.web("#2d2d2d"));
                line.setStartX(snap(xPos));
                line.setStartY(valueToPixel(quartiles.getPr()));
                line.setEndX(snap(xPos));
                line.setEndY(valueToPixel(quartiles.getPl()));
                line.setStrokeType(StrokeType.CENTERED);
                line.setSmooth(true);
                group.getChildren().add(line);

                // box
                Rectangle box = new Rectangle();
                box.setFill(ColorsList.getColor(sampleIdx));
                box.setStroke(ColorsList.getColor(sampleIdx).darker());
                box.setWidth(snap(xPos, boxWidth));
                box.setHeight(valueToPixel(quartiles.getQ1()) - valueToPixel(quartiles.getQ3()));
                box.setX(snap(xPos - boxWidth / 2.0));
                box.setY(valueToPixel(quartiles.getQ3()));
                setMouseTransparent(box);
                group.getChildren().add(box);

                // median
                Circle medianCircle = new Circle();
                medianCircle.setFill(Color.BLACK);
                medianCircle.setCenterX(xPos);
                medianCircle.setCenterY(snap(valueToPixel(quartiles.getQ2())));
                medianCircle.setRadius(5);
                medianCircle.setFill(Color.WHITE);
                group.getChildren().add(medianCircle);


            }
            else {
                // data deleted
                Text deletedText = new Text("Deleted sample");
                deletedText.setFont(sampleFont);
                deletedText.setX(xPos - deletedText.getLayoutBounds().getWidth() + boxWidth / 2.0);
                deletedText.setY(valueToPixel(minValue) - deletedText.getLayoutBounds().getWidth());
                Rotate rotate = new Rotate();
                rotate.setPivotX(deletedText.getX() + deletedText.getLayoutBounds().getWidth());
                rotate.setPivotY(deletedText.getY());
                rotate.setAngle(-90);
                deletedText.getTransforms().add(rotate);
                group.getChildren().add(deletedText);
            }

            // name
            Text l = new Text(cnvSample.getBarcode());
            l.setFont(sampleFont);
            l.getStyleClass().add("violin-chart-x-tick");
            l.applyCss();
            l.setX(xPos - l.getLayoutBounds().getWidth() );
            l.setY(valueToPixel(minValue) + 20);
            l.setFont(sampleFont);
            Rotate rotate = new Rotate();
            rotate.setPivotX(l.getX() + l.getLayoutBounds().getWidth());
            rotate.setPivotY(l.getY() - l.getLayoutBounds().getHeight());
            rotate.setAngle(-45);
            l.getTransforms().add(rotate);

            group.getChildren().add(l);
            violinGroup.getChildren().add(group);
            xPos += boxplotWidth + chartSpacing;

            sampleIdx++;
        }

        pane.getChildren().setAll(violinGroup);
    }

    private double valueToPixel(double value) {
        double size = maxValue - minValue + 1;
        return snap(chartHeight - chartHeight * value / size);
    }

    private double xValueToPixel(double value) {
        return snap(value * (boxplotWidth / 2) / maxDensity);
    }

    private double snap(double v) {
        return ((int) v) + .5;
    }

    private double snap(double pos, double v) {
        if ((v + pos % 1) == 0) {
            return snap(v);
        }
        return v;
    }



    public Double getMinValue() { return minValue; }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() { return maxValue; }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public double getChartHeight() { return chartHeight; }

    public void setHeight(double chartHeight) {
        this.chartHeight = chartHeight;
    }

    public double getTickWidth() { return tickWidth; }

    public void setTickWidth(double tickWidth) {
        this.tickWidth = tickWidth;
    }

    public double getBoxplotWidth() { return boxplotWidth; }

    public void setBoxplotWidth(double boxplotWidth) {
        this.boxplotWidth = boxplotWidth;
    }

    public double getBoxWidth() { return boxWidth; }

    public void setBoxWidth(double boxWidth) {
        this.boxWidth = boxWidth;
    }

    public double getTickSpacing() { return tickSpacing; }

    public void setTickSpacing(double tickSpacing) {
        this.tickSpacing = tickSpacing;
    }

    public Font getTickFont() { return tickFont; }

    public void setTickFont(Font tickFont) {
        this.tickFont = tickFont;
    }

    public Font getSampleFont() { return sampleFont; }

    public void setSampleFont(Font sampleFont) {
        this.sampleFont = sampleFont;
    }

    public CNVSample getSelectedSample() {
        return selectedSample.get();
    }

    public SimpleObjectProperty<CNVSample> selectedSampleProperty() {
        return selectedSample;
    }

    public void setSelectedSample(CNVSample selectedSample) {
        this.selectedSample.set(selectedSample);
    }

    public void selectSample(CNVSample cnvSample) {
        if (cnvSamples.containsKey(cnvSample.getBarcode())) {
            selectedSample.setValue(cnvSamples.get(cnvSample.getBarcode()));
        }
    }

    public void selectSample(int index) {
        if (index < cnvSamples.values().size()) {
            selectedSample.setValue((CNVSample)cnvSamples.values().toArray()[index]);
        }
    }

    private int calculateTickSpacing() {
        int valuesLength = (int) (maxValue - minValue);
        int tickSpacing = valuesLength / 10;
        double factor = 1.0;
        for (int i = 0; i < String.valueOf(tickSpacing).length() - 1; i++) {
            factor *= 10;
        }
        return (int) (NumberUtils.round(tickSpacing / factor, 0) * factor);
    }

    private void setMouseTransparent(Node... nodes) {
        for (Node n : nodes) {
            n.setMouseTransparent(true);
        }
    }

    private void setSelectionRectangleColor(Rectangle r, CNVSample sample) {

        if (selectedSample.get() != null && selectedSample.get().equals(sample)) {
            r.setFill(Color.valueOf("53ac9aff"));
        }
        else {
            r.setFill(Color.TRANSPARENT);
        }
    }
}
