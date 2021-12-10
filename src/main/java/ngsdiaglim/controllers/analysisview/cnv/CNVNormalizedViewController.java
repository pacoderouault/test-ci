package ngsdiaglim.controllers.analysisview.cnv;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;

public class CNVNormalizedViewController extends VBox {

    private static final Logger logger = LogManager.getLogger(CNVNormalizedViewController.class);

    @FXML private ToggleButton autoCNVDetection;
    private RangeSlider delDupThresholdRs;
    @FXML private TextField delThresholdLb;
    @FXML private TextField dupThresholdLb;
    @FXML private HBox deldupManualContainer;
    @FXML private HBox rangeSliderContainer;
    @FXML private Button showCNVBtn;
    @FXML private Spinner<Integer> minimumConsecutivesAmpsSd;
    @FXML private HBox container;
    @FXML private Button prevBlock;
    @FXML private Button nextBlock;

    private final AnalysisViewCNVController analysisViewCNVController;
    private final CNVNormalizedTableViewController cnvNormalizedTableViewController;
    private final CNVNormalizedMapsViewController cnvNormalizedMapsViewController;


    public CNVNormalizedViewController(AnalysisViewCNVController analysisViewCNVController, CovCopCNVData covcopCNVData) {
        this.analysisViewCNVController = analysisViewCNVController;
        cnvNormalizedTableViewController = new CNVNormalizedTableViewController(this, covcopCNVData);
        cnvNormalizedMapsViewController = new CNVNormalizedMapsViewController(this, covcopCNVData);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVNormalizedView.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();
    }

    private void initView() {
        initRangeSlider();
        initAutoModeMode();
        initCNVSizeSpinner();
    }

    private void initRangeSlider() {

        delDupThresholdRs = new RangeSlider();
        delDupThresholdRs.getStyleClass().add("cnv-range-slider");
        delDupThresholdRs.setMinorTickCount(0);
        rangeSliderContainer.getChildren().setAll(delDupThresholdRs);

        final double delThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DEL_THRESHOLD));
        final double dupThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DUP_THRESHOLD));

        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(delThresholdLb.textProperty(), delDupThresholdRs.lowValueProperty(), converter);
        Bindings.bindBidirectional(dupThresholdLb.textProperty(), delDupThresholdRs.highValueProperty(), converter);

        delDupThresholdRs.setMin(0);
        delDupThresholdRs.setMax(2);
        delDupThresholdRs.setBlockIncrement(0.01);
        delDupThresholdRs.setMajorTickUnit(0.1);
        delDupThresholdRs.setMinorTickCount(0);
        delDupThresholdRs.setShowTickLabels(true);
        delDupThresholdRs.setSnapToTicks(false);

        delDupThresholdRs.lowValueProperty().addListener((obs, oldV, newV) -> {
            delDupThresholdRs.setLowValue(NumberUtils.round(newV.doubleValue(), 2));
            setDelValueLabelPosition();
        });
        delDupThresholdRs.highValueProperty().addListener((obs, oldV, newV) -> {
            delDupThresholdRs.setHighValue(NumberUtils.round(newV.doubleValue(), 2));
            setDupValueLabelPosition();
        });

        delDupThresholdRs.setLowValue(delThreshold);
        delDupThresholdRs.setHighValue(dupThreshold);

//        Platform.runLater(() -> {

        setDelValueLabelPosition();
        setDupValueLabelPosition();
