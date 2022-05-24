package ngsdiaglim.controllers.analysisview.cnv;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNV;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.controllers.cells.CNVTableCellFactory;
import ngsdiaglim.controllers.dialogs.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class CNVNormalizedTableViewController extends VBox {
    private static final Logger logger = LogManager.getLogger(CNVNormalizedTableViewController.class);

    @FXML private Button closeSearchBtn;
    @FXML private Button nextSearchResultBtn;
    @FXML private Button prevSearchResultBtn;
    @FXML private CustomTextField searchTf;
    @FXML private Label searchRsltLb;
    @FXML private TableView<CovCopRegion> dataTable;
    @FXML private TableColumn<CovCopRegion, String> poolCol;
    @FXML private TableColumn<CovCopRegion, String> contigCol;
    @FXML private TableColumn<CovCopRegion, Integer> startCol;
    @FXML private TableColumn<CovCopRegion, Integer> endCol;
    @FXML private TableColumn<CovCopRegion, String> geneCol;
    @FXML private TableColumn<CovCopRegion, String> nameCol;
    private final int defaultColumnsCount = 5;
    private final FontIcon searchIcon = new FontIcon("fas-search");

    public static final int noSampleColumnsNumber = 6;
    private final ObservableList<Integer> searchRsltRowIndex = FXCollections.observableArrayList();
    private int searchRsltCursor = 0;
    private String lastQuery = "";
    private final ArrayList<Integer[]> CNVIndexes = new ArrayList<>();
    private int currentCNVIndex = 0;

    private final SimpleObjectProperty<CovCopCNVData> covcopCnvData = new SimpleObjectProperty<>();

    public CNVNormalizedTableViewController() {

        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVNormalizedTableView.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();

        covcopCnvData.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                updateView();
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
        initDataTable();
        searchTf.setLeft(searchIcon);
    }

    private void updateView() {
        initSampleColumns();
        dataTable.setItems(covcopCnvData.get().getAllCovcopRegionsAsList());
        getCNVIndexs();
    }

    private void initDataTable() {
        poolCol.setCellValueFactory(data -> data.getValue().poolProperty());
        contigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        startCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        endCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        geneCol.setCellValueFactory(data -> data.getValue().geneProperty());
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));


        dataTable.getColumns().forEach(c -> c.setReorderable(false));
    }


    private void initSampleColumns() {

        // delete old sample columns
        if (dataTable.getColumns().size() > defaultColumnsCount) {
            dataTable.getColumns().remove(defaultColumnsCount, dataTable.getColumns().size());
        }

        int sampleIdx = 0;
        for (String sampleName : covcopCnvData.get().getSamples().keySet()) {
            TableColumn<CovCopRegion, Double> col = new TableColumn<>(sampleName);
            int finalSampleIdx = sampleIdx;
            col.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNormalized_values().get(finalSampleIdx)));
            col.setCellFactory(data -> new CNVTableCellFactory());
            dataTable.getColumns().add(col);
            sampleIdx++;
        }
    }


    public void getCNVIndexs() {
        CNVIndexes.clear();
        int sampleIndex = 0;
        Set<Integer> rowsIds = new HashSet<>();
        int colIndex;
        for (String sampleName : covcopCnvData.get().getSamples().keySet()) {
            CNVSample sample = covcopCnvData.get().getSamples().get(sampleName);

            for (CNV cnv : sample.getCNV()) {
                if (rowsIds.add(cnv.getFirstAmpliconIndex())) {
                    colIndex = sampleIndex + noSampleColumnsNumber;
                    CNVIndexes.add(new Integer[]{colIndex, cnv.getFirstAmpliconIndex()});
                }
            }
            sampleIndex++;
        }
        CNVIndexes.sort(Comparator.comparingInt((Integer[] integer) -> integer[1]));
        currentCNVIndex = -1;
    }

    public void gotToPreviousCNV() {
        if (CNVIndexes.size() == 0) return;
        currentCNVIndex--;
        if (currentCNVIndex < 0) {
            currentCNVIndex = CNVIndexes.size() - 1;
        }
        dataTable.scrollTo(CNVIndexes.get(currentCNVIndex)[1]);
        dataTable.scrollToColumnIndex(CNVIndexes.get(currentCNVIndex)[0]);
    }

    public void gotToNextCNV() {
        if (CNVIndexes.size() == 0) return;
        currentCNVIndex++;
        if (currentCNVIndex >= CNVIndexes.size()) {
            currentCNVIndex = 0;
        }
        dataTable.scrollTo(CNVIndexes.get(currentCNVIndex)[1]);
        dataTable.scrollToColumnIndex(CNVIndexes.get(currentCNVIndex)[0]);
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
                for (CovCopRegion item : dataTable.getItems()) {
                    for (TableColumn<CovCopRegion, ?> column : dataTable.getVisibleLeafColumns()) {
                        if (column.getCellObservableValue(item) != null && column.getCellObservableValue(item).getValue() != null) {
                            if (StringUtils.containsIgnoreCase(column.getCellObservableValue(item).getValue().toString(), query)) {
                                searchRsltRowIndex.add(dataTable.getItems().indexOf(item));
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
                    dataTable.getSelectionModel().clearSelection();
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
        dataTable.getSelectionModel().clearSelection();
        dataTable.getSelectionModel().select(searchRsltRowIndex.get(searchRsltCursor));
        dataTable.scrollTo(searchRsltRowIndex.get(searchRsltCursor));
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
        dataTable.refresh();
    }
}
