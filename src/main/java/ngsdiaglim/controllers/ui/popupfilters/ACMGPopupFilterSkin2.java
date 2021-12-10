package ngsdiaglim.controllers.ui.popupfilters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.Annotation;

public class ACMGPopupFilterSkin2 implements Skin<ACMGPopupFilter> {

    private final ACMGPopupFilter acmgPopupFilter;
    private final VBox container = new VBox();
    private final HBox hbox = new HBox();
    private final ComboBox<Operators> operatorsComboBox = new ComboBox<>();
    private final ComboBox<ACMG> acmgCombobox = new ComboBox<>();
    private final ButtonBar buttonBar = new ButtonBar();
    private final Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
    private final Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));

    public ACMGPopupFilterSkin2(ACMGPopupFilter acmgPopupFilter) {
        this.acmgPopupFilter = acmgPopupFilter;

        filterBtn.setDefaultButton(true);
        buttonBar.getButtons().setAll(clearBtn, filterBtn);

        initOperatorsCb();
        initAcmgCb();
        hbox.getChildren().setAll(operatorsComboBox, acmgCombobox);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(5);
        container.setSpacing(5);
        container.getChildren().setAll(hbox, buttonBar);
        container.getStyleClass().addAll("module-box", "module-box-container");
        clearBtn.setOnAction(e -> {
            acmgPopupFilter.clearPredicate();
            resetFields();
        });
        filterBtn.setOnAction(e -> {
            updatePredicate();
        });
        resetFields();
    }

    private void initOperatorsCb() {
        operatorsComboBox.getItems().setAll(
                Operators.EQUALS,
                Operators.NOT_EQUALS,
                Operators.LOWER_THAN,
                Operators.LOWER_OR_EQUALS_THAN,
                Operators.GREATER_THAN,
                Operators.GREATER_OR_EQUALS_THAN
        );
    }

    private void initAcmgCb() {
        acmgCombobox.getItems().setAll(
                ACMG.BENIN,
                ACMG.LIKELY_BENIN,
                ACMG.UNCERTAIN_SIGNIGICANCE,
                ACMG.LIKELY_PATHOGENIC,
                ACMG.PATHOGENIC
        );
    }

    private void updatePredicate() {
        Operators op = operatorsComboBox.getValue();
        ACMG acmg = acmgCombobox.getValue();
        acmgPopupFilter.updatePredictate(op, acmg);
    }

    public void resetFields() {
        operatorsComboBox.getSelectionModel().select(Operators.GREATER_OR_EQUALS_THAN);
        acmgCombobox.getSelectionModel().select(null);
    }

    @Override public ACMGPopupFilter getSkinnable() {
        return acmgPopupFilter;
    }

    @Override public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {

    }
}
