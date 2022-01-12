package ngsdiaglim.controllers.ui.popupfilters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.Operators;

public class StringPopupFilterSkin implements Skin<StringPopupFilter> {

    private final StringPopupFilter stringPopupFilter;
    private final VBox container = new VBox();
    private final ComboBox<Operators> operatorsComboBox = new ComboBox<>();
    private final TextField textfield = new TextField();

    public StringPopupFilterSkin(StringPopupFilter stringPopupFilter) {
        this.stringPopupFilter = stringPopupFilter;

        Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));
        filterBtn.setDefaultButton(true);
        ButtonBar buttonBar = new ButtonBar();
        Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
        buttonBar.getButtons().setAll(clearBtn, filterBtn);

        initOperatorsCb();
        HBox hbox = new HBox();
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
        filterBtn.setOnAction(e -> updatePredicate());
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
        stringPopupFilter.updatePredicate(op, value);
    }

    public void resetFields() {
        operatorsComboBox.getSelectionModel().select(null);
        textfield.setText(null);
    }

    @Override
    public StringPopupFilter getSkinnable() {
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
