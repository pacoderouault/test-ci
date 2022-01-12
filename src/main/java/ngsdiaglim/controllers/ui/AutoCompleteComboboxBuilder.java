package ngsdiaglim.controllers.ui;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class AutoCompleteComboboxBuilder {

    public static class HideableItem<T>
    {
        private final ObjectProperty<T> object = new SimpleObjectProperty<>();
        private final BooleanProperty hidden = new SimpleBooleanProperty();

        private HideableItem(T object)
        {
            setObject(object);
        }

        public ObjectProperty<T> objectProperty(){return this.object;}
        public T getObject(){return this.objectProperty().get();}
        public void setObject(T object){this.objectProperty().set(object);}

        public BooleanProperty hiddenProperty(){return this.hidden;}
        public boolean isHidden(){return this.hiddenProperty().get();}
        public void setHidden(boolean hidden){this.hiddenProperty().set(hidden);}

        @Override
        public String toString()
        {
            return getObject() == null ? null : getObject().toString();
        }
    }

    public static <T> ComboBox<HideableItem<T>> createAutoCompleteCombobox(ComboBox<HideableItem<T>> comboBox, List<T> items) {
        ObservableList<HideableItem<T>> hideableHideableItems = FXCollections.observableArrayList(hideableItem -> new Observable[]{hideableItem.hiddenProperty()});

        items.forEach(item ->
        {
            HideableItem<T> hideableItem = new HideableItem<>(item);
            hideableHideableItems.add(hideableItem);
        });

        FilteredList<HideableItem<T>> filteredHideableItems = new FilteredList<>(hideableHideableItems, t -> !t.isHidden());

        comboBox.setItems(filteredHideableItems);

        @SuppressWarnings("unchecked")
        HideableItem<T>[] selectedItem = (HideableItem<T>[]) new HideableItem[1];

        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->
        {
            if(!comboBox.isShowing()) return;

            comboBox.setEditable(true);
            comboBox.getEditor().clear();
        });

        comboBox.showingProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue)
            {
                Platform.runLater(() ->
                {
                    Node node = comboBox.getScene().lookup("list-view");
                    if (node instanceof ListView) {
                        ListView<HideableItem<T>> lv = (ListView<HideableItem<T>>) node;
                        if (selectedItem[0] == null) // first use
                        {
                        double cellHeight = ((Control) lv.lookup(".list-cell")).getHeight();
                        lv.setFixedCellSize(cellHeight);
                        }
                        lv.scrollTo(comboBox.getValue());
                    }
                });

            }
            else
            {
                HideableItem<T> value = comboBox.getValue();
                if(value != null) selectedItem[0] = value;

                comboBox.setEditable(false);

                Platform.runLater(() ->
                {
                    comboBox.getSelectionModel().select(selectedItem[0]);
                    comboBox.setValue(selectedItem[0]);
                });
            }
        });

        comboBox.setOnHidden(event -> hideableHideableItems.forEach(item -> item.setHidden(false)));

        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) ->
        {
            if(!comboBox.isShowing()) return;

            Platform.runLater(() ->
            {
                if(comboBox.getSelectionModel().getSelectedItem() == null)
                {
                    hideableHideableItems.forEach(item -> item.setHidden(!item.getObject().toString().toLowerCase().contains(newValue.toLowerCase())));
                }
                else
                {
                    boolean validText = false;

                    for(HideableItem<?> hideableItem : hideableHideableItems)
                    {
                        if(hideableItem.getObject().toString().equals(newValue))
                        {
                            validText = true;
                            break;
                        }
                    }

                    if(!validText) comboBox.getSelectionModel().select(null);
                }
            });
        });

        return comboBox;
    }


    public static <T> ComboBox<HideableItem<T>> createAutoCompleteCombobox(List<T> items) {
        return createAutoCompleteCombobox(new ComboBox<>(), items);
    }
}
