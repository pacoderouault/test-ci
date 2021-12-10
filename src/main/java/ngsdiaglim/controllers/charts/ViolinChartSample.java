package ngsdiaglim.controllers.charts;

import javafx.beans.property.SimpleStringProperty;
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
import ngsdiaglim.cnv.BoxplotData;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.stats.Quartiles;
import ngsdiaglim.utils.NumberUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ViolinChartSample extends VBox {

    private final Pane pane = new Pane();
    private final CNVSample cnvSample;
    private final double chartSpacing = 10d;
    private double chartHeight;
    private Double minValue = null;
    private Double maxValue = null;
    private int maxDensity = 0;
    private double tickWidth = 9;
    private double boxplotWidth = 100;
    private double boxWidth = 21;
    private double tickSpacing;
    private double binSize;

    private Font tickFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 11);
    private Font sampleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf"), 11);

    private final SimpleStringProperty selectedPool = new SimpleStringProperty();

    public ViolinChartSample(CNVSample cnvSample, double chartHeight) {
        this.cnvSample = cnvSample;
        this.chartHeight = chartHeight;
        defineBoundary();
        this.setPadding(new Insets(30, 0, 0, 50));
        this.getChildren().setAll(pane);
        calculateTickSpacing();
    }

    /** get the minimum and maximum values of the quartiles for boxplot boundaries
     *
     */
    private void defineBoundary() {
        for (BoxplotData boxPlotData : cnvSample.getBoxplotDatabyPool().values()) {
            if (boxPlotData != null) {
                if (minValue == null || boxPlotData.getQuartiles().getPl() < minValue) {
                    minValue = boxPlotData.getQuartiles().getPl();
                }
                if (maxValue == null || boxPlotData.getQuartiles().getPr() > maxValue) {
                    maxValue = boxPlotData.getQuartiles().getPr();
                }
            }
        }
    }

    public void drawBoxPlot() {

        Line yaxis = new Line(snap(0), valueToPixel(minValue) + 5 , snap(0), valueToPixel(maxValue) - 10);
        pane.getChildren().add(yaxis);

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
                t.setFont(tickFont);
                t.setX(0 - 5 - t.getLayoutBounds().getWidth());
                t.setY(valueToPixel(i) + t.getLayoutBounds().getHeight() / 4.0);
                t.getStyleClass().add("violin-chart-y-tick");
                pane.getChildren().addAll(tick, t);
            }
        }

        Line xaxis = new Line(0, snap(valueToPixel(minValue) + 5), (cnvSample.getBoxplotDatabyPool().size() + 1) * boxplotWidth,  snap(valueToPixel(minValue) + 5));
        pane.getChildren().add(xaxis);

        double xPos = boxplotWidth + .5;
        int sampleIdx = 0;
        for (String poolName : cnvSample.getBoxplotDatabyPool().keySet().stream().sorted().collect(Collectors.toList())) {
            BoxplotData boxplotData = cnvSample.getBoxplotDatabyPool().get(poolName);
            Group group = new Group();
            if (boxplotData != null) {
                Quartiles quartiles = boxplotData.getQuartiles();

                // Rectangle for sample clikc selection
                Rectangle rectangleClickHandler = new Rectangle();
                rectangleClickHandler.setFill(Color.TRANSPARENT);
                rectangleClickHandler.setX(snap(xPos - boxplotWidth / 2.0));
                rectangleClickHandler.setY(valueToPixel(maxValue) - 3);
                rectangleClickHandler.setWidth(snap(rectangleClickHandler.getX(), boxplotWidth));
                rectangleClickHandler.setHeight(snap(rectangleClickHandler.getY(), valueToPixel(minValue) - valueToPixel(maxValue) + 6));
                group.setOnMouseEntered(e -> {
                    rectangleClickHandler.setFill(Color.valueOf("baded7ff"));
                });
                group.setOnMouseExited(e -> {
                    setSelectionRectangleColor(rectangleClickHandler, poolName);
                });
                group.setOnMouseClicked(e -> {
                    selectedPool.setValue(poolName);
                });

                selectedPoolProperty().addListener((obs, oldV, newV) -> {
                    setSelectionRectangleColor(rectangleClickHandler, poolName);
                });
                group.getChildren().addAll(rectangleClickHandler);

                // violin
                List<Integer> values = boxplotData.getValues();
                HashMap<Integer, Integer> valuesBin = new HashMap<>();
                values.forEach(v -> {
                    int bin = v / (int)binSize;
                    valuesBin.putIfAbsent(bin, 0);
                    int density = valuesBin.get(bin) + 1;
                    valuesBin.put(bin, density);
                    maxDensity = Math.max(maxDensity, density);
                });


                double finalXPos = xPos;
                Polygon violin = new Polygon();
                violin.setFill(ColorsList.getColor(sampleIdx).brighter());
                violin.setStroke(ColorsList.getColor(sampleIdx).darker());
                violin.getPoints().add(finalXPos + xValueToPixel(0));
                violin.getPoints().add(valueToPixel(0));

                valuesBin.keySet().stream().sorted().forEach(bin -> {
                    violin.getPoints().add(finalXPos + xValueToPixel(valuesBin.get(bin)));
                    double binValue = bin *binSize;
                    if (binValue > maxValue) {
                        binValue = maxValue.intValue();
                    }
                    violin.getPoints().add(valueToPixel(binValue));

                });

                valuesBin.keySet().stream().sorted(Comparator.reverseOrder()).forEach(bin -> {
                    violin.getPoints().add(finalXPos - xValueToPixel(valuesBin.get(bin)));
                    double binValue = bin *binSize;
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
                line.setFill(Color.BLACK);
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

            } else {
                // data deleted
                Text deletedText = new Text("Deleted pool");
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
            Text l = new Text(poolName);
            l.setFont(sampleFont);
            l.setX(xPos - l.getLayoutBounds().getWidth() );
            l.setY(valueToPixel(minValue) + 20);
            l.setFont(sampleFont);
            l.getStyleClass().add("violin-chart-x-tick");
            Rotate rotate = new Rotate();
            rotate.setPivotX(l.getX() + l.getLayoutBounds().getWidth());
            rotate.setPivotY(l.getY() - l.getLayoutBounds().getHeight());
            rotate.setAngle(-45);
            l.getTransforms().add(rotate);

            group.getChildren().add(l);
            pane.getChildren().add(group);
            xPos += boxplotWidth + chartSpacing;

            sampleIdx++;
        }
    }

    private double xValueToPixel(double value) {
        return snap(value * (boxplotWidth / 2) / maxDensity);
    }

    private double valueToPixel(double value) {
        double size = maxValue - minValue + 1;
        return snap(chartHeight - chartHeight * value / size);
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
        calculateTickSpacing();
    }

    public Double getMaxValue() { return maxValue; }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
        calculateTickSpacing();
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

    public String getSelectedPool() {
        return selectedPool.get();
    }

    public SimpleStringProperty selectedPoolProperty() {
        return selectedPool;
    }

    public void setSelectedPool(String selectedPool) {
        this.selectedPool.set(selectedPool);
    }

    private void calculateTickSpacing() {
        if (maxValue != null && minValue != null) {
            int valuesLength = (int) (maxValue - minValue);
            int tickSpacing = valuesLength / 10;
            double factor = 1.0;
            for (int i = 0; i < String.valueOf(tickSpacing).length() - 1; i++) {
                factor *= 10;
            }
            this.tickSpacing = (int) (NumberUtils.round(tickSpacing / factor, 0) * factor);
            this.binSize = this.tickSpacing;
        }
    }

    private void setMouseTransparent(Node... nodes) {
        for (Node n : nodes) {
            n.setMouseTransparent(true);
        }
    }

    private void setSelectionRectangleColor(Rectangle r, String poolName) {

        if (selectedPool.get() != null && selectedPool.get().equals(poolName)) {
            r.setFill(Color.valueOf("53ac9aff"));
        }
        else {
            r.setFill(Color.TRANSPARENT);
        }
    }
}
