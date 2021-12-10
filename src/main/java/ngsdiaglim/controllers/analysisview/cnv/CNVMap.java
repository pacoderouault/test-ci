package ngsdiaglim.controllers.analysisview.cnv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.utils.FileChooserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CNVMap extends VBox {

    private final static Logger logger = LogManager.getLogger(CNVMap.class);

    @FXML private Label sampleNameLb;
    @FXML private Button genderBtn;
    @FXML private Button exportsAsImageBtn;
    @FXML private HBox chartContainer;
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
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();
    }

    public CNVChart getCnvChart() {return cnvChart;}

    private void initView() {
        sampleNameLb.textProperty().bind(sample.barcodeProperty());
//        genderBtn.prefHeightProperty().bind(exportsAsImageBtn.heightProperty());
        setGenderBtnText(sample.getGender());
        sample.genderProperty().addListener((obs, oldV, newV) -> {
            setGenderBtnText(newV);
        });
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

}
