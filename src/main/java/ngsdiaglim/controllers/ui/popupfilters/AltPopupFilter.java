package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class AltPopupFilter extends StringPopupFilter {

    public AltPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
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
                        return a.getVariant().getAlt().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getVariant().getAlt().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getVariant().getAlt().startsWith(value);
                    case ENDS_WITH:
                        return a.getVariant().getAlt().endsWith(value);
                    case CONTAINS:
                        return a.getVariant().getAlt().contains(value);
                    default:
                        return false;
                }

            });
        }
    }
}