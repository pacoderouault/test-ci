package ngsdiaglim.controllers.charts;

import javafx.scene.paint.Color;

public class PredictionChartItem {
    private String name;
    private String subtitle;
    private String tooltipText;
    private double value;
    private Color color;

    public PredictionChartItem(String name, String subtitle, String tooltipText, double value, Color color) {
        this.name = name;
        this.subtitle = subtitle;
        this.tooltipText = tooltipText;
        this.value = value;
        this.color = color;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() { return subtitle; }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTooltipText() { return tooltipText; }

    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

    public double getValue() { return value; }

    public void setValue(double value) {
        this.value = value;
    }

    public Color getColor() { return color; }

    public void setColor(Color color) {
        this.color = color;
    }
}
