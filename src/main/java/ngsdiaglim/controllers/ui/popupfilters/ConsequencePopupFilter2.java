package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.Set;

public class ConsequencePopupFilter2 extends TableColumnPopupFilter2<Annotation, EnsemblConsequence> {

    public ConsequencePopupFilter2(FilterTableColumn<Annotation, EnsemblConsequence> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredictate(Operators op, EnsemblConsequence value) {

    }

    @Override protected Skin<?> createDefaultSkin() {
        return new ConsequencePopupFilterSkin2(this);
    }

    public void updatePredictate(Set<EnsemblConsequence> selectedConsequences) {
        if (selectedConsequences == null || selectedConsequences.isEmpty()) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> selectedConsequences.contains(a.getTranscriptConsequence().getConsequence()));
        }
    }
}