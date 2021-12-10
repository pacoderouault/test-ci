package ngsdiaglim.controllers.ui.popupfilters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.Operators;
import org.apache.commons.lang3.StringUtils;

public class StringPopupFilterSkin implements Skin<StringPopupFilter> {

    private final StringPopupFilter stringPopupFilter;
    private final VBox container = new VBox();
    private final HBox hbox = new HBox();
    private final ComboBox<Operators> operatorsComboBox = new ComboBox<>();
    private final TextField textfield = new TextField();
    private final ButtonBar buttonBar = new ButtonBar();
    private final Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
    private final Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));

    public StringPopupFilterSkin(StringPopupFilter stringPopupFilter) {
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
            stringPopupFilter.getTableColumn().setPredicate(null);
            operatorsComboBox.getSelectionModel().select(null);
            textfield.setText(null);
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

    private void updatePredicate() {
        Operators op = operatorsComboBox.getValue();
        String value = textfield.getText();
        if (StringUtils.isBlank(value)) {
            stringPopupFilter.getTableColumn().setPredicate(null);
        } else {
            stringPopupFilter.getTableColumn().setPredicate(s -> {
                switch (op) {
                    case EQUALS:
                        return s.equalsIgnoreCase(value);
                    case NOT_EQUALS:
                        return !s.equalsIgnoreCase(value);
                    case STARTS_WITH:
                        return s.startsWith(value);
                    case ENDS_WITH:
                        return s.endsWith(value);
                    case CONTAINS:
                        return s.contains(value);
                    default:
                        return false;
                }
            });
        }
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
