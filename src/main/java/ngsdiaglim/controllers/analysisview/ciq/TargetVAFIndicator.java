package ngsdiaglim.controllers.analysisview.ciq;

import de.gsi.chart.axes.Axis;
import de.gsi.chart.plugins.AbstractSingleValueIndicator;
import de.gsi.chart.plugins.YValueIndicator;

public class TargetVAFIndicator extends YValueIndicator  {

    protected static final String STYLE_CLASS_LINE = "targetvaf-value-indicator-line";

    public TargetVAFIndicator(Axis axis, double value, String text) {
        super(axis, value, text);
        setEditable(false);
        setLabelPosition(1);
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();
        label.toFront();
    }

    @Override
    public void updateStyleClass() {
        setStyleClasses(label, "y-", AbstractSingleValueIndicator.STYLE_CLASS_LABEL);
        setStyleClasses(line, "y-", STYLE_CLASS_LINE);
        setStyleClasses(triangle, "x-", AbstractSingleValueIndicator.STYLE_CLASS_MARKER);
        label.getStyleClass().add("warning");
    }

}
