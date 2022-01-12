package ngsdiaglim.controllers.analysisview.cnv;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.modeles.analyse.Analysis;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class CNVRawDataController extends VBox {
    private final static Logger logger = LogManager.getLogger(CNVRawDataController.class);

    @FXML private ComboBox<String> poolCb;
    @FXML private Button closeSearchBtn;
    @FXML private Button nextSearchResultBtn;
    @FXML private Button prevSearchResultBtn;
    @FXML private CustomTextField searchTf;
    @FXML private Label searchRsltLb;
    @FXML private TableView<CovCopRegion> rawdataTable;
    @FXML private TableColumn<CovCopRegion, String> contigCol;
    @FXML private TableColumn<CovCopRegion, Integer> startCol;
    @FXML private TableColumn<CovCopRegion, Integer> endCol;
    @FXML private TableColumn<CovCopRegion, String> geneCol;
    @FXML private TableColumn<CovCopRegion, String> nameCol;
    private final FontIcon searchIcon = new FontIcon("mdmz-search");

    private final AnalysisViewCNVController analysisViewCNVController;
    private final Analysis analysis;
    private final SimpleObjectProperty<CovCopCNVData> covcopCnvData = new SimpleObjectProperty<>();

    private final Tooltip genderBtnTooltip = new Tooltip(App.getBundle().getString("cnv.rawdata.tp.switchgender"));
    private final Tooltip setControlBtnTooltip = new Tooltip(App.getBundle().getString("cnv.rawdata.tp.setcontrol"));
    private final Tooltip unsetControlBtnTooltip = new Tooltip(App.getBundle().getString("cnv.rawdata.tp.unsetcontrol"));

    private ObservableList<Integer> searchRsltRowIndex = FXCollections.observableArrayList();
    private int searchRsltCursor = 0;
    private String lastQuery = "";


    public CNVRawDataController(AnalysisViewCNVController analysisViewCNVController, Analysis analysis, CovCopCNVData covCopCNVData) {
        this.analysisViewCNVController = analysisViewCNVController;
        this.analysis = analysis;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVRawData.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        this.covcopCnvData.set(covCopCNVData);
        initView();
    }


    private void initView() {
        initRawDataTable();
        initPoolCb();

        genderBtnTooltip.setShowDelay(Duration.ZERO);
        setControlBtnTooltip.setShowDelay(Duration.ZERO);
        unsetControlBtnTooltip.setShowDelay(Duration.ZERO);

        searchTf.setLeft(searchIcon);

        // select first pool
        poolCb.getSelectionModel().select(0);
    }


    private void initRawDataTable() {

        contigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        startCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        endCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        geneCol.setCellValueFactory(data -> data.getValue().geneProperty());
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        initSampleColumns();

        rawdataTable.getColumns().forEach(c -> c.setReorderable(false));
    }


    private void initSampleColumns() {
        int sampleIdx = 0;
        for (String sampleName : covcopCnvData.get().getSamples().keySet()) {
            CNVSample sample = covcopCnvData.get().getSamples().get(sampleName);
            TableColumn<CovCopRegion, Integer> col = new TableColumn<>();
            int finalSampleIdx = sampleIdx;
            col.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getRaw_values().get(finalSampleIdx)));

            VBox box = new VBox();
            box.setPadding(new Insets(3, 3, 0, 3));
            Label sampleNameLb = new Label(sampleName);
            sampleNameLb.setWrapText(false);

            HBox actionsBox = new HBox();
            actionsBox.getStyleClass().add("box-action-cell");
            actionsBox.setAlignment(Pos.CENTER);
            ToggleButton genderBtn = getGenderToggleButton(sample);
            genderBtn.getStyleClass().add("button-action-cell");
            ToggleButton controlBtn = getControlToggleButton(sample);
            controlBtn.getStyleClass().add("button-action-cell");
            genderBtn.prefHeightProperty().bind(controlBtn.heightProperty());
            actionsBox.getChildren().setAll(genderBtn, controlBtn);
            box.getChildren().setAll(sampleNameLb, actionsBox);
            col.setGraphic(box);

            Platform.runLater(() -> {
                Text t = new Text(sampleName);
                col.setPrefWidth(t.getLayoutBounds().getWidth() + 30);
            });
//                col.prefWidthProperty().bind(box.widthProperty());
            rawdataTable.getColumns().add(col);
            sampleIdx++;
        }
    }


    private void initPoolCb() {
        String allRegions = "All regions";
        poolCb.getItems().setAll(covcopCnvData.get().getCovcopRegions().keySet());
        poolCb.getItems().add(allRegions);

        poolCb.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            closeSearch();
            if (newV.equals(allRegions)) {
                rawdataTable.setItems(covcopCnvData.get().getAllCovcopRegionsAsList());
            } else {
                if (covcopCnvData.get().getCovcopRegions().containsKey(newV)) {
                    rawdataTable.setItems(covcopCnvData.get().getCovcopRegions().get(newV));
                }
            }
        });

    }


    private ToggleButton getGenderToggleButton(CNVSample sample) {
        ToggleButton genderBtn = new ToggleButton();
        genderBtn.getStyleClass().addAll("icon-button");
        genderBtn.setOnMouseEntered(e -> Tooltip.install(genderBtn, genderBtnTooltip));

        if (sample.getGender().equals(Gender.MALE)) {
            genderBtn.setSelected(true);
            genderBtn.setText("XY");
        }
        else {
            genderBtn.setSelected(false);
            genderBtn.setText("XX");
        }
        genderBtn.selectedProperty().addListener((obs, old, newV) -> {
            if (newV) {
                sample.setGender(Gender.MALE);
                genderBtn.setText("XY");
            } else {
                sample.setGender(Gender.FEMALE);
                genderBtn.setText("XX");
            }
        });
        sample.genderProperty().addListener((obs, oldV, newV) -> genderBtn.setSelected(newV.equals(Gender.MALE)));
        return genderBtn;
    }


    private ToggleButton getControlToggleButton(CNVSample sample) {
        ToggleButton controlBtn = new ToggleButton();
        controlBtn.getStyleClass().addAll("icon-button");
        controlBtn.setOnMouseEntered(e -> {
            if (controlBtn.isSelected()) {
                Tooltip.install(controlBtn, unsetControlBtnTooltip);
            }
            else {
                Tooltip.install(controlBtn, setControlBtnTooltip);
            }
        });

        if (sample.isControl()) {
            controlBtn.setSelected(true);
            controlBtn.setGraphic(new FontIcon("mdmz-person_outline"));

        }
        else {
            controlBtn.setSelected(false);
            controlBtn.setGraphic(new FontIcon("mdmz-person"));
        }

        controlBtn.selectedProperty().addListener((obs, old, newV) -> {
            if (newV) {
                sample.setControl(true);
                controlBtn.setGraphic(new FontIcon("mdmz-person_outline"));
            }
            else {
                sample.setControl(false);
                controlBtn.setGraphic(new FontIcon("mdmz-person"));
            }
        });
        sample.controlProperty().addListener((obs, oldV, newV) -> controlBtn.setSelected(newV));
        return controlBtn;
    }


    /**
     * Search rows of the table containing the query
     */
    @FXML
    private void search() {
        setSearchUIVisible(true);

        // get query and update the UI
        String query = searchTf.getText();
        if (!query.isEmpty()) {
            if (query.equals(lastQuery)) {
                // go to the next row matching the query
                nextSearchRslt();
            } else {
                clearSearch();
                lastQuery = query;

                // Store the index of each row that has a cell matching the query
                for (CovCopRegion item : rawdataTable.getItems()) {
                    for (TableColumn<CovCopRegion, ?> column : rawdataTable.getVisibleLeafColumns()) {
                        if (column.getCellObservableValue(item) != null && column.getCellObservableValue(item).getValue() != null) {
                            if (StringUtils.containsIgnoreCase(column.getCellObservableValue(item).getValue().toString(), query)) {
                                searchRsltRowIndex.add(rawdataTable.getItems().indexOf(item));
                                break;
                            }
                        }
                    }
                }

                // update the UI
                if (searchRsltRowIndex.size() > 0) {
                    showSearchResult();
                    searchTf.getStyleClass().remove("search-text-field-error");
                } else {
                    rawdataTable.getSelectionModel().clearSelection();
                    searchTf.getStyleClass().add("search-text-field-error");
                    searchRsltLb.setText("no matches");
                }
            }
        }
        else {
            closeSearch();
        }
    }


    /**
     * Go to next row matching the search query
     */
    @FXML
    private void prevSearchRslt() {
        if (searchRsltRowIndex.size() == 0) return;
        searchRsltCursor--;
        if (searchRsltCursor < 0) {
            searchRsltCursor = searchRsltRowIndex.size() - 1;
        }
        showSearchResult();
    }


    /**
     * Go to previous row matching the search query
     */
    @FXML
    private void nextSearchRslt() {
        if (searchRsltRowIndex.size() == 0) return;
        searchRsltCursor++;
        if (searchRsltCursor >= searchRsltRowIndex.size()) {
            searchRsltCursor = 0;
        }
        showSearchResult();
    }

    /**
     * Scroll to the search result
     */
    private void showSearchResult() {
        rawdataTable.getSelectionModel().clearSelection();
        rawdataTable.getSelectionModel().select(searchRsltRowIndex.get(searchRsltCursor));
        rawdataTable.scrollTo(searchRsltRowIndex.get(searchRsltCursor));
        searchRsltLb.setText((searchRsltCursor + 1) + " of " + searchRsltRowIndex.size() + " matches");
    }

    /**
     * Hide and clear the search UI
     */
    @FXML
    private void closeSearch() {
        clearSearch();
        setSearchUIVisible(false);
        searchTf.setText(null);
    }

    /**
     * Reset the search UI elements
     */
    private void clearSearch() {
        searchRsltCursor = 0;
        lastQuery = "";
        searchRsltRowIndex.clear();
        searchTf.getStyleClass().remove("search-text-field-error");
        searchRsltLb.setText(null);
    }

    /**
     * Hide the search UI elements
     */
    private void setSearchUIVisible(boolean value) {
        nextSearchResultBtn.setVisible(value);
        nextSearchResultBtn.setManaged(value);
        prevSearchResultBtn.setVisible(value);
        prevSearchResultBtn.setManaged(value);
        closeSearchBtn.setVisible(value);
        closeSearchBtn.setManaged(value);
    }

    public void refreshTable() {
        rawdataTable.refresh();
    }
}
