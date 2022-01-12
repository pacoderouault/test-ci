package ngsdiaglim.controllers.ui.popupfilters;

import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;

public class GerpPopupFilter extends PredictionPopupFilter {

    public GerpPopupFilter(FilterTableColumn<Annotation, VariantPrediction> tableColumn) {
        super(tableColumn);
    }

    @Override
    protected void updatePredicate(Operators op, VariantPrediction value) {
    }

    @Override
    protected void updatePredicate(Operators op, Number value) {
        if (value == null) {
            getTableColumn().setPredicate(null);
        } else {
            getTableColumn().setPredicate(a -> {
                switch (op) {
                    case EQUALS:
                        return a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() == value.doubleValue();
                    case NOT_EQUALS:
                        return a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() != value.doubleValue();
                    case GREATER_THAN:
                        return a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() >= value.doubleValue();
                    case LOWER_THAN:
                        return a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return a.getTranscriptConsequence().getGerpPred().getScore().doubleValue() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }
}
