package ngsdiaglim.controllers.cells.ciq;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import org.kordamp.ikonli.javafx.FontIcon;

public class AddCIQHotspotActionsCell extends TableCell<CIQHotspot, Void> {

    private final HBox box = new HBox();

    public AddCIQHotspotActionsCell() {
        box.getStyleClass().add("box-action-cell");
        Button deleteHotspotBtn = new Button("", new FontIcon("mdal-delete_forever"));
        deleteHotspotBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(deleteHotspotBtn);
        deleteHotspotBtn.setOnAction(e -> deleteHotspotHandler());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(box);
        }
    }

    private void deleteHotspotHandler() {
        CIQHotspot h = getTableRow().getItem();
        if (h != null) {
            getTableView().getItems().remove(h);
        }
    }
}
