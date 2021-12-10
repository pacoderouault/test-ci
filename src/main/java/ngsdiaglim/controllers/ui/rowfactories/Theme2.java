package ngsdiaglim.controllers.ui.rowfactories;

import javafx.scene.control.TableRow;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.variants.Annotation;

public class Theme2 extends TableRow<Annotation> {
    @Override
    public void updateItem(Annotation item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeIf(c -> c.startsWith("tabletheme"));
        if (item != null && !empty) {
            if (item.getVariant().getAcmg().equals(ACMG.PATHOGENIC)) {
                getStyleClass().add("tabletheme2-patho");
            } else if (item.getVariant().getAcmg().equals(ACMG.LIKELY_PATHOGENIC)) {
                getStyleClass().add("tabletheme2-likelypatho");
            } else if (item.getVariant().getAcmg().equals(ACMG.LIKELY_BENIN)) {
                getStyleClass().add("tabletheme2-likelybenin");
            } else if (item.getVariant().getAcmg().equals(ACMG.BENIN)) {
                getStyleClass().add("tabletheme2-benin");
            } else if (item.getVariant().isFalsePositive()) {
                getStyleClass().add("tabletheme2-falsepositive");
            }
        }
    }
}