//        });
    }

    private void setDelValueLabelPosition() {
        if (delDupThresholdRs.getWidth() == 0) {
            // width is not yet attached, wait until skin is attached to access the scroll bars
            ChangeListener<Number> widthChangeListener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    delDupThresholdRs.widthProperty().removeListener(this);
                    setDelValueLabelPosition();
                }
            };
            delDupThresholdRs.widthProperty().addListener(widthChangeListener);
        } else {
            double rangeWidth = delDupThresholdRs.getWidth();
            double pixelValue = delDupThresholdRs.getLowValue() * rangeWidth / ((delDupThresholdRs.getMax() - delDupThresholdRs.getMin()));
            delThresholdLb.setLayoutX(pixelValue - 10);
        }
    }

    private void setDupValueLabelPosition() {
        if (delDupThresholdRs.getWidth() == 0) {
            // width is not yet attached, wait until skin is attached to access the scroll bars
            ChangeListener<Number> widthChangeListener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    delDupThresholdRs.widthProperty().removeListener(this);
                    setDupValueLabelPosition();
                }
            };
            delDupThresholdRs.widthProperty().addListener(widthChangeListener);
        } else {
            double rangeWidth = delDupThresholdRs.getWidth();
            double pixelValue = delDupThresholdRs.getHighValue()* rangeWidth / ((delDupThresholdRs.getMax() - delDupThresholdRs.getMin()));
            dupThresholdLb.setLayoutX(pixelValue);
        }
    }

    private void initAutoModeMode() {
        final boolean autoModeEnabled = Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_AUTO_DETECTION));
        autoCNVDetection.setSelected(autoModeEnabled);
        autoCNVDetection.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                App.get().getLoggedUser().setPreference(DefaultPreferencesEnum.CNV_AUTO_DETECTION, String.valueOf(newV));
            }
        });
        deldupManualContainer.disableProperty().bind(autoCNVDetection.selectedProperty());
    }

    private void initCNVSizeSpinner() {
        int cnvSize = Integer.parseInt(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_MIN_AMPLICONS));
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE , cnvSize);
        minimumConsecutivesAmpsSd.setValueFactory(valueFactory);
        minimumConsecutivesAmpsSd.valueProperty().addListener((obs, oldV, newV) -> {
            App.get().getLoggedUser().setPreference(DefaultPreferencesEnum.CNV_MIN_AMPLICONS, String.valueOf(newV));
            App.get().getLoggedUser().savePreferences();
        });
    }

    public void showNormalizedTableView() {
        container.getChildren().setAll(cnvNormalizedTableViewController);
        HBox.setHgrow(cnvNormalizedTableViewController, Priority.ALWAYS);
        prevBlock.setVisible(true);
        nextBlock.setVisible(true);
    }

    public void showNormalizedMapsView() {
        container.getChildren().setAll(cnvNormalizedMapsViewController);
        HBox.setHgrow(cnvNormalizedMapsViewController, Priority.ALWAYS);
        prevBlock.setVisible(false);
        nextBlock.setVisible(false);
    }

    @FXML
    private void gotToPreviousCNV() {
        cnvNormalizedTableViewController.gotToPreviousCNV();
    }

    @FXML
    private void gotToNextCNV() {
        cnvNormalizedTableViewController.gotToNextCNV();
    }

    @FXML
    private void callCNVs() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("cnvnormalizedview.lb.callCNV"));
        wid.exec("callCnv", inputParams -> {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.CNV_AUTO_DETECTION, String.valueOf(autoCNVDetection.isSelected()));
            user.setPreference(DefaultPreferencesEnum.CNV_MIN_AMPLICONS, String.valueOf(minimumConsecutivesAmpsSd.getValue()));
            user.setPreference(DefaultPreferencesEnum.CNV_DEL_THRESHOLD, String.valueOf(NumberUtils.round(delDupThresholdRs.getLowValue(), 2)));
            user.setPreference(DefaultPreferencesEnum.CNV_DUP_THRESHOLD, String.valueOf(NumberUtils.round(delDupThresholdRs.getHighValue(), 2)));
            user.savePreferences();
            try {
                analysisViewCNVController.callCNVs();
                Platform.runLater(() -> {
                    cnvNormalizedTableViewController.getCNVIndexs();
                    cnvNormalizedTableViewController.refreshTable();
                    cnvNormalizedMapsViewController.forceRedrawMaps();
                });
            } catch (Exception e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
                return 1;
            }
            return 0;
        });
    }
}
