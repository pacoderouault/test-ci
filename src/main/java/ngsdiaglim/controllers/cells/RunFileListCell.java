package ngsdiaglim.controllers.cells;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.RunFile;
import org.kordamp.ikonli.javafx.FontIcon;

public class RunFileListCell extends ListCell<RunFile> {

    private final HBox box = new HBox();
    private final Label fileNameLb = new Label();

    public RunFileListCell() {
        FontIcon openIcon = new FontIcon("far-file-alt");
        Button openBtn = new Button("", openIcon);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(fileNameLb, openBtn);
        box.getStyleClass().add("box-action-cell");
        openBtn.setOnAction(e -> openFile());
    }

    @Override
    protected void updateItem(RunFile item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            fileNameLb.setText(item.getFile().getName());

            if (item.getFile() != null && !item.getFile().exists()) {
                fileNameLb.setGraphic(new FontIcon("mdoal-error"));
                Tooltip tp = new Tooltip(App.getBundle().getString("runinfodialog.msg.err.filenotexists"));
                tp.setShowDelay(Duration.ZERO);
                fileNameLb.setTooltip(tp);
            } else {
                fileNameLb.setTooltip(null);
            }

            setGraphic(box);
        }
    }

    private void openFile() {
        RunFile runFile = getItem();
        if (runFile != null && runFile.getFile().exists()) {
            App.get().getHostServices().showDocument(runFile.getFile().getPath());
        }
    }
}
