package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;

public class SpliceAIPredsTableCell extends TableCell<Annotation, SpliceAIPredictions> {
    @Override
    protected void updateItem(SpliceAIPredictions item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty || item == null) {
            setText(null);
        } else {
            SpliceAIPredictions.SpliceAIPrediction mostSeverePred = item.getMostSeverePred();
            if (mostSeverePred != null) {
                setText(mostSeverePred.toString());
            }else {
                setText(null);
            }
        }
    }
}
