package ngsdiaglim.controllers.analysisview.cnv;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.CNVChartHeight;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.h2.util.Tool;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;

public class CNVMap extends VBox {

    private final static Logger logger = LogManager.getLogger(CNVMap.class);

    @FXML private Label sampleNameLb;
    @FXML private Button genderBtn;
    @FXML private Button resetZoomBtn;
    @FXML private Button saveChartToFileBtn;
    @FXML private Button copyToClipBoardbtn;
    @FXML private HBox chartContainer;
    private final static Tooltip changerGenderTp = new Tooltip(App.getBundle().getString("cnvnormalizedview.btn.changeGener"));
    private final static Tooltip resetZoomTp = new Tooltip(App.getBundle().getString("cnvnormalizedview.btn.resetZoom"));
    private final static Tooltip saveToFileTp = new Tooltip(App.getBundle().getString("cnvnormalizedview.btn.exportToFile"));
    private final static Tooltip copyToClipboardTp = new Tooltip(App.getBundle().getString("cnvnormalizedview.btn.exportToClipboard"));

    private ChangeListener<Gender> genderListener;

    private final CovCopCNVData cnvData;
    private final CNVSample sample;
    private final CNVChart cnvChart;
    private final CNVNormalizedMapsViewController cnvNormalizedMapsViewController;

    public CNVMap(CNVNormalizedMapsViewController cnvNormalizedMapsViewController, CovCopCNVData cnvData, CNVSample sample) {
        this.cnvNormalizedMapsViewController = cnvNormalizedMapsViewController;
        this.cnvData = cnvData;
        this.sample = sample;
        this.cnvChart = new CNVChart(cnvData, sample);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVMap.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();
    }

    public CNVChart getCnvChart() {return cnvChart;}

    private void initView() {
        changerGenderTp.setShowDelay(Duration.ZERO);
        genderBtn.setTooltip(changerGenderTp);
        resetZoomTp.setShowDelay(Duration.ZERO);
        resetZoomBtn.setTooltip(resetZoomTp);
        saveToFileTp.setShowDelay(Duration.ZERO);
        saveChartToFileBtn.setTooltip(saveToFileTp);
        copyToClipboardTp.setShowDelay(Duration.ZERO);
        copyToClipBoardbtn.setTooltip(copyToClipboardTp);

        sampleNameLb.textProperty().bind(sample.barcodeProperty());
////        genderBtn.prefHeightProperty().bind(exportsAsImageBtn.heightProperty());
        setGenderBtnText(sample.getGender());

        genderListener = (obs, oldV, newV) -> {
            setGenderBtnText(newV);
        };
        sample.genderProperty().addListener(genderListener);
    }

    public void drawChart() {
        cnvChart.drawChart();
        chartContainer.getChildren().setAll(cnvChart);
        HBox.setHgrow(chartContainer, Priority.ALWAYS);
    }

    private void setGenderBtnText(Gender gender) {
        if (gender != null && gender.equals(Gender.MALE)) genderBtn.setText("XY");
        else genderBtn.setText("XX");
    }

    @FXML
    private void switchGender() {
        if (sample.getGender().equals(Gender.FEMALE)) sample.setGender(Gender.MALE);
        else sample.setGender(Gender.FEMALE);
    }


    @FXML
    private void exportToFile() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        fc.setInitialFileName("CNVs_" + sample.getBarcode() + ".png");
        File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            cnvChart.screenshotToFile(selectedFile);
        }
    }

    @FXML
    private void exportToClipBoard() {
        cnvChart.screenshotToClipboard();
    }

    @FXML
    public void resetZoomAllCharts() {
        cnvNormalizedMapsViewController.resetZoom();
    }

    @FXML
    public void resetZoom() {
        cnvChart.resetZoom();
    }

    public void clear() {
        sampleNameLb.textProperty().unbind();
        sample.genderProperty().removeListener(genderListener);
        chartContainer.getChildren().clear();
    }

}
