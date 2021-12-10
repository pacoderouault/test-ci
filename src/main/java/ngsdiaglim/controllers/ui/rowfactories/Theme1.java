package ngsdiaglim.controllers.ui.rowfactories;

import javafx.scene.control.TableRow;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.variants.Annotation;

public class Theme1 extends TableRow<Annotation> {

    @Override
    public void updateItem(Annotation item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeIf(c -> c.startsWith("tabletheme"));
        if (item != null && !empty) {
            if (item.getVariant().getAcmg().equals(ACMG.PATHOGENIC)) {
                getStyleClass().add("tabletheme1-patho");
            } else if (item.getVariant().getAcmg().equals(ACMG.LIKELY_PATHOGENIC)) {
                getStyleClass().add("tabletheme1-likelypatho");
            } else if (item.getVariant().getHotspot() != null) {
                getStyleClass().add("tabletheme1-hotspot");
            } else {
                if (item.getTranscriptConsequence() != null) {
                    if (!Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.COLOR_UNIQUE_VARIANTS)) || item.getVariant().getOccurrence() <= 1 ) {
                        if (item.getTranscriptConsequence().getExon() != null && !item.getTranscriptConsequence().getExon().isEmpty()) {
                            getStyleClass().add("tabletheme1-exonic");
                        } else if (item.getTranscriptConsequence().getIntron() != null && !item.getTranscriptConsequence().getIntron().isEmpty()) {
                            getStyleClass().add("tabletheme1-intronic");
                        } else {
                            getStyleClass().add("tabletheme1-other");
                        }
                    }
                } else {
                    getStyleClass().add("tabletheme1-other");
                }
            }
        }
    }
}
