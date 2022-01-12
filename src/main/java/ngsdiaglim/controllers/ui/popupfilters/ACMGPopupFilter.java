package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.control.Skin;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;


public class ACMGPopupFilter extends TableColumnPopupFilter<Annotation, ACMG> {


    public ACMGPopupFilter(FilterTableColumn<Annotation, ACMG> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredicate(Operators op, ACMG acmg) {
        if (acmg == null) {
            getTableColumn().setPredicate(null);
        }
        else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getVariant().getAcmg().equals(acmg);
                    case NOT_EQUALS:
                        return !a.getVariant().getAcmg().equals(acmg);
                    case GREATER_THAN:
                        return a.getVariant().getAcmg().getPathogenicityValue() > acmg.getPathogenicityValue();
                    case LOWER_THAN:
                        return a.getVariant().getAcmg().getPathogenicityValue() < acmg.getPathogenicityValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getVariant().getAcmg().getPathogenicityValue() >= acmg.getPathogenicityValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getVariant().getAcmg().getPathogenicityValue() <= acmg.getPathogenicityValue();
                    default:
                        return false;
                }
            });
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ACMGPopupFilterSkin(this);
    }
}
