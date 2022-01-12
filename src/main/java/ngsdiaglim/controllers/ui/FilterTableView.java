package ngsdiaglim.controllers.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.function.Predicate;

public class FilterTableView<S> extends TableView<S> {


    public FilterTableView() {
        initColumnPredicateListener();
    }

    public FilterTableView(ObservableList<S> items) {
        super(items);
        initColumnPredicateListener();
    }

    private void initColumnPredicateListener() {
        getColumns().addListener((ListChangeListener<TableColumn<S, ?>>) c -> {
            for (TableColumn<S, ?> col : getColumns()) {
                if (col instanceof FilterTableColumn) {
                    FilterTableColumn<S, ?> filterCol = (FilterTableColumn<S, ?>) col;
                    filterCol.predicateProperty().addListener(p -> updatePredicates());
                }
            }
        });
    }

    private final SimpleObjectProperty<Predicate<S>> predicate = new SimpleObjectProperty<>();

    public Predicate<S> getPredicate() {
        return predicate.get();
    }

    public SimpleObjectProperty<Predicate<S>> predicateProperty() {
        return predicate;
    }

    public void setPredicate(Predicate<S> predicate) {
        this.predicate.set(predicate);
    }

    public void addPredicate(Predicate<S> predicate) {
        if (this.predicate.get() == null) {
            this.predicate.set(predicate);
        } else {
            this.predicate.set(this.predicate.get().and(predicate));
        }
    }


    private void updatePredicates() {
        Predicate<S> p = null;
        for (TableColumn<S, ?> col : getColumns()){
            if (col instanceof FilterTableColumn) {
                FilterTableColumn<S, ?> filterCol = (FilterTableColumn<S, ?>) col;
                if (filterCol.getPredicate() != null) {
                    if (p == null) {
                        p = filterCol.getPredicate();
                    } else {
                        p = p.and(filterCol.getPredicate());
                    }
                }
            }
        }
        predicate.set(p);
    }
}
