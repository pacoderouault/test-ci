package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;

public class VariantPredictionTableCell extends TableCell<Annotation, VariantPrediction> {
    @Override
    protected void updateItem(VariantPrediction item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.toString());
        }
    }
}
