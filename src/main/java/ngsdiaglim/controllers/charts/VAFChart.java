package ngsdiaglim.controllers.charts;

import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class VAFChart extends Region {

    private GraphicsContext ctx;
    private final SimpleFloatProperty vafValue = new SimpleFloatProperty();
    private final int size;
    private final int height;
    private final double center;
    private final double width;
    private final double arcWidth;
    private final double textYMargin;
    private final Font robotoMedium;
    private final Font robotoLight;

    public VAFChart() {
        this.size = 120;
        height = size / 2 - 5;
        center = size * 0.5;
        width = center;
        arcWidth = width / 3;
        textYMargin = 18;

        robotoMedium = Font.loadFont(getClass().getResourceAsStream("fonts/Roboto-Medium.ttf"), 14);
        robotoLight = Font.loadFont(getClass().getResourceAsStream("fonts/Roboto-Light.ttf"), 11);

        initGraphics();

        vafValue.addListener((obs, oldV, newV) -> drawChart());
    }

    private void initGraphics() {

        Canvas canvas = new Canvas(size, height);

        ctx = canvas.getGraphicsContext2D();

        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.CENTER);

        Pane pane = new Pane(canvas);
        getChildren().setAll(pane);

        drawChart();
    }


    private void drawChart() {

        ctx.clearRect(0, 0, size, height);
        ctx.setStroke(Color.web("#dcdcdc"));
        ctx.setLineWidth(arcWidth);

        double startX = width / 2;
        double startY = width / 2 - 20;

        ctx.strokeArc(startX, startY, width, width, 0.0, 180.0, ArcType.OPEN);

        ctx.setFill(Color.BLACK);
        ctx.setFont(Font.font(12));

        double textY = width / 2 + textYMargin;
        ctx.setFont(robotoLight);
        ctx.fillText("VAF", width, textY);
        ctx.fillText("0", width / 2, textY);
        ctx.fillText("1", width * 1.5, textY);

        ctx.setFont(robotoMedium);

        String decimalFormat = "%.3f";
        if (vafValue.get() <= 0.1) {
            decimalFormat = "%.4f";
        }
        ctx.fillText(String.format(java.util.Locale.US, decimalFormat, vafValue.get()), width, width / 2 + 5);

        if (vafValue.getValue() != null) {
            ctx.setStroke(Color.web("#00a5d7"));
            double arcExt = -180 * vafValue.get();
            ctx.strokeArc(startX, startY, width, width, 180, arcExt, ArcType.OPEN);
        }
    }

    public void setVaf(float vaf) {
        vafValue.setValue(vaf);
    }

    public float getVafValue() {
        return vafValue.get();
    }

    public SimpleFloatProperty vafValueProperty() {
        return vafValue;
    }
}
