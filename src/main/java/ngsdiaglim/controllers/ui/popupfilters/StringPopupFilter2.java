package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.modeles.variants.Annotation;

public abstract class StringPopupFilter2 extends TableColumnPopupFilter2<Annotation, String> {

    public StringPopupFilter2(FilterTableColumn<Annotation, String> tableColumn) {
        super(tableColumn);
    }


    @Override
    protected Skin<?> createDefaultSkin() {
        return new StringPopupFilterSkin2(this);
    }

}
