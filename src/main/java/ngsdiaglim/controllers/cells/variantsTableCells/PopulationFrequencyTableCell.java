package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.modeles.variants.predictions.GnomADFrequencies;

import java.text.DecimalFormat;

public class PopulationFrequencyTableCell extends TableCell<Annotation, GnomadPopulationFreq> {

    private final static DecimalFormat df = new DecimalFormat("#.###");

    @Override
    protected void updateItem(GnomadPopulationFreq item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.toString());
        }
    }
}
