package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.controllers.ui.FilterTableView;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class ContigPopupFilter extends StringPopupFilter2 {
    public ContigPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
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
                        return a.getVariant().getContig().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getVariant().getContig().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getVariant().getContig().startsWith(value);
                    case ENDS_WITH:
                        return a.getVariant().getContig().endsWith(value);
                    case CONTAINS:
                        return a.getVariant().getContig().contains(value);
                    default:
                        return false;
                }
            });
        }
    }
}
