package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import ngsdiaglim.enumerations.SangerState;
import ngsdiaglim.modeles.variants.Annotation;

public class SangerStateTableCell extends TableCell<Annotation, SangerState> {

    @Override
    protected void updateItem(SangerState item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty || item == null || getTableRow() == null) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
