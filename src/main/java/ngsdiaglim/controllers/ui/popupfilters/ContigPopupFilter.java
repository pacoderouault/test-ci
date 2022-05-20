package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;

public class ContigPopupFilter extends StringPopupFilter {
    public ContigPopupFilter(FilterTableColumn<Annotation, String> tableColumn) {
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
                        return a.getGenomicVariant().getContig().equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !a.getGenomicVariant().getContig().equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return a.getGenomicVariant().getContig().startsWith(value);
                    case ENDS_WITH:
                        return a.getGenomicVariant().getContig().endsWith(value);
                    case CONTAINS:
                        return a.getGenomicVariant().getContig().contains(value);
                    default:
                        return false;
                }
            });
        }
    }
}
