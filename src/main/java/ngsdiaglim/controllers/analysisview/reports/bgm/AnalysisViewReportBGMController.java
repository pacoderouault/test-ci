package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.reports.bgm.ReportCreator;
import ngsdiaglim.modeles.reports.bgm.ReportData;
import ngsdiaglim.modeles.reports.bgm.ReportDataBuilder;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnalysisViewReportBGMController extends VBox {

    private static final Logger logger = LogManager.getLogger(AnalysisViewReportBGMController.class);

    @FXML private HBox reportModuleContainer;
    @FXML private Button gotToPreviousStepBtn;
    @FXML private Button gotToNextStepBtn;
    @FXML private Button createReportBtn;

    private final SimpleIntegerProperty paneIdx = new SimpleIntegerProperty(-1);
    private final List<ReportPane> reportPanes = new ArrayList<>();

    private ReportPersonalInformation reportPersonalInformation;
    private ReportSelectGenes reportSelectGenes;
    private ReportSelectVariants reportSelectVariants;
//    private final ReportVous reportVous;
    private ReportComments reportComments;

    private ChangeListener<Number> paneIdxListener;

    private final Analysis analysis;

    public AnalysisViewReportBGMController(Analysis analysis) {
        this.analysis = analysis;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewReportBGM.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (
                IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        reportPersonalInformation = new ReportPersonalInformation(this);
        reportSelectGenes = new ReportSelectGenes(this);
        reportSelectVariants = new ReportSelectVariants(this);
        reportComments = new ReportComments(this);

        reportPanes.add(reportPersonalInformation);
        reportPanes.add(reportSelectGenes);
        reportPanes.add(reportSelectVariants);
        reportPanes.add(reportComments);

        init();
    }


    private void init() {
        paneIdxListener = (obs, oldV, newV) -> {
            if (newV.intValue() >= 0 && newV.intValue() < reportPanes.size()) {
                reportModuleContainer.getChildren().setAll(reportPanes.get(newV.intValue()));
                gotToPreviousStepBtn.setDisable(newV.intValue() <= 0);
                gotToNextStepBtn.setDisable(newV.intValue() >= reportPanes.size() - 1);
                createReportBtn.setDisable(newV.intValue() < reportPanes.size() - 1);
            }

        };
        paneIdx.addListener(paneIdxListener);
        paneIdx.setValue(0);
    }


    @FXML
    private void gotToPreviousStep() {
        paneIdx.setValue(paneIdx.getValue() - 1);
    }


    @FXML
    private void goToNextStep() {
        paneIdx.setValue(paneIdx.getValue() + 1);
    }


    public Analysis getAnalysis() {return analysis;}

    public ReportPersonalInformation getReportPersonalInformation() {return reportPersonalInformation;}

    public ReportSelectGenes getReportSelectGenes() {return reportSelectGenes;}

    public ReportSelectVariants getReportSelectVariants() {return reportSelectVariants;}

//    public ReportVous getReportVous() {return reportVous;}

    public ReportComments getReportComments() {return reportComments;}

    public ReportData getReportData() {
        return new ReportDataBuilder()
                .setAnalysis(analysis)
                .setRun(analysis.getRun())
                .setGender(reportPersonalInformation.getGender())
                .setFirstName(reportPersonalInformation.getFirstName())
                .setLastName(reportPersonalInformation.getLastName())
                .setMaidenName(reportPersonalInformation.getMaidenNameName())
                .setIsChild(reportPersonalInformation.isChild())
                .setBarcode(reportPersonalInformation.getBarcode())
                .setBirthdate(reportPersonalInformation.getBirthdate())
                .setPrescriber(reportPersonalInformation.getPrescriber())
                .setSamplingType(reportPersonalInformation.getSamplingType())
                .setSamplingDate(reportPersonalInformation.getSamplingDate())
                .setSamplingArrivedDate(reportPersonalInformation.getSamplingArrivedDate())
                .setGenesList(reportSelectGenes.getGenes())
                .setReportedVariants(reportSelectVariants.getReportedVariants())
                .setCommentaries(reportComments.getCommentaries())
                .createReportData();
    }


    private boolean checkErrors() {
        for (int i = 0; i < reportPanes.size(); i++) {
            String error = reportPanes.get(i).checkForm();
            if (error != null) {
                paneIdx.setValue(i);
                Message.error(error);
                return true;
            }
        }
        return false;
    }


    private String setReportInitialName() {
        final String delim = "_";
        return analysis.getRun().getName() + delim +
                reportPersonalInformation.getBarcode() + delim +
                reportPersonalInformation.getFirstName() + delim +
                reportPersonalInformation.getLastName() +
                ".docx";
    }


    @FXML
    private void createReport() {
        boolean isError = checkErrors();
        if (!isError) {
            FileChooser fc = FileChooserUtils.getFileChooser();
            fc.setInitialFileName(setReportInitialName());
            File reportFile = fc.showSaveDialog(App.getPrimaryStage());
//            File reportFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/test.docx");
            if (reportFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(reportFile));
                user.savePreferences();
                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("analysisviewreports.msg.createReport"));
                wid.addTaskEndNotification(r -> {

                });
                wid.exec("createReport", inputParams -> {
                    try {
                        ReportData reportData = getReportData();
                        ReportCreator reportCreator = new ReportCreator(reportData, reportFile);
                        reportCreator.createReport();
                    } catch (Exception e) {
                        logger.error(e);
                        Platform.runLater(() -> Message.error(e.getMessage(), e));
                        return 1;
                    }

                    return 0;
                });
            }
        }
    }

    public void clear() {
        paneIdx.removeListener(paneIdxListener);
        reportPersonalInformation.clear();
        reportSelectGenes.clear();
        reportSelectVariants.clear();
        reportComments.clear();

        reportPersonalInformation = null;
        reportSelectGenes = null;
        reportSelectVariants = null;
        reportComments = null;

        reportPanes.clear();
        reportModuleContainer.getChildren().clear();
    }
}
