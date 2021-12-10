package ngsdiaglim.controllers.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngsdiaglim.controllers.ui.popupfilters.TableColumnPopupFilter2;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Predicate;

public class FilterTableColumn<S, T> extends TableColumn<S, T> {

    private TableColumnPopupFilter2<S, T> popupFilter;
    private final SimpleObjectProperty<Predicate<S>> predicate = new SimpleObjectProperty<>();
    private final FontIcon filterIcon = new FontIcon("mdal-filter_alt");

    public FilterTableColumn() {
    }

    public FilterTableColumn(String text) {
        Label l = new Label(text);

        setGraphic(l);
        predicateProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                l.setGraphic(null);
            } else {
                l.setGraphic(filterIcon);
            }
        });
    }

    public void showPopupFilter() {
        if (popupFilter != null) {
            popupFilter.showPopup();
        }
    }

    public TableColumnPopupFilter2<S, T> getPopupFilter() {return popupFilter;}

    public void setPopupFilter(TableColumnPopupFilter2<S, T> popupFilter) {
        this.popupFilter = popupFilter;
    }

    public final FilterTableView<S> getTestTableView() {
        TableView<S> table =  tableViewProperty().get();
        if (table instanceof FilterTableView) {
            return (FilterTableView<S>)table;
        }
        return null;
    }

    public Predicate<S> getPredicate() {
        return predicate.get();
    }

    public SimpleObjectProperty<Predicate<S>> predicateProperty() {
        return predicate;
    }

    public void setPredicate(Predicate<S> predicate) {
        this.predicate.set(predicate);
    }
}
