package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class HgvscPopupFilter extends StringPopupFilter {

    public HgvscPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
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
                        return a.getTranscriptConsequence().getHgvsc().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getTranscriptConsequence().getHgvsc().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getTranscriptConsequence().getHgvsc().startsWith(value);
                    case ENDS_WITH:
                        return a.getTranscriptConsequence().getHgvsc().endsWith(value);
                    case CONTAINS:
                        return a.getTranscriptConsequence().getHgvsc().contains(value);
                    default:
                        return false;
                }
            });
        }
    }
}
