package ngsdiaglim.controllers.analysisview.ciq;

import de.gsi.chart.axes.Axis;
import de.gsi.chart.plugins.YValueIndicator;

public class TargetVAFIndicator extends YValueIndicator  {

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
}
