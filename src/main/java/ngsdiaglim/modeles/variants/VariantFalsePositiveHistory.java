package ngsdiaglim.modeles.variants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Collection;

public class VariantFalsePositiveHistory {

    private final Variant variant;
    private final ObservableList<VariantFalsePositive> history = FXCollections.observableArrayList();
    private SortedList<VariantFalsePositive> historySorted;

    public VariantFalsePositiveHistory(Variant variant) {
        this.variant = variant;
    }

    public Variant getVariant() {return variant;}

    public SortedList<VariantFalsePositive> getHistory() {return historySorted;}

    public void addVariantFalsePositive(VariantFalsePositive variantFalsePositive) {
        if (variantFalsePositive.getVariantId() == variant.getId()) {
            if (historySorted == null) {
                historySorted = new SortedList<>(history);
            }
            history.add(variantFalsePositive);
        }
    }

    public void setVariantFalsePositiveHistory(Collection<VariantFalsePositive> variantFalsePositives) {
        if (historySorted == null) {
            historySorted = new SortedList<>(history);
        }
        history.setAll(variantFalsePositives);
    }

    public VariantFalsePositive getLastVariantFalsePositive() {
        return history.isEmpty() ? null : history.get(0);
    }

}
