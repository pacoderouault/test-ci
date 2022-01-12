package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;

public class VafPopupFilter extends NumberPopupFilter {

    public VafPopupFilter(FilterTableColumn<Annotation, Number> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredicate(Operators op, Number value) {
        if (value == null) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getVaf() == value.doubleValue();
                    case NOT_EQUALS:
                        return a.getVaf() != value.doubleValue();
                    case GREATER_THAN:
                        return a.getVaf() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getVaf() >= value.doubleValue();
                    case LOWER_THAN:
                        return a.getVaf() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getVaf() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}