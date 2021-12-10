package ngsdiaglim.controllers.ui.popupfilters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsequencePopupFilterSkin2 implements Skin<ConsequencePopupFilter2> {

    private final ConsequencePopupFilter2 popupFilter;
    private final VBox container = new VBox();
    private final CustomTextField textfield = new CustomTextField();
    private final ListView<EnsemblConsequence> lv = new ListView<>();
    private final ButtonBar buttonBar = new ButtonBar();
    private final Button clearBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.cancel"));
    private final Button filterBtn = new Button(App.getBundle().getString("popupfilter.acmg.btn.filter"));
    private final Set<EnsemblConsequence> selectedConsequences = new HashSet<>();
    private FilteredList<EnsemblConsequence> filteredConsequences;
    private SortedList<EnsemblConsequence> sortedData;
    public ConsequencePopupFilterSkin2(ConsequencePopupFilter2 popupFilter) {
        this.popupFilter = popupFilter;

        filterBtn.setDefaultButton(true);
        buttonBar.getButtons().setAll(clearBtn, filterBtn);

        initListView();
        initTextField();
        container.setSpacing(5);
        container.getChildren().setAll(textfield, lv, buttonBar);
        container.getStyleClass().addAll("module-box", "module-box-container");
        clearBtn.setOnAction(e -> {
            resetFields();
            popupFilter.clearPredicate();
        });
        filterBtn.setOnAction(e -> {
            updatePredicate();
        });
    }

    private void initTextField() {
        textfield.setLeft(new FontIcon("mdmz-search"));
        textfield.setOnAction(e -> {
            String text = textfield.getText();
            if (text == null || StringUtils.isBlank(text)) {
                filteredConsequences.setPredicate(null);
            } else {
                String finalText = text.toUpperCase();
                filteredConsequences.setPredicate(c -> c.getName().toUpperCase().contains(finalText));
            }
        });
    }

    private void initListView() {
        ObservableList<EnsemblConsequence> consequences = Arrays.stream(EnsemblConsequence.values())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        filteredConsequences = new FilteredList<>(consequences, s -> true);
        sortedData = new SortedList<>(filteredConsequences);
        sortedData.setComparator(Comparator.comparing(EnsemblConsequence::getWeight));
        lv.setItems(sortedData);
        lv.setCellFactory(list -> new ConsequenceCell());
    }

    private void updatePredicate() {
        popupFilter.updatePredictate(selectedConsequences);
    }

    public void resetFields() {
        textfield.setText(null);
        selectedConsequences.clear();
    }


    @Override public ConsequencePopupFilter2 getSkinnable() {
        return popupFilter;
    }

    @Override public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {

    }

    private class ConsequenceCell extends ListCell<EnsemblConsequence> {
        private final CheckBox cb = new CheckBox();

        ConsequenceCell() {
            cb.selectedProperty().addListener((obs, oldV, newV) -> {
                if (getItem() != null) {
                    if (newV) {
                        selectedConsequences.add(getItem());
                    }
                    else {
                        selectedConsequences.remove(getItem());
                    }
                }
            });
        }
        @Override
        protected void updateItem(EnsemblConsequence item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
            }
            else {
                cb.setText(item.getName());
                cb.setSelected(selectedConsequences.contains(item));
                setGraphic(cb);
            }
        }
    }
}