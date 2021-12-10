package ngsdiaglim.controllers.analysisview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HideVariantDropDownMenuContent extends VBox {

    @FXML private CheckBox hideIntronicVariants;
    @FXML private CheckBox hideSynonymousVariants;
    @FXML private CheckBox hideUTRVariants;
    @FXML private CheckBox hideIntergenicVariants;
    @FXML private CheckBox hideNonCodingVariants;
    @FXML private CheckBox hideFalsePositiveVariants;

    private final SimpleObjectProperty<Predicate<Annotation>> predicate = new SimpleObjectProperty<>();

    public HideVariantDropDownMenuContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HideVariantDropDownMenuContent.fxml"), App.getBundle());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        User user = App.get().getLoggedUser();
        hideIntronicVariants.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate();
            if (oldValue != null) {
                user.setPreference(DefaultPreferencesEnum.HIDE_INTRONIC_VARIANT, newValue);
                user.savePreferences();
            }
        });
        hideSynonymousVariants.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate();
            if (oldValue != null) {
                user.setPreference(DefaultPreferencesEnum.HIDE_SYNONYMOUS_VARIANT, newValue);
                user.savePreferences();
            }
        });
        hideUTRVariants.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate();
            if (oldValue != null) {
                user.setPreference(DefaultPreferencesEnum.HIDE_UTR_VARIANT, newValue);
                user.savePreferences();
            }
        });
        hideIntergenicVariants.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate();
            if (oldValue != null) {
                user.setPreference(DefaultPreferencesEnum.HIDE_INTERGENIC_VARIANT, newValue);
                user.savePreferences();
            }
        });
        hideNonCodingVariants.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate();
            if (oldValue != null) {
                user.setPreference(DefaultPreferencesEnum.HIDE_NON_CODING_VARIANT, newValue);
                user.savePreferences();
            }
        });
        hideFalsePositiveVariants.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePredicate();
            if (oldValue != null) {
                user.setPreference(DefaultPreferencesEnum.HIDE_FALSE_POSITIVE_VARIANT, newValue);
                user.savePreferences();
            }
        });

        String hideIntronic = user.getPreferences().getPreference(DefaultPreferencesEnum.HIDE_INTRONIC_VARIANT);
        if (hideIntronic != null) {
            hideIntronicVariants.setSelected(Boolean.parseBoolean(hideIntronic));
        }
        String hideSynonymous = user.getPreferences().getPreference(DefaultPreferencesEnum.HIDE_SYNONYMOUS_VARIANT);
        if (hideSynonymous != null) {
            hideSynonymousVariants.setSelected(Boolean.parseBoolean(hideSynonymous));
        }
        String hideUtr = user.getPreferences().getPreference(DefaultPreferencesEnum.HIDE_UTR_VARIANT);
        if (hideUtr != null) {
            hideUTRVariants.setSelected(Boolean.parseBoolean(hideUtr));
        }
        String hideIntergenic = user.getPreferences().getPreference(DefaultPreferencesEnum.HIDE_INTERGENIC_VARIANT);
        if (hideIntergenic != null) {
            hideIntergenicVariants.setSelected(Boolean.parseBoolean(hideIntergenic));
        }
        String hideNonCoding = user.getPreferences().getPreference(DefaultPreferencesEnum.HIDE_NON_CODING_VARIANT);
        if (hideNonCoding != null) {
            hideNonCodingVariants.setSelected(Boolean.parseBoolean(hideNonCoding));
        }
        String hideFalsePositive = user.getPreferences().getPreference(DefaultPreferencesEnum.HIDE_FALSE_POSITIVE_VARIANT);
        if (hideFalsePositive != null) {
            hideFalsePositiveVariants.setSelected(Boolean.parseBoolean(hideFalsePositive));
        }
    }

    private void updatePredicate() {
        List<Predicate<Annotation>> predicates = new ArrayList<>();
        if (hideIntronicVariants.isSelected()) {
            predicates.add(p -> !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.INTRON_VARIANT));
        }
        if (hideSynonymousVariants.isSelected()) {
            predicates.add(p -> !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.SYNONYMOUS_VARIANT));
        }
        if (hideUTRVariants.isSelected()) {
            predicates.add(p -> !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.PRIME_3_UTR_VARIANT)
                    && !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.PRIME_5_UTR_VARIANT));
        }
        if (hideIntergenicVariants.isSelected()) {
            predicates.add(p -> !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.INTERGENIC_VARIANT)
                    && !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.UPSTREAM_GENE_VARIANT)
                    && !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.DOWNSTREAM_GENE_VARIANT));
        }
        if (hideNonCodingVariants.isSelected()) {
            predicates.add(p -> !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.NON_CODING_TRANSCRIPT_EXON_VARIANT)
                    && !p.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.NON_CODING_TRANSCRIPT_VARIANT));
        }
        if (hideFalsePositiveVariants.isSelected()) {
            predicates.add(p -> !p.getVariant().isFalsePositive());
        }

        predicate.setValue(predicates.stream().reduce(x -> true, Predicate::and));
    }

    public Predicate<Annotation> getPredicate() {
        return predicate.get();
    }

    public SimpleObjectProperty<Predicate<Annotation>> predicateProperty() {
        return predicate;
    }
}
