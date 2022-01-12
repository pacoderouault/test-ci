package ngsdiaglim.controllers.analysisview.cnv;

import de.gsi.chart.utils.AxisSynchronizer;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.*;

public class CNVNormalizedMapsViewController extends VBox {

    private static final Logger logger = LogManager.getLogger(CNVNormalizedMapsViewController.class);

    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> samplesByPageCountCb;
    @FXML private VBox mapsContainer;
    @FXML private HBox visibleSamplesDropMenuContainer;
    @FXML private Button showVisibleSampleBtn;
    @FXML private ToggleButton loesBtn;
    @FXML private ToggleButton geneAverageBtn;
    @FXML private ToggleButton cusumBtn;
//    private DropDownMenu sampleDropDownMenu;
    private final Map<String, CNVMap> drawnMapList = new HashMap<>();
    private final CovCopCNVData covcopCnvData;

    private ChangeListener<Number> paginationPageIndexListener;
    private ChangeListener<Number> paginationPageCountListener;

    public CNVNormalizedMapsViewController(CNVNormalizedViewController cnvNormalizedViewController, CovCopCNVData covcopCnvData) {
        //    private final AxisSynchronizer2 sync2 = new AxisSynchronizer2();
        this.covcopCnvData = covcopCnvData;

        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVNormalizedMapsView.fxml"), App.getBundle());
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
        initShowChartElementsBtn();
        initVisibleSamplesDropDownMenu();
        intiSamplesPerPageCb();

        covcopCnvData.getVisibleSamples().addListener((ListChangeListener<CNVSample>) change -> initPagination());

        initPagination();

        paginationPageIndexListener = (obs, old, newV) -> drawMaps();
        paginationPageCountListener = (obs, old, newV) -> {
            pagination.currentPageIndexProperty().removeListener(paginationPageIndexListener);
            drawMaps();
            pagination.currentPageIndexProperty().addListener(paginationPageIndexListener);
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.CNV_NUMBER_SAMPLE_PER_PAGE, String.valueOf(newV));
            user.savePreferences();
        };
        pagination.currentPageIndexProperty().addListener(paginationPageIndexListener);
        pagination.pageCountProperty().addListener(paginationPageCountListener);

        HBox.setHgrow(mapsContainer, Priority.ALWAYS);

