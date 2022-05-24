package ngsdiaglim.controllers.cells;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import ngsdiaglim.modeles.ciq.CIQModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Optional;

public class ImportAnalysisCIQTableCell extends TableCell<AnalysisInputData, CIQModel> {

    private static final Logger logger = LogManager.getLogger(ImportAnalysisCIQTableCell.class);
    private static ObservableList<CIQModel> ciqModels;
    private final ComboBox<CIQModel> ciqModelCb = new ComboBox<>();
    private final ChangeListener<CIQModel> ciqModelChangeListener;

    public ImportAnalysisCIQTableCell() {
        try {
            ciqModels = DAOController.getCiqModelDAO().getActiveCIQModels();
            ciqModels.add(0, null);
            ciqModelCb.setItems(ciqModels);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            ciqModels = FXCollections.observableArrayList();
        }

        ciqModelChangeListener = (obs, olDv, newV) -> setCIQModel(newV);
        ciqModelCb.getSelectionModel().selectedItemProperty().addListener(ciqModelChangeListener);
    }

    @Override
    protected void updateItem(CIQModel item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(ciqModelCb);
            ciqModelCb.getSelectionModel().selectedItemProperty().removeListener(ciqModelChangeListener);
            ciqModelCb.getSelectionModel().select(item);
            ciqModelCb.getSelectionModel().selectedItemProperty().addListener(ciqModelChangeListener);
        }
    }

    private void setCIQModel(CIQModel ciqModel) {
        AnalysisInputData data = getTableRow().getItem();
        if (data != null) {
            data.setCiqModel(ciqModel);
        }
    }

    private void selectCIQModel() {
        AnalysisInputData data = getTableRow().getItem();
        if (data != null) {
            Optional<CIQModel> opt = ciqModelCb.getItems().stream().filter(m -> (m != null && m.getBarcode().equalsIgnoreCase(data.getSampleName()))).findAny();
            if (opt.isPresent()) {
                ciqModelCb.getSelectionModel().select(opt.get());
            } else {
                ciqModelCb.getSelectionModel().select(null);
            }
        }
    }
}
