package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.ExternalVariation;

import java.util.List;

public class ExternalVariationsTableCell<S> extends TableCell<S, List<ExternalVariation>> {

    private final HBox box = new HBox();

    public ExternalVariationsTableCell() {
        box.setSpacing(5);
        box.setMinWidth(USE_COMPUTED_SIZE);
        box.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(List<ExternalVariation> item, boolean empty) {
        super.updateItem(item, empty);
        box.getChildren().clear();
        if (empty || item == null || item.isEmpty()) {
            setGraphic(null);
        }else {
            for (ExternalVariation ev : item) {
                box.getChildren().add(createNode(ev));
            }
            setGraphic(box);
        }

    }

    private Label createNode(ExternalVariation v) {
        Label label = new Label(v.getId());
        label.getStyleClass().add("hyperlink-label");
        label.setOnMouseClicked(e -> {
            String url = v.getURL();
            if (url != null) {
                App.get().getHostServices().showDocument(url);
            }
        });
        return label;
    }
}
