package ngsdiaglim.controllers.ui.popupfilters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.Operators;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import org.apache.commons.lang3.math.NumberUtils;

public class PredictionPopupFilterSkin2 implements Skin<PredictionPopupFilter2> {

    private final PredictionPopupFilter2 popupFilter;
    private final VBox container = new VBox();
    private final HBox hbox = new HBox();
    private final ComboBox<Operators> operatorsComboBox = new ComboBox<>();
    private final TextField textfield = new TextField();
    private final ButtonBar buttonBar = new ButtonBar();
    private final Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
    private final Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));

    public PredictionPopupFilterSkin2(PredictionPopupFilter2 popupFilter) {
        this.popupFilter = popupFilter;

        filterBtn.setDefaultButton(true);
        buttonBar.getButtons().setAll(clearBtn, filterBtn);

        initOperatorsCb();
        hbox.getChildren().setAll(operatorsComboBox, textfield);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(5);
        container.setSpacing(5);
        container.getChildren().setAll(hbox, buttonBar);
        container.getStyleClass().addAll("module-box", "module-box-container");
        clearBtn.setOnAction(e -> {
            popupFilter.clearPredicate();
            resetFields();
        });
        filterBtn.setOnAction(e -> {
            updatePredicate();
        });
    }

    private void initOperatorsCb() {
        operatorsComboBox.getItems().setAll(
                Operators.EQUALS,
                Operators.NOT_EQUALS,
                Operators.GREATER_THAN,
                Operators.GREATER_OR_EQUALS_THAN,
                Operators.LOWER_THAN,
                Operators.LOWER_OR_EQUALS_THAN
        );
    }

    private void updatePredicate() {
        Operators op = operatorsComboBox.getValue();
        String valueStr = textfield.getText();
        if (NumberUtils.isParsable(valueStr)) {
            Number value = NumberUtils.createNumber(valueStr);
            popupFilter.updatePredicate(op, value);
        }
    }

    public void resetFields() {
        operatorsComboBox.getSelectionModel().select(null);
        textfield.setText(null);
    }

    @Override
    public PredictionPopupFilter2 getSkinnable() {
        return popupFilter;
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {

    }
}
