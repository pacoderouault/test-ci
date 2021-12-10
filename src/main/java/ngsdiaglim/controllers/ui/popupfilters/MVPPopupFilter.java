package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;

public class MVPPopupFilter extends PredictionPopupFilter2{

    public MVPPopupFilter(FilterTableColumn<Annotation, VariantPrediction> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredictate(Operators op, VariantPrediction value) {
    }

    @Override
    protected void updatePredicate(Operators op, Number value) {
        if (value == null) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getTranscriptConsequence().getMvpPred().getScore().doubleValue() == value.doubleValue();
                    case NOT_EQUALS:
                        return a.getTranscriptConsequence().getMvpPred().getScore().doubleValue() != value.doubleValue();
                    case GREATER_THAN:
                        return a.getTranscriptConsequence().getMvpPred().getScore().doubleValue() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getTranscriptConsequence().getMvpPred().getScore().doubleValue() >= value.doubleValue();
                    case LOWER_THAN:
                        return a.getTranscriptConsequence().getMvpPred().getScore().doubleValue() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getTranscriptConsequence().getMvpPred().getScore().doubleValue() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}
