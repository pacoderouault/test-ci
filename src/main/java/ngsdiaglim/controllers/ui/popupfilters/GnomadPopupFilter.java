package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;

public class GnomadPopupFilter extends TableColumnPopupFilter2<Annotation, GnomadPopulationFreq> {

    public GnomadPopupFilter(FilterTableColumn<Annotation, GnomadPopulationFreq> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredictate(Operators op, GnomadPopulationFreq value) {
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new GnomadPopupFilterSkin2(this);
    }

    public void updatePredicate(Operators op, Number value) {
        if (value == null) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getGnomADFrequencies().getMax() != null && a.getGnomADFrequencies().getMax().getAf() == value.doubleValue();
                    case NOT_EQUALS:
                        return a.getGnomADFrequencies().getMax() != null && a.getGnomADFrequencies().getMax().getAf() != value.doubleValue();
                    case GREATER_THAN:
                        return a.getGnomADFrequencies().getMax() != null && a.getGnomADFrequencies().getMax().getAf() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getGnomADFrequencies().getMax() != null && a.getGnomADFrequencies().getMax().getAf() >= value.doubleValue();
                    case LOWER_THAN:
                        return a.getGnomADFrequencies().getMax() == null || a.getGnomADFrequencies().getMax().getAf() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getGnomADFrequencies().getMax() == null || a.getGnomADFrequencies().getMax().getAf() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}
