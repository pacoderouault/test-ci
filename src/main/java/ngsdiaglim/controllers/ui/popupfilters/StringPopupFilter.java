package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.modeles.variants.Annotation;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public class StringPopupFilter extends TableColumnPopupFilter<Annotation, String> {

    public StringPopupFilter(FilteredTableColumn<Annotation, String> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new StringPopupFilterSkin(this);
    }
}
