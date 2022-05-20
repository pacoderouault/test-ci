package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomAD;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;

public class GnomadPopupFilter extends TableColumnPopupFilter<Annotation, GnomadPopulationFreq> {

    public GnomadPopupFilter(FilterTableColumn<Annotation, GnomadPopulationFreq> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredicate(Operators op, GnomadPopulationFreq value) {
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new GnomadPopupFilterSkin(this);
    }

    public void updatePredicate(Operators op, Number value) {
        if (value == null) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                GnomadPopulationFreq maxPop = a.getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME);
                switch (op) {
                    case EQUALS:
                        return maxPop != null && maxPop.getAf() == value.doubleValue();
                    case NOT_EQUALS:
                        return maxPop != null && maxPop.getAf() != value.doubleValue();
                    case GREATER_THAN:
                        return maxPop != null && maxPop.getAf() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return maxPop != null && maxPop.getAf() >= value.doubleValue();
                    case LOWER_THAN:
                        return maxPop == null || maxPop.getAf() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return maxPop == null || maxPop.getAf() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}
