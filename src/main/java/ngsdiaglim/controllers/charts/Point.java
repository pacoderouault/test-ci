package ngsdiaglim.controllers.charts;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Point extends Point2D {

    private final Color color;

    public Point(double x, double y, Color color) {
        super(x, y);
        this.color = color;
    }

    public Color getColor() { return color; }
}
