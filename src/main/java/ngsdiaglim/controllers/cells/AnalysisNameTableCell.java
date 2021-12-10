package ngsdiaglim.controllers.cells;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.Analysis;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class AnalysisNameTableCell extends TableCell<Analysis, String>  {

    private final Tooltip tooltip;

    public AnalysisNameTableCell() {
        tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);

    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        tooltip.setText(null);
        setTooltip(null);
        if (item != null && !item.isEmpty()) {
            setTooltip(tooltip);
            setText(item);
            tooltip.setText(item);
            Analysis analysis = getTableRow().getItem();
            if (analysis != null) {
                File vcfFile = analysis.getVcfFile();
                File bamFile = analysis.getBamFile();
                File depthFile = analysis.getDepthFile();

                State state = State.VALID;
                List<String> messages = new ArrayList<>();
                if (bamFile != null && !bamFile.exists()) {
                    messages.add(App.getBundle().getString("home.module.analyseslist.msg.err.bamFileIsMissing"));
                    state = State.WARNING;
                }
                if (depthFile != null && !depthFile.exists()) {
                    messages.add(App.getBundle().getString("home.module.analyseslist.msg.err.depthFileIsMissing"));
                    state = State.WARNING;
                }
                if (!vcfFile.exists()) {
                    messages.add(App.getBundle().getString("home.module.analyseslist.msg.err.vcfFileIsMissing"));
                    state = State.ERROR;
                }

                if (!state.equals(State.VALID)) {
                    FontIcon icon;
                    if (state.equals(State.WARNING)) {
                        icon = new FontIcon("mdmz-warning");
                        icon.setIconColor(Color.ORANGE);
                    } else {
                        icon = new FontIcon("mdal-error");
                        icon.setIconColor(Color.RED);
                    }
                    icon.setIconSize(18);

                    StringJoiner stringJoiner = new StringJoiner("\n");
                    for (String m : messages) {
                        stringJoiner.add(m);
                    }
                    Tooltip errTp = new Tooltip(stringJoiner.toString());
                    errTp.setShowDelay(Duration.ZERO);
                    errTp.setStyle("-fx-font-size: 13px;");

                    Tooltip.install(icon, errTp);

                    setGraphic(icon);
                }
            }
        }
    }

    private enum State {
        VALID, WARNING, ERROR;
    }
}
