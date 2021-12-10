package ngsdiaglim.comparators;

import ngsdiaglim.enumerations.EnsemblConsequence;

import java.util.Comparator;

public class EnsemblConsequenceComparator implements Comparator<EnsemblConsequence> {

    @Override
    public int compare(EnsemblConsequence o1, EnsemblConsequence o2) {
        if (o1 == null && o2 == null) return 0;
        else if (o1 == null) return 1;
        else if (o2 == null) return -1;
        else return o1.getWeight().compareTo(o2.getWeight());
    }
}
