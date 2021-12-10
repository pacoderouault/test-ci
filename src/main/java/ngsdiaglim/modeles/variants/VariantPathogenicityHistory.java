package ngsdiaglim.modeles.variants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Collection;
import java.util.Comparator;

public class VariantPathogenicityHistory {

    private final Variant variant;
    private final ObservableList<VariantPathogenicity> history = FXCollections.observableArrayList();
    private SortedList<VariantPathogenicity> historySorted;

    public VariantPathogenicityHistory(Variant variant) {
        this.variant = variant;
//        historySorted.setComparator(Comparator.comparing(VariantPathogenicity::getDateTime).reversed());
    }

    public Variant getVariant() {return variant;}

    public SortedList<VariantPathogenicity> getHistory() {return historySorted;}

    public void addVariantPathogenicity(VariantPathogenicity variantPathogenicity) {
        if (variantPathogenicity.getVariantId() == variant.getId()) {
            if (historySorted == null) {
                historySorted = new SortedList<>(history);
            }
            history.add(variantPathogenicity);
        }
    }

    public void setVariantPathogenicityHistory(Collection<VariantPathogenicity> variantPathogenicities) {
        if (historySorted == null) {
            historySorted = new SortedList<>(history);
        }
        history.setAll(variantPathogenicities);
    }

    public VariantPathogenicity getLastVariantPathogenicity() {
        return history.isEmpty() ? null : history.get(0);
    }
}
