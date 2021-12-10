package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Hotspot;
import org.kordamp.ikonli.javafx.FontIcon;

public class HotspotTableCell extends TableCell<Annotation, Hotspot> {

    @Override
    protected void updateItem(Hotspot item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (item != null && !empty) {
            FontIcon fi = new FontIcon("mdmz-warning");
            fi.setFill(Color.RED);
            setGraphic(fi);

        }
        else {
            setGraphic(null);
        }
    }
}
