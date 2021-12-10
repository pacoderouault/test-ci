package ngsdiaglim.controllers.analysisview;


import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ngsdiaglim.App;
import ngsdiaglim.controllers.Module;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.analysisview.cnv.AnalysisViewCNVController;
import ngsdiaglim.controllers.analysisview.reports.bgm.AnalysisViewReportBGMController;
import ngsdiaglim.controllers.dialogs.ChangeAnalysisStateDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.exceptions.NotBiallelicVariant;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;

public class AnalysisViewController extends Module {

    private final static Logger logger = LogManager.getLogger(AnalysisViewController.class);

    @FXML private ComboBox<Analysis> analysisCb;
    @FXML private Label analysisStatusLb;
    @FXML private FontIcon editAnalysisStatusIcon;
    @FXML private ToggleGroup analysisViewsToggleGroup;
    @FXML private ToggleButton variantsViewToggleBtn;
    @FXML private ToggleButton cnvViewToggleBtn;
    @FXML private ToggleButton additionalDataViewToggleBtn;
    @FXML private ToggleButton metaDataViewToggleBtn;
    @FXML private ToggleButton coverageViewToggleBtn;
    @FXML private ToggleButton reportViewToggleBtn;
    @FXML private ToggleButton runinfoViewToggleBtn;
    @FXML private AnchorPane analysisModuleContainer;

    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();

    private AnalysisViewVariantsController2 variantsViewController;
    private AnalysisViewAdditionalData additionalDataController;
    private AnalysisViewMetaDataController metaDataController;
    private AnalysisViewCoverageController coverageController;
    private AnalysisViewCNVController analysisViewCNVController;
    private AnalysisViewReportBGMController reportBGMController;
    private AnalysisViewRunInfoController runInfoController;

    public AnalysisViewController() {
        super(App.getBundle().getString("analysisview.lb.moduletitle"));

        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisView.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
            logger.error(e);
        }

