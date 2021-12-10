package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import ngsdiaglim.enumerations.EnsemblConsequence;

public class EnsemblConsequenceTableCell<S> extends TableCell<S, EnsemblConsequence> {

    @Override
    protected void updateItem(EnsemblConsequence item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
