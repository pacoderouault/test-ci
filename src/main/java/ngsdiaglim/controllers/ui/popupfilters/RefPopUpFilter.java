package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.controllers.ui.FilterTableView;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class RefPopUpFilter extends StringPopupFilter2 {

    public RefPopUpFilter(FilterTableColumn<Annotation, String> tableColumn) {
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
                        return a.getVariant().getRef().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getVariant().getRef().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getVariant().getRef().startsWith(value);
                    case ENDS_WITH:
                        return a.getVariant().getRef().endsWith(value);
                    case CONTAINS:
                        return a.getVariant().getRef().contains(value);
                    default:
                        return false;
                }

            });
        }
    }
}