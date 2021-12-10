package ngsdiaglim.controllers.ui.popupfilters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.Operators;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;

public class NumberPopupFilterSkin implements Skin<NumberPopupFilter> {

    private final NumberPopupFilter popupFilter;
    private final VBox container = new VBox();
    private final HBox hbox = new HBox();
    private final ComboBox<Operators> operatorsComboBox = new ComboBox<>();
    private final TextField textfield = new TextField();
    private final ButtonBar buttonBar = new ButtonBar();
    private final Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
    private final Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));

    public NumberPopupFilterSkin(NumberPopupFilter popupFilter) {
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
            popupFilter.getTableColumn().setPredicate(null);
            operatorsComboBox.getSelectionModel().select(null);
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
            popupFilter.getTableColumn().setPredicate(n -> {
                switch (op) {
                    case EQUALS:
                        return n.doubleValue() == value.doubleValue();
                    case NOT_EQUALS:
                        return n.doubleValue() != value.doubleValue();
                    case GREATER_THAN:
                        return n.doubleValue() > value.doubleValue();
                    case GREATER_OR_EQUALS_THAN:
                        return n.doubleValue() >= value.doubleValue();
                    case LOWER_THAN:
                        return n.doubleValue() < value.doubleValue();
                    case LOWER_OR_EQUALS_THAN:
                        return n.doubleValue() <= value.doubleValue();
                    default:
                        return false;
                }
            });
        }
    }

    @Override
    public NumberPopupFilter getSkinnable() {
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