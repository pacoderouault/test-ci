package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.modeles.variants.Annotation;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public class ConsequencePopupFilter extends TableColumnPopupFilter<Annotation, EnsemblConsequence> {

    public ConsequencePopupFilter(FilteredTableColumn<Annotation, EnsemblConsequence> tableColumn) {
        super(tableColumn);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new ConsequencePopupFilterSkin(this);
    }
}