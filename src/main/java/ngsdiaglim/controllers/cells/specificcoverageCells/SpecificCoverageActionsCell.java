package ngsdiaglim.controllers.cells.specificcoverageCells;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.Region;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import org.kordamp.ikonli.javafx.FontIcon;

public class SpecificCoverageActionsCell extends TableCell<SpecificCoverage, Void> {

    private final HBox box = new HBox();

    public SpecificCoverageActionsCell() {
        box.getStyleClass().add("box-action-cell");
        final Button removeButton = new Button(null, new FontIcon("mdal-delete_forever"));
        removeButton.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(removeButton);

        removeButton.setOnAction(e -> removeRegion());
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

    private void removeRegion() {
        SpecificCoverage r = getTableRow().getItem();
        if (r != null) {
            getTableView().getItems().remove(r);
            getTableView().refresh();
        }
    }
}
