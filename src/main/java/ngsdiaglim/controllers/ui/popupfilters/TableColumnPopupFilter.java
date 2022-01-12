package ngsdiaglim.controllers.ui.popupfilters;

import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.stage.Window;
import ngsdiaglim.controllers.ui.FilterTableColumn;
import ngsdiaglim.controllers.ui.FilterTableView;
import ngsdiaglim.enumerations.Operators;

public abstract class TableColumnPopupFilter<S, T> extends PopupControl {

    private static final String DEFAULT_STYLE_CLASS = "pop-up-filter";
    private final FilterTableColumn<S, T> tableColumn;

    public TableColumnPopupFilter(FilterTableColumn<S, T> tableColumn) {
        this.tableColumn = tableColumn;
        setAutoHide(true);
        setAutoFix(true);
        setHideOnEscape(true);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public void showPopup() {
        Node node = tableColumn.getGraphic().getParent().getParent();

        if (node.getScene() == null || node.getScene().getWindow() == null) {
            throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window."); //$NON-NLS-1$
        }

        if (isShowing()) {
            return;
        }

        Window parent = node.getScene().getWindow();
        this.show(
                parent,
                parent.getX() + node.localToScene(0, 0).getX() +
                        node.getScene().getX(),
                parent.getY() + node.localToScene(0, 0).getY() +
                        node.getScene().getY() + node.getLayoutBounds().getHeight());

    }

    protected void clearPredicate() {
        tableColumn.setPredicate(null);
        hide();
    }

    protected abstract void updatePredicate(Operators op, T value);

    public FilterTableView<S> getTableView() {
        return tableColumn.getTestTableView();
    }

    public FilterTableColumn<S, T> getTableColumn() {return tableColumn;}
}
