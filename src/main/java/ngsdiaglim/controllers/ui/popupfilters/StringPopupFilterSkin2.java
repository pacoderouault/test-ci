package ngsdiaglim.controllers.ui.popupfilters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.Operators;

public class StringPopupFilterSkin2 implements Skin<StringPopupFilter2> {

    private final StringPopupFilter2 stringPopupFilter;
    private final VBox container = new VBox();
    private final HBox hbox = new HBox();
    private final ComboBox<Operators> operatorsComboBox = new ComboBox<>();
    private final TextField textfield = new TextField();
    private final ButtonBar buttonBar = new ButtonBar();
    private final Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
    private final Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));

    public StringPopupFilterSkin2(StringPopupFilter2 stringPopupFilter) {
        this.stringPopupFilter = stringPopupFilter;

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
            stringPopupFilter.clearPredicate();
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
                Operators.STARTS_WITH,
                Operators.ENDS_WITH,
                Operators.CONTAINS
        );
    }

    protected void updatePredicate() {
        Operators op = operatorsComboBox.getValue();
        String value = textfield.getText();
        stringPopupFilter.updatePredictate(op, value);
    }

    public void resetFields() {
        operatorsComboBox.getSelectionModel().select(null);
        textfield.setText(null);
    }

    @Override
    public StringPopupFilter2 getSkinnable() {
        return stringPopupFilter;
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {

    }

}
