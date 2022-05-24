package ngsdiaglim.controllers.analysisview.cnv;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.cnv.*;
import ngsdiaglim.controllers.charts.ViolinChartRun;
import ngsdiaglim.controllers.charts.ViolinChartSample;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.stream.Collectors;

public class CNVQualityControl extends VBox {

    private final static Logger logger = LogManager.getLogger(CNVQualityControl.class);

    @FXML private HBox runBoxplotContainer;
    @FXML private HBox sampleDetailContainer;
    @FXML private HBox poolsDetailContainer;
    @FXML private TextField sampleNameTf;
    @FXML private TextField globalMeanTf;
    @FXML private TextField totalLowAmpliconsTf;
    @FXML private RadioButton femaleRb;
    @FXML private RadioButton maleRb;
    @FXML private Button deleteAllPoolsBtn;

    private final AnalysisViewCNVController analysisViewCNVController;
    private final SimpleObjectProperty<CovCopCNVData> covcopCnvData = new SimpleObjectProperty<>();

    private ViolinChartRun chartRun;

    private final ChangeListener<CNVSample> CNVSampleListener;
    private final ChangeListener<Gender> genderListener;
    private final ChangeListener<Boolean> maleRbListener;

    public CNVQualityControl(AnalysisViewCNVController analysisViewCNVController) {
        this.analysisViewCNVController = analysisViewCNVController;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVQualityControl.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        CNVSampleListener = (obs, oldV, newV) -> {
            if (newV != null) {
                loadSampleDetail(newV);
            }
        };

        maleRbListener = (obs, oldV, newV) -> {
            if (chartRun.getSelectedSample() != null) {
                if (newV) {
                    chartRun.getSelectedSample().setGender(Gender.MALE);
                } else {
                    chartRun.getSelectedSample().setGender(Gender.FEMALE);
                }
            }
        };

        genderListener =  (obs, oldV, newV) -> {
            if (chartRun.getSelectedSample() != null) {
                maleRb.selectedProperty().removeListener(maleRbListener);
                if (newV.equals(Gender.MALE)) {
                    maleRb.setSelected(true);
                } else {
                    femaleRb.setSelected(true);
                }
                maleRb.selectedProperty().addListener(maleRbListener);
            }
        };



        covcopCnvData.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                clearView(oldV);
                initView();
            }
        });
    }

    public CovCopCNVData getCovcopCnvData() {
        return covcopCnvData.get();
    }

    public SimpleObjectProperty<CovCopCNVData> covcopCnvDataProperty() {
        return covcopCnvData;
    }

    public void setCovcopCnvData(CovCopCNVData covcopCnvData) {
        this.covcopCnvData.set(covcopCnvData);
    }

    private void initView() {
        initChartRun();
        maleRb.selectedProperty().addListener(maleRbListener);
    }

    private void clearView(CovCopCNVData oldCovcopCnvData) {
//        maleRb.selectedProperty().removeListener(maleRbListener);
        if (chartRun != null) {
            chartRun.selectedSampleProperty().removeListener(CNVSampleListener);
        }
        if (oldCovcopCnvData != null) {
            for (CNVSample sample : oldCovcopCnvData.getSamples().values()) {
                sample.genderProperty().removeListener(genderListener);
            }
        }
    }

    private void initChartRun() {
        chartRun = new ViolinChartRun(covcopCnvData.get().getSamples(), 200);
        chartRun.drawBoxPlot();
        runBoxplotContainer.getChildren().setAll(chartRun);
        chartRun.selectedSampleProperty().addListener(CNVSampleListener);
        for (CNVSample sample : covcopCnvData.get().getSamples().values()) {
            sample.genderProperty().addListener(genderListener);
        }
        chartRun.selectSample(0);
    }


    private void loadSampleDetail(CNVSample sample) {

        ViolinChartSample sampleChart = new ViolinChartSample(sample, 250);
        sampleChart.setMinValue(chartRun.getMinValue());
        sampleChart.setMaxValue(chartRun.getMaxValue());
        sampleChart.drawBoxPlot();
        sampleDetailContainer.getChildren().setAll(sampleChart);

        sampleNameTf.setText(sample.getBarcode());
        if (sample.getBoxplotData() != null) {
            globalMeanTf.setText(String.valueOf(NumberUtils.round(sample.getBoxplotData().getMean(), 2)));
            totalLowAmpliconsTf.setText(String.valueOf(sample.getBoxplotData().getLowAmpliconsNb()));
        }

        maleRb.selectedProperty().removeListener(maleRbListener);
        maleRb.setSelected(sample.getGender().equals(Gender.MALE));
        femaleRb.setSelected(sample.getGender().equals(Gender.FEMALE));
        maleRb.selectedProperty().addListener(maleRbListener);


        constructPoolsDetail(sample);
    }

    private void constructPoolsDetail(CNVSample sample) {
        poolsDetailContainer.getChildren().clear();
        for (String poolName : sample.getBoxplotDatabyPool().keySet().stream().sorted().collect(Collectors.toList())) {

            BoxplotData boxplotData = sample.getBoxplotDatabyPool().get(poolName);

            GridPane grid = new GridPane();

            grid.setHgap(10);
            grid.setVgap(10);
            ColumnConstraints cc = new ColumnConstraints();
            grid.getColumnConstraints().add(cc);

            int rowIndex = 0;
            // pool name
            Label poolNameLb = new Label(App.getBundle().getString("cnv.qualitycontrol.lb.pooldetail.name"));
            TextField poolNameTf = getDetailPoolTextField(poolName);
            grid.add(poolNameLb, 0, rowIndex);
            grid.add(poolNameTf, 1, rowIndex++);

            Label poolMeanLb = new Label(App.getBundle().getString("cnv.qualitycontrol.lb.pooldetail.meanDepth"));
            Label poolLowAmpLb = new Label(App.getBundle().getString("cnv.qualitycontrol.lb.pooldetail.noCovRegions"));
            TextField poolMeanTf;
            TextField poolLowAmpTf;
            if (boxplotData != null) {
                poolMeanTf = getDetailPoolTextField(String.valueOf(NumberUtils.round(boxplotData.getMean(), 2)));
                poolLowAmpTf = getDetailPoolTextField(String.valueOf(boxplotData.getLowAmpliconsNb()));
            }
            else {
                poolMeanTf = getDetailPoolTextField(null);
                poolLowAmpTf = getDetailPoolTextField(null);
            }
            grid.add(poolMeanLb, 0, rowIndex);
            grid.add(poolMeanTf, 1, rowIndex++);
            grid.add(poolLowAmpLb, 0, rowIndex);
            grid.add(poolLowAmpTf, 1, rowIndex++);

            Button deletePool = new Button(App.getBundle().getString("cnv.qualitycontrol.lb.pooldetail.deletepool"));
            if (boxplotData == null) {
                deletePool.setDisable(true);
            }
            deletePool.getStyleClass().add("button-secondary");
            deletePool.setOnAction(e -> {
                covcopCnvData.get().deletePool(poolName, sample);
                analysisViewCNVController.getCnvRawDataController().refreshTable();
                QualityCalculator.calculateStatistics(covcopCnvData.get());
                GenderCalculator.calculateGender(covcopCnvData.get());
                initView();
                chartRun.selectSample(sample);
            });
            grid.add(deletePool, 0, rowIndex);
            grid.getStyleClass().addAll("cnv-sample-detail");
            poolsDetailContainer.getChildren().add(grid);
        }

    }


    private TextField getDetailPoolTextField(String text) {
        TextField textField = new TextField(text);
        textField.setEditable(false);
        textField.getStyleClass().addAll("textfield-label", "font-medium");
        textField.setPrefWidth(100);
        return textField;
    }


    @FXML
    private void deleteAllPools() {
        CNVSample sample = chartRun.getSelectedSample();
        if (sample != null) {
            DialogPane.Dialog<ButtonType> d = Message.confirm(App.getBundle().getString("cnv.qualitycontrol.msg.confirmdeletepools"));
            d.getButton(ButtonType.YES).setOnAction(e -> {
                covcopCnvData.get().deletePools(sample);
                analysisViewCNVController.getCnvRawDataController().refreshTable();
                QualityCalculator.calculateStatistics(covcopCnvData.get());
                GenderCalculator.calculateGender(covcopCnvData.get());
                initView();
                Message.hideDialog(d);
            });
        }
    }
}
