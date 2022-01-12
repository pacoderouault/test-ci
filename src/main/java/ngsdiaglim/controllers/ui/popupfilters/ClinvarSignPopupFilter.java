package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class ClinvarSignPopupFilter extends StringPopupFilter {

    public ClinvarSignPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredicate(Operators op, String value) {
        if (StringUtils.isBlank(value)) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getTranscriptConsequence().getClinvarSign().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getTranscriptConsequence().getClinvarSign().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getTranscriptConsequence().getClinvarSign().startsWith(value);
                    case ENDS_WITH:
                        return a.getTranscriptConsequence().getClinvarSign().endsWith(value);
                    case CONTAINS:
                        return a.getTranscriptConsequence().getClinvarSign().contains(value);
                    default:
                        return false;
                }
            });
        }
    }
}
