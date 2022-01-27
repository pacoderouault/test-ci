package ngsdiaglim.controllers.cells.ciq;

import javafx.application.Platform;
import javafx.scene.control.TableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.ciq.CIQModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.font.CIDFontMapping;

import java.sql.SQLException;

public class CIQAnalysisNbCell extends TableCell<CIQModel, Integer> {

    private static final Logger logger = LogManager.getLogger(CIQAnalysisNbCell.class);

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty || item == null) {
            setText(null);
        } else {
            CIQModel m = getTableRow().getItem();
            if (m != null) {
                Platform.runLater(() -> {
                try {
                    int count = DAOController.getCiqAnalysisDAO().countCIQ(m.getId());
                    setText(String.valueOf(count));
                } catch (SQLException e) {
                    logger.error(e);
                    setText(null);
                }
                });
            } else {
                setText(null);
            }
        }
    }
}
