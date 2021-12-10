package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.variants.Annotation;

public class ACMGTableCell extends TableCell<Annotation, ACMG> {

    @Override
    protected void updateItem(ACMG item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || getTableRow() == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
        }
    }
}
