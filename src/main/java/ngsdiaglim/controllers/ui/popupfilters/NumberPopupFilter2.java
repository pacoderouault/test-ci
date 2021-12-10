package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.modeles.variants.Annotation;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public abstract class NumberPopupFilter2 extends TableColumnPopupFilter2<Annotation, Number> {

    public NumberPopupFilter2(FilterTableColumn<Annotation, Number> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new NumberPopupFilterSkin2(this);
    }
}
