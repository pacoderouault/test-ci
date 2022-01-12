package ngsdiaglim.controllers.analysisview;


import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ngsdiaglim.App;
import ngsdiaglim.controllers.Module;
import ngsdiaglim.controllers.analysisview.cnv.AnalysisViewCNVController;
import ngsdiaglim.controllers.analysisview.reports.bgm.AnalysisViewReportBGMController;
import ngsdiaglim.controllers.dialogs.ChangeAnalysisStateDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.enumerations.Service;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.ScrollBarUtil;
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

    private ChangeListener<AnalysisStatus> analysisStatusChangeListener;

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
                clearView(oldV);
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

    public AnalysisViewCNVController getAnalysisViewCNVController() {return analysisViewCNVController;}

    private void initView() {

        initAnalysisCb();
        if (variantsViewController != null) {
            variantsViewController.clear();
        }
        variantsViewController = new AnalysisViewVariantsController2(analysis.get());
        additionalDataController = new AnalysisViewAdditionalData(analysis.get());
        metaDataController = new AnalysisViewMetaDataController(analysis.get());
        coverageController = new AnalysisViewCoverageController(analysis.get());
        analysisViewCNVController = new AnalysisViewCNVController(analysis.get());

        if (App.get().getService().equals(Service.BGM)) {
            reportBGMController = new AnalysisViewReportBGMController(analysis.get());
        } else {
            reportViewToggleBtn.setVisible(false);
            reportViewToggleBtn.setManaged(false);
        }
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
                            DAOController.getAnalysisDAO().updateAnalysisStatus(analysis.get().getId(), dialog.getValue());
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

        analysisStatusChangeListener = (obs, oldV, newV) -> setAnalysisStatus();

        analysis.get().statusProperty().addListener(analysisStatusChangeListener);

        Platform.runLater(() -> {
            variantsViewToggleBtn.setSelected(true);
            applyCss();
            layout();
            variantsViewController.getTableBuilder().setColumnsHeaderEvent();
            variantsViewController.setDividerPosition();
            System.out.println(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.USE_SMOOTH_SCROLLING));
            if (Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.USE_SMOOTH_SCROLLING))) {
                ScrollBarUtil.smoothScrollingTableView(variantsViewController.getVariantsTable(), (1.0 / variantsViewController.getVariantsTable().getItems().size()) * 2.0);
            }
        });

    }

    private void clearView(Analysis oldV) {
        oldV.clear();
        oldV.statusProperty().removeListener(analysisStatusChangeListener);
        analysisCb.setItems(null);
        analysisStatusLb.setText(null);
        variantsViewController.clear();
        analysisViewCNVController.clear();
        if (reportBGMController != null) {
            reportBGMController.clear();
        }
    }

    private void initAnalysisCb() {
        try {
            analysisCb.setItems(analysis.get().getRun().getAnalyses());
            analysisCb.getSelectionModel().select(analysis.get());
        } catch (SQLException e) {
            logger.error(e);
        }

        analysisCb.valueProperty().addListener((obs, oldV, newV) -> App.get().getAppController().openAnalysis(newV));
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
        if (App.get().getService().equals(Service.BGM)) {
            analysisModuleContainer.getChildren().clear();
            analysisModuleContainer.getChildren().add(reportBGMController);
            setModuleViewAnchors(reportBGMController);
        }
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
