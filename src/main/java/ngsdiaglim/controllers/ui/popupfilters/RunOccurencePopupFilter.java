package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;

public class RunOccurencePopupFilter extends NumberPopupFilter {

    public RunOccurencePopupFilter(FilterTableColumn<Annotation, Number> tableColumn) {
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
                        return a.getVariant().getOccurrenceInRun() == value.doubleValue();
                    case NOT_EQUALS:
                        return a.getVariant().getOccurrenceInRun() != value.doubleValue();
                    case GREATER_THAN:
                        return a.getVariant().getOccurrenceInRun() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getVariant().getOccurrenceInRun() >= value.doubleValue();
                    case LOWER_THAN:
                        return a.getVariant().getOccurrenceInRun() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getVariant().getOccurrenceInRun() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}