        drawMaps();
    }

    private void initShowChartElementsBtn() {
        User user = App.get().getLoggedUser();
        boolean showLoess = Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.CNV_SHOW_LOESS));
        boolean showGeneAverage = Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.CNV_SHOW_GENE_AVERAGE));
        boolean showCusum = Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.CNV_SHOW_CUSUM));
        loesBtn.setSelected(showLoess);
        geneAverageBtn.setSelected(showGeneAverage);
        cusumBtn.setSelected(showCusum);

        loesBtn.selectedProperty().addListener((obs, oldV, newV) -> {
            user.setPreference(DefaultPreferencesEnum.CNV_SHOW_LOESS, String.valueOf(newV));
            user.savePreferences();
        });
        geneAverageBtn.selectedProperty().addListener((obs, oldV, newV) -> {
            user.setPreference(DefaultPreferencesEnum.CNV_SHOW_GENE_AVERAGE, String.valueOf(newV));
            user.savePreferences();
        });
        cusumBtn.selectedProperty().addListener((obs, oldV, newV) -> {
            user.setPreference(DefaultPreferencesEnum.CNV_SHOW_CUSUM, String.valueOf(newV));
            user.savePreferences();
        });
    }

    private void initVisibleSamplesDropDownMenu() {
//        DropDownMenu dropDownMenu = new DropDownMenu(App.getBundle().getString("cnvnormalizedview.btn.visibleSamples"));
        CNVVisibleSamplesDropMenuContent content = new CNVVisibleSamplesDropMenuContent(covcopCnvData);
//        dropDownMenu.setContentNode(content);
//        visibleSamplesDropMenuContainer.getChildren().setAll(dropDownMenu);
        PopOver visibleSamplesPopOver = new PopOver();
        visibleSamplesPopOver.setContentNode(content);
        visibleSamplesPopOver.setAnimated(false);
        visibleSamplesPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        showVisibleSampleBtn.setOnAction(e -> visibleSamplesPopOver.show(showVisibleSampleBtn));
    }


    private void intiSamplesPerPageCb() {
        int[] possibleValues = new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30};
        for (int i : possibleValues) {
            samplesByPageCountCb.getItems().add(i);
        }
        Integer userValue = Integer.parseInt(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_NUMBER_SAMPLE_PER_PAGE));
        samplesByPageCountCb.getSelectionModel().select(userValue);
        if(samplesByPageCountCb.getSelectionModel().getSelectedItem() == null) samplesByPageCountCb.getSelectionModel().select(2);
        samplesByPageCountCb.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> initPagination());
    }


    private void initPagination() {
        int pageNumber = (int) Math.ceil(covcopCnvData.getVisibleSamples().size() / (samplesByPageCountCb.getValue() * 1.0));
        pagination.setPageCount(pageNumber);
        pagination.setCurrentPageIndex(0);
    }


    public void forceRedrawMaps() {
        clear();
        drawMaps(true);
    }

    public void drawMaps() {
        drawMaps(false);
    }

    private void drawMaps(boolean forceRedraw) {
        PlatformUtils.runAndWait(() -> {
            mapsContainer.getChildren().clear();
            AxisSynchronizer synchronizer = new AxisSynchronizer();

            int firstSampleIndexToDraw = pagination.getCurrentPageIndex() * samplesByPageCountCb.getValue();
            int lastSampleIndexToDraw = Math.min(firstSampleIndexToDraw + samplesByPageCountCb.getValue(), covcopCnvData.getVisibleSamples().size()) - 1;
            Set<String> drawnSamples = new HashSet<>();

            for (int i = firstSampleIndexToDraw; i <= lastSampleIndexToDraw; i++) {

                CNVSample sample = covcopCnvData.getVisibleSamples().get(i);
                drawnSamples.add(sample.getBarcode());
                CNVMap cnvMap;
                if (!forceRedraw && drawnMapList.containsKey(sample.getBarcode())) {
                    cnvMap = drawnMapList.get(sample.getBarcode());
                } else {
                    cnvMap = new CNVMap(this, covcopCnvData, sample);
                    cnvMap.getCnvChart().loessVisibleProperty().bind(loesBtn.selectedProperty());
                    cnvMap.getCnvChart().geneAverageVisibleProperty().bind(geneAverageBtn.selectedProperty());
                    cnvMap.getCnvChart().cusumVisibleProperty().bind(cusumBtn.selectedProperty());
                    cnvMap.drawChart();
                    drawnMapList.put(sample.getBarcode(), cnvMap);
                }
                mapsContainer.getChildren().add(cnvMap);
                synchronizer.add(cnvMap.getCnvChart().getXAxis());
            }

            Iterator<Map.Entry<String, CNVMap>> iter = drawnMapList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String,CNVMap> entry = iter.next();
                if(!drawnSamples.contains(entry.getKey())){
                    entry.getValue().clear();
                    iter.remove();
                }
            }

        });
    }

    public void resetZoom() {
        drawnMapList.values().forEach(CNVMap::resetZoom);
    }

    public void clear() {
        for (CNVMap map : drawnMapList.values()) {
            map.getCnvChart().loessVisibleProperty().unbind();
            map.getCnvChart().geneAverageVisibleProperty().unbind();
            map.getCnvChart().cusumVisibleProperty().unbind();
            map.clear();
        }
        mapsContainer.getChildren().forEach(n -> {
            if (n instanceof CNVMap) {
                CNVMap map = (CNVMap) n;
                map.getCnvChart().loessVisibleProperty().unbind();
                map.getCnvChart().geneAverageVisibleProperty().unbind();
                map.getCnvChart().cusumVisibleProperty().unbind();
                map.clear();
            }
        });
        mapsContainer.getChildren().clear();
        drawnMapList.clear();
    }

}
