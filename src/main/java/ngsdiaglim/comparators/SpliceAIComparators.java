package ngsdiaglim.comparators;

import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;

import java.util.Comparator;

public class SpliceAIComparators implements Comparator<SpliceAIPredictions> {

    @Override
    public int compare(SpliceAIPredictions o1, SpliceAIPredictions o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        } else {
            return Double.compare(o1.getMostSeverePred().getScore(), o2.getMostSeverePred().getScore());
        }
    }

    @Override
    public Comparator<SpliceAIPredictions> reversed() {
        return Comparator.super.reversed();
    }
}
