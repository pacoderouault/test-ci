package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class GenesPopupFilter extends StringPopupFilter2 {

    public GenesPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredictate(Operators op, String value) {
        if (StringUtils.isBlank(value)) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getGeneNames().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getGeneNames().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getGeneNames().startsWith(value);
                    case ENDS_WITH:
                        return a.getGeneNames().endsWith(value);
                    case CONTAINS:
                        return a.getGeneNames().contains(value);
                    default:
                        return false;
                }
            });
        }
    }
}
