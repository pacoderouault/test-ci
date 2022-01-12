package ngsdiaglim.controllers.analysisview.cnv;

import de.gsi.chart.plugins.Screenshot;
import javafx.scene.layout.HBox;

public class ScreenShotPlugin extends Screenshot {

    @Override
    public HBox getScreenshotInteractorBar() {
        return new HBox();
    }
}
