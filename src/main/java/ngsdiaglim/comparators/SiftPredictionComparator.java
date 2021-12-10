package ngsdiaglim.comparators;

import ngsdiaglim.modeles.variants.predictions.SiftPrediction;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;

import java.util.Comparator;

public class SiftPredictionComparator implements Comparator<VariantPrediction> {

    @Override
    public int compare(VariantPrediction o1, VariantPrediction o2) {

        if (o1 == null && o2 == null) return 0;
        else if (o2 == null) return 1;
        else if (o1 == null) return -1;
        else {
            if (o1.getTool().equals(o2.getTool())) {
                double score1 = o1.getScore().doubleValue();
                double score2 = o2.getScore().doubleValue();
                return -Double.compare(score1, score2);
            }
            return 0;
        }
    }

    @Override
    public Comparator<VariantPrediction> reversed() {
        return Comparator.super.reversed();
    }
}
