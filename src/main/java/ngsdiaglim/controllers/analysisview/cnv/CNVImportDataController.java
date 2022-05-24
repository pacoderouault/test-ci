package ngsdiaglim.controllers.analysisview.cnv;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.parsers.AmpliconMatrixParser;
import ngsdiaglim.cnv.parsers.CaptureDepthParser;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.FileFormatException;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CNVImportDataController extends VBox {

    private static final Logger logger = LogManager.getLogger(CNVImportDataController.class);
    @FXML private ComboBox<Panel> ampliconPanelCb;
    @FXML private ComboBox<Panel> capturePanelCb;
    @FXML private TextField captureMatrixPathTf;
    @FXML private Spinner<Integer> windowSizeSpinner;
    @FXML private TabPane algoTabPane;
    @FXML private Tab ampliconTab;
    @FXML private Tab captureTab;

    @FXML private Button importDataBtn;
    private final AnalysisViewCNVController analysisViewCNVController;

    private final SimpleObjectProperty<File> matrixFile = new SimpleObjectProperty<>();

    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();

    public CNVImportDataController(AnalysisViewCNVController analysisViewCNVController) {
        this.analysisViewCNVController = analysisViewCNVController;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVImportData.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initViews();

        analysis.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                updateView();
            }
        });

        importDataBtn.disableProperty().bind(matrixFile.isNull());
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

    private void initViews() {
        matrixFile.addListener((obs, oldV, newV) -> {
            if (newV == null) {
                captureMatrixPathTf.setText(null);
            } else {
                captureMatrixPathTf.setText(newV.getPath());
            }
        });
//        matrixFile.set(new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/RunProton/Auto_BGM-CMT-021219_s5-torrent-server-vm_219.bcmatrix.xls"));
//        initPanelsCombobox();
        initWindowSizeSpinner();

//        // set default algo from library enrichment
//        if (analysis.getAnalysisParameters().getTargetEnrichment().equals(TargetEnrichment.CAPTURE)) {
//            algoTabPane.getSelectionModel().select(captureTab);
//        } else {
//            algoTabPane.getSelectionModel().select(ampliconTab);
//        }
    }

    private void updateView() {
        initPanelsCombobox();
        // set default algo from library enrichment
        if (analysis.get().getAnalysisParameters().getTargetEnrichment().equals(TargetEnrichment.CAPTURE)) {
            algoTabPane.getSelectionModel().select(captureTab);
        } else {
            algoTabPane.getSelectionModel().select(ampliconTab);
        }
    }

    private void initPanelsCombobox() {
        try {
            ObservableList<Panel> panels = DAOController.getPanelDAO().getPanels();
            capturePanelCb.setItems(panels);
            ampliconPanelCb.setItems(panels);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage(), e);
        }

        capturePanelCb.getSelectionModel().select(analysis.get().getAnalysisParameters().getPanel());
        ampliconPanelCb.getSelectionModel().select(analysis.get().getAnalysisParameters().getPanel());
    }

    private void initWindowSizeSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 10000, 150, 10);
        windowSizeSpinner.setValueFactory(valueFactory);
    }


    @FXML
    private void importMatrixHandler() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            matrixFile.set(selectedFile);
        }
    }

    @FXML
    private void importAmpliconDataHandler() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("cnv.importdata.lb.importingMatrix"));
        wid.exec("loadMatrix", inputParams -> {
            AmpliconMatrixParser ampliconMatrixParser = new AmpliconMatrixParser(analysis.get(), ampliconPanelCb.getValue(), matrixFile.get());
            try {
                CovCopCNVData cnvData = ampliconMatrixParser.parseMatrixFile();
                cnvData.setAlgorithm(TargetEnrichment.AMPLICON);
                Platform.runLater(() -> analysisViewCNVController.setCovcopCNVData(cnvData));
            } catch (FileFormatException | SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> Message.error(e.getMessage(), e));
                return 1;
            }
            return 0;
        });

    }

    @FXML
    private void importCaptureDataHandler() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("cnv.importdata.lb.importingDepth"));
        wid.exec("loadMatrix", inputParams -> {
            CaptureDepthParser captureDepthParser = new CaptureDepthParser(analysis.get(), capturePanelCb.getValue(), windowSizeSpinner.getValue());
            try {
                CovCopCNVData cnvData = captureDepthParser.parseDepthFiles();
                cnvData.setAlgorithm(TargetEnrichment.CAPTURE);
                cnvData.setWindowsSize(windowSizeSpinner.getValue());
                Platform.runLater(() -> analysisViewCNVController.setCovcopCNVData(cnvData));
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> Message.error(e.getMessage(), e));
                return 1;
            }
            return 0;
        });


    }

}
