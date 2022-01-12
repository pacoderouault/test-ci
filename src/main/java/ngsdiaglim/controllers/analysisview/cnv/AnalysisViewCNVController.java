package ngsdiaglim.controllers.analysisview.cnv;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVControlGroup;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.caller.CovCopCNVCaller;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.CNVDefineControls;
import ngsdiaglim.controllers.dialogs.ImportAnalysisDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.CNVControlType;
import ngsdiaglim.modeles.analyse.Analysis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AnalysisViewCNVController extends VBox {

    private final Logger logger = LogManager.getLogger(AnalysisViewCNVController.class);

    @FXML private ToggleGroup cnvViews;
    @FXML private ToggleButton importDataTb;
    @FXML private ToggleButton rawDataTb;
    @FXML private ToggleButton qualityCrontrolTb;
    @FXML private ToggleButton normalizedDataTb;
    @FXML private ToggleButton mapsTb;
    @FXML private Button defineControlsBtn;
    @FXML private Button normalizeBtn;
    @FXML private HBox cnvViewContainer;

    private final Analysis analysis;

    private final CNVImportDataController cnvImportDataController;
    private CNVRawDataController cnvRawDataController;
    private CNVQualityControl cnvQualityControl;
    private CNVNormalizedViewController cnvNormalizedViewController;

    private final SimpleObjectProperty<CovCopCNVData> covcopCNVData = new SimpleObjectProperty<>();

    private CovCopCNVCaller caller;

    public AnalysisViewCNVController(Analysis analysis) {
        this.analysis = analysis;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewCNV.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        cnvImportDataController = new CNVImportDataController(this, analysis);
        covcopCNVData.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                cnvRawDataController = new CNVRawDataController(this, analysis, covcopCNVData.get());
                cnvQualityControl = new CNVQualityControl(this, analysis, covcopCNVData.get());
                rawDataTb.setDisable(false);
                defineControlsBtn.setDisable(false);
                normalizeBtn.setDisable(false);
                qualityCrontrolTb.setDisable(false);
                normalizedDataTb.setDisable(true);
                mapsTb.setDisable(true);
                rawDataTb.setSelected(true);
                caller = new CovCopCNVCaller(covcopCNVData.get());
            } else {
                cnvRawDataController = null;
                rawDataTb.setDisable(true);
                qualityCrontrolTb.setDisable(true);
                defineControlsBtn.setDisable(true);
                normalizeBtn.setDisable(true);
            }
        });
        importDataTb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                cnvViewContainer.getChildren().setAll(cnvImportDataController);
            }
        });
        importDataTb.setSelected(true);
        rawDataTb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                cnvViewContainer.getChildren().setAll(cnvRawDataController);
                HBox.setHgrow(cnvRawDataController, Priority.ALWAYS);
            }
        });
        qualityCrontrolTb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                cnvViewContainer.getChildren().setAll(cnvQualityControl);
                HBox.setHgrow(cnvQualityControl, Priority.ALWAYS);
            }
        });
        normalizedDataTb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                showNormalizationTableView();
            }
        });
        mapsTb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                showNormalizationMapsView();
            }
        });
    }

    public Analysis getAnalysis() {return analysis;}

    public CNVImportDataController getCnvImportDataController() {return cnvImportDataController;}

    public CNVRawDataController getCnvRawDataController() {return cnvRawDataController;}

    public CNVNormalizedViewController getCnvNormalizedViewController() {return cnvNormalizedViewController;}

    public CovCopCNVData getCovcopCNVData() {
        return covcopCNVData.get();
    }

    public SimpleObjectProperty<CovCopCNVData> covcopCNVDataProperty() {
        return covcopCNVData;
    }

    public void setCovcopCNVData(CovCopCNVData covcopCNVData) {
        this.covcopCNVData.set(covcopCNVData);
    }

    @FXML
    private void defineControls() {
        CNVDefineControls dialog = new CNVDefineControls(App.get().getAppController().getDialogPane(), covcopCNVData.get());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            CNVControlType controlType = dialog.getValue().getControlType();
            covcopCNVData.get().setControlType(controlType);
            if (controlType.equals(CNVControlType.SAMPLES)) {
                for (CNVSample sample : covcopCNVData.get().getSamples().values()) {
                    sample.setControl(dialog.getValue().getSamplesControls().contains(sample));
                }
//                covcopCNVData.get().setControlSamples(dialog.getValue().getSamplesControls());
            } else if (controlType.equals(CNVControlType.EXTERNAL)) {
                covcopCNVData.get().setControlGroup(dialog.getValue().getGroupControl());
            }
            Message.hideDialog(dialog);
        });
    }

    @FXML
    private void normalizeData() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("cnvnormalizedview.lb.normalizeData"));
        wid.addTaskEndNotification(r -> {
            if (r == 0) {
                cnvNormalizedViewController = new CNVNormalizedViewController(this, covcopCNVData.get());
                normalizedDataTb.setDisable(false);
                mapsTb.setDisable(false);
                mapsTb.setSelected(true);
            }
        });
        wid.exec("normalizeData", inputParams -> {
            try {
                caller.call();
            } catch (Exception e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
            }
            return 0;
        });
    }


    private void showNormalizationTableView() {
        cnvViewContainer.getChildren().setAll(cnvNormalizedViewController);
        HBox.setHgrow(cnvNormalizedViewController, Priority.ALWAYS);
        cnvNormalizedViewController.showNormalizedTableView();
    }

    private void showNormalizationMapsView() {
        cnvViewContainer.getChildren().setAll(cnvNormalizedViewController);
        HBox.setHgrow(cnvNormalizedViewController, Priority.ALWAYS);
        cnvNormalizedViewController.showNormalizedMapsView();
    }

    public void callCNVs() {
        caller.callCNVs();
    }

    public void clear() {
        if (cnvNormalizedViewController != null) {
            cnvNormalizedViewController.clear();
        }
    }

}
