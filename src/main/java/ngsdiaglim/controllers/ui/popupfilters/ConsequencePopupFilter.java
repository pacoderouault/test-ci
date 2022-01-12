package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;

import java.util.Set;

public class ConsequencePopupFilter extends TableColumnPopupFilter<Annotation, EnsemblConsequence> {

    public ConsequencePopupFilter(FilterTableColumn<Annotation, EnsemblConsequence> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredicate(Operators op, EnsemblConsequence value) {

    }

    @Override protected Skin<?> createDefaultSkin() {
        return new ConsequencePopupFilterSkin(this);
    }

    public void updatePredictate(Set<EnsemblConsequence> selectedConsequences) {
        if (selectedConsequences == null || selectedConsequences.isEmpty()) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> selectedConsequences.contains(a.getTranscriptConsequence().getConsequence()));
        }
    }
}