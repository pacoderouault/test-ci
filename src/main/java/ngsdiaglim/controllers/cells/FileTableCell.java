package ngsdiaglim.controllers.cells;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;


public class FileTableCell<T> extends TableCell<T, File> {

    private final Tooltip fileNameTooltip = new Tooltip();
    private final boolean modifiable;

    public FileTableCell(boolean modifiable) {
        this.modifiable = modifiable;
        setContentDisplay(ContentDisplay.RIGHT);
        fileNameTooltip.setShowDelay(Duration.ZERO);
    }

    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
            setTooltip(null);
        } else {
            setText(item.getName());
            if (modifiable) {
                HBox box = new HBox();
                box.getStyleClass().add("box-action-cell");

                Button reloadBtn = new Button("", new FontIcon("mdal-insert_drive_file"));
                reloadBtn.setOnAction(e -> openFile());
                setGraphic(reloadBtn);

                Button removeBtn = new Button("", new FontIcon("mdal-cancel"));
                removeBtn.setOnAction(e -> removeFile());

                box.getChildren().addAll(reloadBtn, removeBtn);
                setTooltip(fileNameTooltip);
                fileNameTooltip.setText(item.getName());
                setGraphic(box);
            }
            else {
                setGraphic(null);
            }
        }
    }

    protected void openFile() {};

    protected void removeFile() {};

}
