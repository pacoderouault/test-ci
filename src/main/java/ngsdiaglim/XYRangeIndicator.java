package ngsdiaglim;

import de.gsi.chart.axes.Axis;
import de.gsi.chart.axes.spi.CategoryAxis;
import de.gsi.chart.plugins.AbstractRangeValueIndicator;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class XYRangeIndicator extends AbstractRangeValueIndicator {

    private Color color;
    private final double yBound;
    private final double height;

//    public XYRangeIndicator(Axis axis, final double xLowerBound, final double xUpperBound) {
//
//        this(axis, xLowerBound, xUpperBound, null);
//
//        this.yLowerBound = yLowerBound;
//        this.yUpperBound = yUpperBound;
//    }

    /**
     * Creates a new instance of the indicator.
     *
     * @param axis the axis this indicator is associated with
     * @param text the text to be shown by the label. Value of {@link #textProperty()}.
     */
    public XYRangeIndicator(Axis axis, final double xLowerBound, final double xUpperBound, final double yBound, final double height, final String text) {
        super(axis, xLowerBound, xUpperBound, text);

        for (Node n : getChartChildren()) {
            if (n instanceof Rectangle) {
                Rectangle r = (Rectangle) n;
                label.visibleProperty().bind(label.widthProperty().lessThan(r.widthProperty()));
            }
        }
        this.yBound = yBound;
        this.height = height;
    }

    public XYRangeIndicator(Axis xAxis, final double xLowerBound, final double xUpperBound, final double yBound, final double height, String text, Color color) {
        this(xAxis, xLowerBound, xUpperBound, yBound, height, text);
        this.color = color;
        for (Node n : getChartChildren()) {
            if (n instanceof Rectangle) {
                Rectangle r = (Rectangle) n;
                r.setFill(color);
            }
        }
    }

    @Override
    public void layoutChildren() {
        if (getChart() == null) {
            return;
        }
        final Bounds plotAreaBounds = getChart().getCanvas().getBoundsInLocal();
        final double minX = plotAreaBounds.getMinX();
        final double maxX = plotAreaBounds.getMaxX();
        final double minY = plotAreaBounds.getMaxY() - yBound - height;
        final double maxY = height;

        final Axis xAxis = getAxis();
        final double value1 = xAxis.getDisplayPosition(getLowerBound());
        final double value2 = xAxis.getDisplayPosition(getUpperBound());

        final double startX = Math.max(minX, minX + Math.min(value1, value2));
        final double endX = Math.min(maxX, minX + Math.max(value1, value2));

        layout(new BoundingBox(startX, minY, endX - startX, maxY));
    }

    @Override
    public void updateStyleClass() {
        setStyleClasses(label, "x-", AbstractRangeValueIndicator.STYLE_CLASS_LABEL);
//        setStyleClasses(rectangle, "x-", AbstractRangeValueIndicator.STYLE_CLASS_RECT);
    }
}
