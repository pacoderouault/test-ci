package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;

public abstract class PredictionPopupFilter2 extends TableColumnPopupFilter2<Annotation, VariantPrediction> {

    public PredictionPopupFilter2(FilterTableColumn<Annotation, VariantPrediction> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new PredictionPopupFilterSkin2(this);
    }

    protected abstract void updatePredicate(Operators op, Number score);
}