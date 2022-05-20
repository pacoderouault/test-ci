package ngsdiaglim.controllers.cells.coverageTreetableCell;

import javafx.scene.control.Button;
import javafx.scene.control.TreeTableCell;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.coverageTableCells.CoverageActionTableCell;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageTreeItem;
import ngsdiaglim.modeles.igv.IGVLinks2;
import ngsdiaglim.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CoverageActionsTreeTableCell extends TreeTableCell<SpecificCoverageTreeItem, SpecificCoverageTreeItem> {

    private final static Logger logger = LogManager.getLogger(CoverageActionsTreeTableCell.class);
    private final Button igvbtn = new Button("IGV");

    public CoverageActionsTreeTableCell() {
        igvbtn.getStyleClass().add("button-action-cell");
        igvbtn.setOnAction(e -> viewRegionOnIGV());
    }

    @Override
    protected void updateItem(SpecificCoverageTreeItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(igvbtn);
        }
    }

    private void viewRegionOnIGV() {
        IGVLinks2 igv = App.get().getIgvLinks2();
        SpecificCoverageTreeItem item = getItem();
        if (item != null) {
            if (item.getCoverageRegion() == null) {
                try {
                    igv.goTo(ModuleManager.getAnalysisViewController().getAnalysis(),
                            item.getSpecificCoverage().getContig(),
                            item.getSpecificCoverage().getStart(),
                            item.getSpecificCoverage().getEnd());
                } catch (Exception e) {
                    logger.error(e);
                    Message.error(e.getMessage(), App.getBundle().getString("app.msg.err.igvnotreponding"));
                }
            } else {
                try {
                    igv.goTo(ModuleManager.getAnalysisViewController().getAnalysis(),
                            item.getCoverageRegion().getContig(),
                            item.getCoverageRegion().getStart(),
                            item.getCoverageRegion().getEnd());
                } catch (Exception e) {
                    logger.error(e);
                    Message.error(e.getMessage(), App.getBundle().getString("app.msg.err.igvnotreponding"));
                }

            }
        }
    }
}