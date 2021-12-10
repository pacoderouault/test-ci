package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;

public class DepthPopupFilter extends NumberPopupFilter2 {

    public DepthPopupFilter(FilterTableColumn<Annotation, Number> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredictate(Operators op, Number value) {
        if (value == null) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getDepth() == value.doubleValue();
                    case NOT_EQUALS:
                        return a.getDepth() != value.doubleValue();
                    case GREATER_THAN:
                        return a.getDepth() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getDepth() >= value.doubleValue();
                    case LOWER_THAN:
                        return a.getDepth() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getDepth() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}
