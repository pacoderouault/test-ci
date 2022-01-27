package ngsdiaglim.controllers.cells.coverageTableCells;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.igv.IGVLinks;
import ngsdiaglim.modeles.igv.IGVLinks2;
import ngsdiaglim.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CoverageActionTableCell extends TableCell<CoverageRegion, Void> {

    private final static Logger logger = LogManager.getLogger(CoverageActionTableCell.class);
    private final Button igvbtn = new Button("IGV");

    public CoverageActionTableCell() {
        igvbtn.getStyleClass().add("button-action-cell");
        igvbtn.setOnAction(e -> viewRegionOnIGV());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (!empty) {
            setGraphic(igvbtn);
        } else {
            setGraphic(null);
        }
    }

    private void viewRegionOnIGV() {
//        IGVHandler igv = App.get().getIgvHandler();
        IGVLinks2 igv = App.get().getIgvLinks2();
        CoverageRegion cr = getTableRow().getItem();
        if (cr != null) {
            try {
                igv.goTo(ModuleManager.getAnalysisViewController().getAnalysis(), cr.getContig(), cr.getStart(), cr.getEnd());
            } catch (IOException e) {
                logger.error(e);
                Message.error(e.getMessage(), App.getBundle().getString("app.msg.err.igvnotreponding"));
            }
        }
    }
}
