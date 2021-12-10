package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public class PredictionPopupFilter extends TableColumnPopupFilter<Annotation, VariantPrediction> {

    public PredictionPopupFilter(FilteredTableColumn<Annotation, VariantPrediction> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new PredictionPopupFilterSkin(this);
    }
}
