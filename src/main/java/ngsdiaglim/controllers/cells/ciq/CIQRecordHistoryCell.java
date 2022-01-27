package ngsdiaglim.controllers.cells.ciq;

import javafx.scene.control.TableCell;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;

public class CIQRecordHistoryCell extends TableCell<CIQVariantRecord, CIQRecordHistory> {
    @Override
    protected void updateItem(CIQRecordHistory item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getUsername());
        }
    }
}
