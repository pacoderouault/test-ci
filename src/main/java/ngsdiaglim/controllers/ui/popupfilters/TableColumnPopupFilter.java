package ngsdiaglim.controllers.ui.popupfilters;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.stage.Window;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public abstract class TableColumnPopupFilter<S, T> extends PopupControl {

    private static final String DEFAULT_STYLE_CLASS = "pop-up-filter";
    private final FilteredTableColumn<S, T> tableColumn;

    public TableColumnPopupFilter(FilteredTableColumn<S, T> tableColumn) {
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

    public FilteredTableColumn<S, T> getTableColumn() {return tableColumn;}
}
