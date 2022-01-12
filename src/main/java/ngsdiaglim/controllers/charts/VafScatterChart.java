package ngsdiaglim.controllers.charts;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.SearchVariantResult;

import java.util.List;

public class VafScatterChart extends AnchorPane {

    private final Canvas canvasBg;
    private final Canvas canvasData;

    private final GraphicsContext gcBg;
    private final GraphicsContext gcData;

    private final double canvasPadding = 10.0;
    private final double canvasHeight;
    private final double canvasWidth;
    private final double halfFontHeight = 3.5;
    private final double yLegendWidth = 30.0;
    private final double dotRadius = 5;

    private final Font robotoRegelar = Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);

    private Annotation annotation;
    private List<SearchVariantResult> similarVariants;

    public VafScatterChart(double width, double height) {
        canvasBg = new Canvas(width, height);
        canvasData = new Canvas(width, height);
        gcBg = canvasBg.getGraphicsContext2D();
        gcData = canvasData.getGraphicsContext2D();

        canvasHeight = canvasBg.getHeight() - (canvasPadding * 2);
        canvasWidth = canvasBg.getWidth() - (canvasPadding * 2);

        getChildren().addAll(canvasBg, canvasData);

        drawBgLayer();
    }

    private void drawBgLayer() {

        gcBg.setFont(robotoRegelar);
        gcBg.setStroke(Color.valueOf("#696969"));
        gcBg.setFill(Color.valueOf("#696969"));
        gcBg.fillText("1.0", canvasPadding, getYPos(1) + halfFontHeight);
        gcBg.fillText("0.5", canvasPadding, getYPos(0.5) + halfFontHeight);
        gcBg.fillText("0.0", canvasPadding, getYPos(0) + halfFontHeight);

        gcBg.setLineWidth(0.5);
        gcBg.strokeLine(canvasPadding + yLegendWidth + 0.5, getYPos(1), canvasPadding + yLegendWidth + 0.5, getYPos(0) );
        gcBg.strokeLine(canvasPadding + yLegendWidth, getYPos(0), canvasWidth, getYPos(0) );

        // Ticks on y axis
        for (float i = 0.0f; i <= 1.0; i += 0.1) {
            gcBg.strokeLine(canvasPadding + yLegendWidth - 3, getYPos(i), canvasPadding + yLegendWidth, getYPos(i) );
        }


        gcBg.setStroke(Color.valueOf("dbdbdb"));
        gcBg.strokeLine(canvasPadding + yLegendWidth, getYPos(0), canvasWidth, getYPos(0) );
        gcBg.strokeLine(canvasPadding + yLegendWidth, getYPos(0.5), canvasWidth, getYPos(0.5) );
        gcBg.strokeLine(canvasPadding + yLegendWidth, getYPos(1), canvasWidth, getYPos(1) );
        gcBg.strokeLine(((canvasWidth - canvasPadding *2) / 2.0) + yLegendWidth, getYPos(0), ((canvasWidth - canvasPadding *2) / 2.0) + yLegendWidth, getYPos(1));

    }


    public void setVariant(Annotation annotation, List<SearchVariantResult> similarVariants) {
        this.annotation = annotation;
        this.similarVariants = similarVariants;
        drawDataLayer();
    }


    public void drawDataLayer() {
        gcData.clearRect(0, 0, canvasData.getWidth(), canvasData.getHeight());
        double space = (canvasWidth - yLegendWidth - canvasPadding * 2) / (similarVariants.size()*1.0);
        double x1 = space;

        gcData.setFill(Color.valueOf("737373"));
        boolean vafPrinted = false;
        for (SearchVariantResult r : similarVariants) {
            float vaf = r.getVaf();
            double vafX = x1 - (space / 2) + yLegendWidth + canvasPadding + dotRadius / 2.0;
            x1 += space;

            if (!vafPrinted && vaf == annotation.getVaf()) {
                gcData.setFill(Color.ORANGE);
                gcData.fillOval(vafX, getYPos(vaf) - dotRadius / 2.0, dotRadius, dotRadius);
                gcData.setFill(Color.valueOf("737373"));
                vafPrinted = true;
            }
            else {
                gcData.fillOval(vafX, getYPos(vaf) - dotRadius / 2.0, dotRadius, dotRadius);
            }
        }
    }

    private double getYPos(double v) {
        return ((1.0 - v) * canvasHeight) + canvasPadding - halfFontHeight;
    }
}