        analysis.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                if (!newV.equals(oldV)) {
                    initView();
                }
            }
            else {
                clearView();
            }
        });
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

    public AnalysisViewVariantsController2 getVariantsViewController() {return variantsViewController;}

    public AnalysisViewAdditionalData getAdditionalDataController() {return additionalDataController;}

    public AnalysisViewReportBGMController getReportBGMController() {return reportBGMController;}

    private void initView() {

        initAnalysisCb();

        variantsViewController = new AnalysisViewVariantsController2(analysis.get());
        additionalDataController = new AnalysisViewAdditionalData(analysis.get());
        metaDataController = new AnalysisViewMetaDataController(analysis.get());
        coverageController = new AnalysisViewCoverageController(analysis.get());
        analysisViewCNVController = new AnalysisViewCNVController(analysis.get());
        reportBGMController = new AnalysisViewReportBGMController(analysis.get());
        runInfoController = new AnalysisViewRunInfoController(analysis.get());

        variantsViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showVariantView();
            }
        }));
        additionalDataViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showAdditionalDataView();
            }
        }));
        metaDataViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showMetaDataView();
            }
        }));
        coverageViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showCoverageDataView();
            }
        }));
        cnvViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showCNVView();
            }
        }));

        reportViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showReportView();
            }
        }));

        runinfoViewToggleBtn.selectedProperty().addListener(((observableValue, oldV, newV) -> {
            if (newV) {
                showRunInfoView();
            }
        }));

        editAnalysisStatusIcon.setOnMouseClicked(e -> {
            if (App.get().getLoggedUser().isPermitted(PermissionsEnum.CHANGE_ANALYSIS_STATE)) {
                ChangeAnalysisStateDialog dialog = new ChangeAnalysisStateDialog(analysis.get());
                Message.showDialog(dialog);
                Button b = dialog.getButton(ButtonType.OK);
                b.setOnAction(event -> {
                    if (dialog.isValid() && dialog.getValue() != null) {
                        try {
                            DAOController.get().getAnalysisDAO().updateAnalysisStatus(analysis.get().getId(), dialog.getValue());
                            analysis.get().setStatus(dialog.getValue());
                            Message.hideDialog(dialog);
                        } catch (SQLException ex) {
                            logger.error(ex);
                            Message.error(ex.getMessage(), ex);
                        }
                    }
                });
            }
        });

        setAnalysisStatus();
        analysis.get().statusProperty().addListener(o -> setAnalysisStatus());

        Platform.runLater(() -> {
            variantsViewToggleBtn.setSelected(true);
            applyCss();
            layout();
            variantsViewController.getTableBuilder().setColumnsHeaderEvent();
            variantsViewController.setDividerPosition();
        });



    }

    private void clearView() {
        analysisCb.setItems(null);
        analysisStatusLb.setText(null);
    }

    private void initAnalysisCb() {
        try {
            analysisCb.setItems(analysis.get().getRun().getAnalyses());
            analysisCb.getSelectionModel().select(analysis.get());
        } catch (SQLException e) {
            logger.error(e);
        }

        analysisCb.valueProperty().addListener((obs, oldV, newV) -> {
            App.get().getAppController().openAnalysis(newV);
//            if (newV != null) {
//                Object[] messageArguments = {newV.getName()};
//                String message = BundleFormatter.format("home.module.analyseslist.msg.openingAnalysis", messageArguments);
//                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), message);
//                wid.addTaskEndNotification(r -> {
//                    if (r == 0) {
//                        App.get().getAppController().showAnalysisView(newV);
//                    }
//                });
//                wid.exec("LoadPanels", inputParam -> {
//                    try {
//                        AnalysisParameters params = DAOController.get().getAnalysisParametersDAO().getAnalysisParameters(newV.getAnalysisParameters().getId());
//                        newV.setAnalysisParameters(params);
//                        VCFParser vcfParser = new VCFParser(newV.getVcfFile(), newV.getAnalysisParameters(), newV.getRun());
//                        vcfParser.parseVCF(true);
//                        newV.setAnnotations(vcfParser.getAnnotations());
//                        newV.loadCoverage();
//                    } catch (IOException | NotBiallelicVariant | SQLException | MalformedCoverageFile e) {
//                        logger.error(e);
//                        Platform.runLater(() -> Message.error(e.getMessage(), e));
//                        return 1;
//                    }
//                    return 0;
//                });
//            }
        });
    }

    private void setAnalysisStatus() {
        if (analysis.get().getStatus().equals(AnalysisStatus.DONE)) {
            analysisStatusLb.setText(AnalysisStatus.DONE.getValue());
            analysisStatusLb.getStyleClass().setAll("analysis_status_complete");
        }
        else {
            analysisStatusLb.setText(AnalysisStatus.INPROGRESS.getValue());
            analysisStatusLb.getStyleClass().setAll("analysis_status_inprogress");
        }
        analysisStatusLb.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.CHANGE_ANALYSIS_STATE));
    }


    private void showVariantView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(variantsViewController);
        setModuleViewAnchors(variantsViewController);
    }

    private void showAdditionalDataView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(additionalDataController);
        setModuleViewAnchors(additionalDataController);
    }

    private void showMetaDataView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(metaDataController);
        setModuleViewAnchors(metaDataController);
    }

    private void showCoverageDataView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(coverageController);
        setModuleViewAnchors(coverageController);
    }

    private void showCNVView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(analysisViewCNVController);
        setModuleViewAnchors(analysisViewCNVController);
    }

    private void showReportView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(reportBGMController);
        setModuleViewAnchors(reportBGMController);
    }

    private void showRunInfoView() {
        analysisModuleContainer.getChildren().clear();
        analysisModuleContainer.getChildren().add(runInfoController);
        setModuleViewAnchors(runInfoController);
    }

    private void setModuleViewAnchors(Pane pane) {
        AnchorPane.setLeftAnchor(pane, 0d);
        AnchorPane.setTopAnchor(pane, 0d);
        AnchorPane.setRightAnchor(pane, 0d);
        AnchorPane.setBottomAnchor(pane, 0d);
    }


}
