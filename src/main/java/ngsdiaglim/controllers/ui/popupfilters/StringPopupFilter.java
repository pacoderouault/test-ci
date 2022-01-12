package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.modeles.variants.Annotation;

public abstract class StringPopupFilter extends TableColumnPopupFilter<Annotation, String> {

    public StringPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
        super(tableColumn);
    }


    @Override
    protected Skin<?> createDefaultSkin() {
        return new StringPopupFilterSkin(this);
    }

}
