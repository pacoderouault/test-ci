package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.modeles.variants.Annotation;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public class NumberPopupFilter extends TableColumnPopupFilter<Annotation, Number> {

    public NumberPopupFilter(FilteredTableColumn<Annotation, Number> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new NumberPopupFilterSkin(this);
    }
}
