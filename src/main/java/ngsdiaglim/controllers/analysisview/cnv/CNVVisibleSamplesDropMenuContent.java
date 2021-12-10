package ngsdiaglim.controllers.analysisview.cnv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.controllers.dialogs.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CNVVisibleSamplesDropMenuContent extends VBox {

    private static final Logger logger = LogManager.getLogger(CNVVisibleSamplesDropMenuContent.class);

    @FXML private ListView<CNVSample> lv;
    private final CovCopCNVData cnvData;

    public CNVVisibleSamplesDropMenuContent(CovCopCNVData cnvData) {
        this.cnvData = cnvData;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CNVVisibleSamplesDropDownMenuContent.fxml"), App.getBundle());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    @FXML
    public void initialize() {
        lv.setCellFactory(data -> new Cell());
        fillListView();
    }

    private void fillListView() {
        cnvData.getSamples().forEach((k, v) -> {
            lv.getItems().add(v);
        });
    }

    @FXML
    private void selectAllSamples() {
        lv.getItems().forEach(s -> s.setVisible(true));
    }


    @FXML
    private void unselectAllSamples() {
        lv.getItems().forEach(s -> s.setVisible(false));
    }


    private static class Cell extends ListCell<CNVSample> {
        @Override
        protected void updateItem(CNVSample item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);

            if (empty || item == null) {
                setGraphic(null);
            }
            else {
                final CheckBox cb = new CheckBox();
                cb.setText(item.getBarcode());
                cb.selectedProperty().bindBidirectional(item.visibleProperty());
                setGraphic(cb);
            }
        }
    }
}