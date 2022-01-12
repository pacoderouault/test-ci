package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.modeles.variants.Annotation;

public abstract class NumberPopupFilter extends TableColumnPopupFilter<Annotation, Number> {

    public NumberPopupFilter(FilterTableColumn<Annotation, Number> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new NumberPopupFilterSkin(this);
    }
}
