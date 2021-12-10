package ngsdiaglim.controllers.analysisview.cnv;

import de.gsi.chart.axes.Axis;
import de.gsi.chart.plugins.AbstractRangeValueIndicator;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import ngsdiaglim.cnv.CNV;
import ngsdiaglim.enumerations.CNVTypes;

public class CNVRangeIndicator extends AbstractRangeValueIndicator {

    private final CNV cnv;
    private final Polygon hat = new Polygon();
    private final static double topMargin = 10;
    private final static double hatWidth = 10;
    private final static double hatHeight = 20;

    /**
     * Creates a new instance of the indicator.
     *
     * @param axis       the axis this indicator is associated with
     * @param lowerBound lower bound (min value) of the range
     * @param upperBound upper bound (max value) of the range
     */
    protected CNVRangeIndicator(Axis axis, double lowerBound, double upperBound, CNV cnv) {
        super(axis, lowerBound, upperBound, null);
        this.cnv = cnv;
        hat.fillProperty().bind(rectangle.fillProperty());
        if (cnv.getCnvTypes().equals(CNVTypes.DELETION)) {
//            rectangle.setFill(CNVChart.deletionColor);
            rectangle.getStyleClass().add("deltest");
        }
        else if (cnv.getCnvTypes().equals(CNVTypes.DUPLICATION)) {
//            rectangle.setFill(CNVChart.duplicationColor);
            rectangle.getStyleClass().add("duptest");
        }
        hat.setOnMouseClicked(e -> {
            axis.setAutoRanging(false);
            axis.set(lowerBound - 10, upperBound + 10);
            axis.forceRedraw();
        });
    }

    @Override
    public void layoutChildren() {
        if (getChart() == null) {
            return;
        }
        final Bounds plotAreaBounds = getChart().getCanvas().getBoundsInLocal();
        final double minX = plotAreaBounds.getMinX();
        final double maxX = plotAreaBounds.getMaxX();
        final double minY = plotAreaBounds.getMinY();
        final double maxY = plotAreaBounds.getMaxY();

        final Axis xAxis = getAxis();
        final double value1 = xAxis.getDisplayPosition(getLowerBound());
        final double value2 = xAxis.getDisplayPosition(getUpperBound());

        final double startX = Math.max(minX, minX + Math.min(value1, value2));
        final double endX = Math.min(maxX, minX + Math.max(value1, value2));

        layout(new BoundingBox(startX, minY, endX - startX, maxY - minY));
    }

    @Override
    public void updateStyleClass() {
        setStyleClasses(label, "x-", AbstractRangeValueIndicator.STYLE_CLASS_LABEL);
//        setStyleClasses(rectangle, "x-", AbstractRangeValueIndicator.STYLE_CLASS_RECT);
    }

    @Override
    protected void layout(final Bounds bounds) {
        if (bounds.intersects(getChart().getCanvas().getBoundsInLocal())) {
            layoutLabel(bounds, getLabelHorizontalPosition(), getLabelVerticalPosition());
            rectangle.setX(bounds.getMinX());
            rectangle.setY(bounds.getMinY() + topMargin);
            rectangle.setWidth(bounds.getWidth());
            rectangle.setHeight(bounds.getHeight());

            // left hat
            hat.getPoints().setAll(
                    bounds.getMinX() - hatWidth, bounds.getMinY() + topMargin,
                    bounds.getMinX() + bounds.getWidth() + hatWidth, bounds.getMinY() + topMargin,
                    bounds.getMinX() + bounds.getWidth() - 1, bounds.getMinY() + hatHeight + topMargin,
                    bounds.getMinX() + 1, bounds.getMinY() + hatHeight + topMargin,
                    bounds.getMinX() - hatWidth, bounds.getMinY() + topMargin
            );

            addChildNodeIfNotPresent(hat);
            addChildNodeIfNotPresent(rectangle);
        } else {
            getChartChildren().clear();
        }
    }
}
