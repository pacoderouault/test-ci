package ngsdiaglim.controllers.analysisview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.analysisview.ciq.CIQChart;
import ngsdiaglim.controllers.cells.ciq.CIQRecordAcceptedCell;
import ngsdiaglim.controllers.cells.ciq.CIQRecordCommentCell;
import ngsdiaglim.controllers.cells.ciq.CIQRecordHistoryCell;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.ciq.*;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class AnalysisViewCIQController extends VBox {

    private final static Logger logger = LogManager.getLogger(AnalysisViewCIQController.class);

    @FXML private TextField ciqNameTf;
    @FXML private TextField ciqBarcodeTf;
    @FXML private TextField mutationTf;
    @FXML private TextField vafTargetTf;
    @FXML private TextField meanTf;
    @FXML private TextField sdTf;
    @FXML private Button copyToClipboardBtn;
    @FXML private Button exportToImageBtn;
    @FXML private ListView<CIQHotspot> ciqHotspotLv;
    @FXML private TableView<CIQVariantRecord> ciqRecordTable;
    @FXML private TableColumn<CIQVariantRecord, String> runCol;
    @FXML private TableColumn<CIQVariantRecord, String> analyseCol;
    @FXML private TableColumn<CIQVariantRecord, LocalDateTime> datetimeCol;
    @FXML private TableColumn<CIQVariantRecord, Float> vafCol;
    @FXML private TableColumn<CIQVariantRecord, Integer> dpCol;
    @FXML private TableColumn<CIQVariantRecord, Integer> aoCol;
    @FXML private TableColumn<CIQVariantRecord, CIQRecordHistory> acceptedCol;
    @FXML private TableColumn<CIQVariantRecord, CIQRecordHistory> validatorCol;
    @FXML private TableColumn<CIQVariantRecord, CIQRecordHistory> commentCol;
    @FXML private TableColumn<CIQVariantRecord, Void> actionsCol;
    @FXML private VBox chartContainer;
    private final static Tooltip saveToFileTp = new Tooltip(App.getBundle().getString("cnvnormalizedview.btn.exportToFile"));
    private final static Tooltip copyToClipboardTp = new Tooltip(App.getBundle().getString("cnvnormalizedview.btn.exportToClipboard"));

    private final CIQChart chart = new CIQChart();
    private final HashMap<CIQHotspot, CIQVariantDataSet> recordMap = new HashMap<>();

    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();
    private CIQModel ciqModel;

    public AnalysisViewCIQController() {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewCIQ.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        initView();
        analysis.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                try {
                    updateView();
                } catch (SQLException e) {
                    logger.error(e);
                    Message.error(e.getMessage(), e);
                }
            }
        });
    }

    private void initView() {
        initCIQHotspotLv();
        initCIQRecordTable();

        saveToFileTp.setShowDelay(Duration.ZERO);
        exportToImageBtn.setTooltip(saveToFileTp);
        copyToClipboardTp.setShowDelay(Duration.ZERO);
        copyToClipboardBtn.setTooltip(copyToClipboardTp);

    }

    private void initCIQHotspotLv() {
        ciqHotspotLv.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            fillHotspotDetail(newV);
        });
    }

    private void fillHotspotDetail(CIQHotspot ciqHotspot) {
        if (ciqHotspot == null) {
            mutationTf.setText(null);
            vafTargetTf.setText(null);
            meanTf.setText(null);
            sdTf.setText(null);

            ciqRecordTable.getItems().clear();
//            chartContainer.getChildren().clear();

        } else {
            mutationTf.setText(ciqHotspot.getHGVS());
            vafTargetTf.setText(String.valueOf(ciqHotspot.getVafTarget()));

            CIQVariantDataSet dataset = recordMap.get(ciqHotspot);
            fillHotspotsStatsFields(dataset);
            ciqRecordTable.getItems().setAll(dataset.getCiqRecords());
            chart.setDataset(dataset);
//            chart = new CIQChart(dataset);
            chartContainer.getChildren().setAll(chart);
        }
    }

    private void initCIQRecordTable() {
        runCol.setCellValueFactory(data -> data.getValue().getAnalysis().getRun().nameProperty());
        analyseCol.setCellValueFactory(data -> data.getValue().getAnalysis().nameProperty());
        datetimeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAnalysis().getCreationDate()));
        datetimeCol.setCellFactory(data -> new TableCell<>(){
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(DateFormatterUtils.formatLocalDateTime(item));
                }
            }
        });
        datetimeCol.setComparator(datetimeCol.getComparator().reversed());
        vafCol.setCellValueFactory(data -> data.getValue().vafProperty().asObject());
        vafCol.setCellFactory(data -> new TableCell<>(){
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(NumberUtils.round(item, 3)));
                }
            }
        });
        dpCol.setCellValueFactory(data -> data.getValue().dpProperty().asObject());
        aoCol.setCellValueFactory(data -> data.getValue().aoProperty().asObject());
        acceptedCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastHistory()));
        acceptedCol.setCellFactory( data -> new CIQRecordAcceptedCell());
        commentCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastHistory()));
        commentCol.setCellFactory(data -> new CIQRecordCommentCell());
        validatorCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastHistory()));
        validatorCol.setCellFactory(data -> new CIQRecordHistoryCell());

        ciqRecordTable.getSortOrder().add(datetimeCol);

        ciqRecordTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                chart.unhighlight();
            } else {
                chart.highLightDatapoint(newV);
            }
        });
    }

    private void updateView() throws SQLException {
        if (!DAOController.getCiqAnalysisDAO().analysisIsCIQ(analysis.get().getId())) {
            this.setDisable(true);
            clearView();
        } else {
            this.setDisable(false);
            loadCIQ();
            loadRecordDatasets();
            ciqNameTf.setText(ciqModel.getName());
            ciqBarcodeTf.setText(ciqModel.getBarcode());
            ciqHotspotLv.getItems().setAll(ciqModel.getHotspots());

            if (!ciqHotspotLv.getItems().isEmpty()) {
                ciqHotspotLv.getSelectionModel().select(0);
            }
            ciqRecordTable.sort();
        }
    }

    private void loadCIQ() throws SQLException {
        ciqModel = DAOController.getCiqAnalysisDAO().getCIQModel(analysis.get().getId());
        ciqModel.setHotspots(DAOController.getCiqHotspotDAO().getCIQHotspots(ciqModel));
    }

    private void loadRecordDatasets() throws SQLException {
        for (CIQHotspot ciqHotspot : ciqModel.getHotspots()) {
            List<CIQVariantRecord> records = DAOController.getCiqRecordDAO().getCIQRecords(ciqHotspot);
            CIQVariantDataSet dataset = new CIQVariantDataSet(ciqHotspot, records);
            dataset.computeStats();
            recordMap.put(ciqHotspot, dataset);
        }
    }


    public void fillHotspotsStatsFields(CIQVariantDataSet dataset) {
        if (dataset != null) {
            meanTf.setText(String.valueOf(NumberUtils.round(dataset.getMean(), 3)));
            sdTf.setText(String.valueOf(NumberUtils.round(dataset.getSd(), 3)));
        } else {
            meanTf.setText(null);
            sdTf.setText(null);
        }
    }


    public void clearView() {
        ciqNameTf.setText(null);
        ciqBarcodeTf.setText(null);
        if (chart != null) {
            chart.setDataset(null);
            chart.setDisable(true);
        }
        ciqHotspotLv.getItems().clear();
    }



    @FXML
    private void exportToFile() {

        CIQVariantDataSet hotspotDataset = getShowedDataset();
        if (hotspotDataset != null) {
            FileChooser fc = FileChooserUtils.getFileChooser();
            fc.setInitialFileName(ciqModel.getName() + "_" + hotspotDataset.getCiqHotspot().getName() + "_" + DateFormatterUtils.formatLocalDate(LocalDate.now(), "YYMMdd") +".png");
            File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
                user.savePreferences();
                chart.screenshotToFile(selectedFile);
            }
        }
    }

    @FXML
    private void exportToClipBoard() {
        chart.screenshotToClipboard();
    }

    public Analysis getAnalysis() {
        return analysis.get();
    }

    public SimpleObjectProperty<Analysis> analysisProperty() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis.set(analysis);
    }

    public CIQChart getChart() {return chart;}

    public CIQVariantDataSet getShowedDataset() {
        return recordMap.get(ciqHotspotLv.getSelectionModel().getSelectedItem());
    }
}
