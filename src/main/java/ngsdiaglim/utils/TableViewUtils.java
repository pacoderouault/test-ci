package ngsdiaglim.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.*;

import java.text.NumberFormat;

public class TableViewUtils {

    private static final NumberFormat numberFormatter = NumberFormat.getNumberInstance();

    public static String getColmunTitle(TableColumn<?, ?> col) {
        if (col == null) return null;
        String title = col.getText();
        if (title == null || title.isEmpty()) {
            if (col.getGraphic() instanceof Label) {
                title = ((Label) col.getGraphic()).getText();
            }
        }
        return title;
    }

    /**
     * Install the keyboard handler:
     *   + CTRL + C = copy to clipboard
     */
    public static void installCopyHandler(TableView<?> table) {
        // install copy/paste keyboard handler
        table.setOnKeyPressed(new TableKeyEventHandler());
    }


    /**
     * Copy/Paste keyboard event handler.
     * The handler uses the keyEvent's source for the clipboard data. The source must be of type TableView.
     */
    public static class TableKeyEventHandler implements EventHandler<KeyEvent> {
        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        public void handle(final KeyEvent keyEvent) {
            if (copyKeyCodeCompination.match(keyEvent)) {
                if (keyEvent.getSource() instanceof TableView) {
                    // copy to clipboard
                    copySelectionToClipboard((TableView<?>) keyEvent.getSource());
                    // event is handled, consume it
                    keyEvent.consume();
                }
            }
        }
    }

    /**
     * Get table selection and copy it to the clipboard.
     *
     */
    public static void copySelectionToClipboard(TableView<?> table) {

        StringBuilder clipboardString = new StringBuilder();

        ObservableList<TablePosition> positionList = table.getSelectionModel().getSelectedCells();

        int prevRow = -1;

        for (TablePosition position : positionList) {

            int row = position.getRow();
            int col = position.getColumn();

            // determine whether we advance in a row (tab) or a column
            // (newline).
            if (prevRow == row) {
                clipboardString.append('\t');
            } else if (prevRow != -1) {
                clipboardString.append('\n');
            }

            // create string from cell
            String text;

            Object observableValue = table.getVisibleLeafColumns().get(col).getCellObservableValue(row);
            // null-check: provide empty string for nulls
            if (observableValue == null) {
                text = "";
            } else if (observableValue instanceof DoubleProperty) { // TODO: handle boolean etc
                text = numberFormatter.format(((DoubleProperty) observableValue).get());
            } else if (observableValue instanceof IntegerProperty) {
                text = numberFormatter.format(((IntegerProperty) observableValue).get());
            } else if (observableValue instanceof StringProperty) {
                StringProperty value = (StringProperty) observableValue;
                if (value.getValue() == null) {
                    text = "";
                } else {
                    text = value.get();
                }
            } else if (observableValue instanceof ObjectProperty) {
                ObjectProperty<?> value = (ObjectProperty<?>) observableValue;
                if (value.getValue() == null) {
                    text = "";
                } else {
                    text = ((ObjectProperty<?>) observableValue).getValue().toString();
                }
            } else {
                text = "";
            }

            // add new item to clipboard
            clipboardString.append(text);

            // remember previous
            prevRow = row;
        }

        // create clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());

        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

}
