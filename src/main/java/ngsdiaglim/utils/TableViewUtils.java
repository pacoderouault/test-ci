package ngsdiaglim.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class TableViewUtils {


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
}
