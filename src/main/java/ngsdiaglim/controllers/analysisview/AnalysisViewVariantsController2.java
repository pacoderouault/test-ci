package ngsdiaglim.controllers.analysisview;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.comparators.AnnotationComparator;
import ngsdiaglim.controllers.VariantTableBuilder;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.AddGenePanelDialog;
import ngsdiaglim.controllers.dialogs.ExportTableDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.ui.DropDownMenu;
import ngsdiaglim.controllers.ui.FilterTableView;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.TableExporter;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GenePanel;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.TranscriptConsequence;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class AnalysisViewVariantsController2 extends VBox {

    private final Logger logger = LogManager.getLogger(AnalysisViewVariantsController2.class);

    @FXML private ComboBox<GenePanel> panelsCb;
    @FXML private CustomTextField searchInTableTf;
    @FXML private Button closeSearchBtn;
    @FXML private Button prevSearchBtn;
    @FXML private Button nextSearchBtn;
    @FXML private Label searchResultLb;
    @FXML private HBox tableMenuContainer;
    @FXML private VBox tableContainer;
    @FXML private final FontIcon searchIcon = new FontIcon("mdmz-search");
    @FXML private final FilterTableView<Annotation> variantsTable;
    @FXML private SplitPane splitPane;
    @FXML private AnchorPane variantDetailContainer;

//    private DropDownMenu exportTableDropmenu;
    private DropDownMenu columnsVisibilityDropmenu;
    private DropDownMenu hideVariantsDropmenu;
    private HideVariantDropDownMenuContent hideVariantDropDownMenuContent;

    private final Analysis analysis;
    private FilteredList<Annotation> filteredAnnotations;
    private final VariantTableBuilder tableBuilder;
    private final AnalysisViewVariantDetailController variantDetailController;
    private final AnnotationComparator annotationComparator = new AnnotationComparator();
    private final ObservableList<Integer> searchRsltRowIndex = FXCollections.observableArrayList();
    private int searchRsltCursor = 0;
    private String lastQuery = "";


    public AnalysisViewVariantsController2(Analysis analysis) {
        this.analysis = analysis;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewVariants.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        variantDetailController = new AnalysisViewVariantDetailController();
        variantDetailContainer.getChildren().add(variantDetailController);
        AnchorPane.setTopAnchor(variantDetailController, 0d);
        AnchorPane.setRightAnchor(variantDetailController, 0d);
        AnchorPane.setBottomAnchor(variantDetailController, 0d);
        AnchorPane.setLeftAnchor(variantDetailController, 0d);

        variantsTable = new FilterTableView<>();
        tableContainer.getChildren().add(variantsTable);
        VBox.setVgrow(variantsTable, Priority.ALWAYS);
        tableBuilder = new VariantTableBuilder(variantsTable);
        try {
            tableBuilder.buildTable();
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
        setTableItems();
        initView();

//        primaryFilteredAnnotations = FXCollections.observableArrayList();
        setAnnotationsFilters();

        variantDetailController.annotationProperty().bind(variantsTable.getSelectionModel().selectedItemProperty());

        // listen when table is filtered from columns
        variantsTable.predicateProperty().addListener((obs, oldV, newV) -> setAnnotationsFilters());

        // select first variant of the table
        if (variantsTable.getItems().size() > 0) {
            variantsTable.getSelectionModel().select(0, variantsTable.getColumns().get(0));
        }
    }

    @FXML
    public void initialize() {
        // init mouse click event on header table
//        Platform.runLater(() -> {
////            variantsTable.refresh();
////            variantsTable.applyCss();
////            variantsTable.layout();
////            variantsTable.refresh();
////            PlatformUtils.runAndWait(() -> {
//            System.out.println(getParent());
//            getParent().applyCss();
//            getParent().layout();
//            tableBuilder.setColumnsHeaderEvent();
////            });
//
//        });
    }

    private void setTableItems() {
        filteredAnnotations = new FilteredList<>(analysis.getAnnotations().sorted(annotationComparator));
        SortedList<Annotation> sortedAnnotations = new SortedList<>(filteredAnnotations);
//        sortedAnnotations.setComparator(annotationComparator);
        sortedAnnotations.comparatorProperty().bind(variantsTable.comparatorProperty());
        variantsTable.setItems(sortedAnnotations);

    }

    @FXML
    private void setAnnotationsFilters() {
        /*
        FilteredTableView cannot be filtered from public method
        due to the predicate of the filtered list is binded
        to the predicate of the table.

        If we want to filter the annotation from another source than
        the table's columns, we primary filter the annotations list
         */
        Predicate<Annotation> hideVariantsPredicate = hideVariantDropDownMenuContent.getPredicate();
        Predicate<Annotation> genesPanelPredicate = null;
        if (panelsCb.getValue() != null) {
            genesPanelPredicate = a -> {
                if (a.getGene() != null && panelsCb.getValue().hasGene(a.getGene())) return true;
                for (String gn : a.getGeneNameSet()) {
                    if (panelsCb.getValue().hasGene(new Gene(gn))) return true;
                }
                return false;
            };
        }
        Predicate<Annotation> tablePredicate = variantsTable.getPredicate();

        Predicate<Annotation> predicate = PredicateUtils.addPredicates(hideVariantsPredicate, genesPanelPredicate, tablePredicate);
        filteredAnnotations.setPredicate(predicate);
//
//        Predicate<Annotation> predicate = null;
//        if (hideVariantsPredicate != null) {
//            predicate = hideVariantsPredicate;
//        }
//        if (genesPanelPredicate != null) {
//            if (predicate != null) {
//                predicate = predicate.and(genesPanelPredicate);
//            } else {
//                predicate = genesPanelPredicate;
//            }
//        }
//        if (predicate != null) {
//            primaryFilteredAnnotations.setAll(
//                    analysis.getAnnotations().stream()
//                            .filter(predicate)
//                            .collect(Collectors.toCollection(ArrayList::new))
//            );
//        } else {
//            primaryFilteredAnnotations.setAll(analysis.getAnnotations());
//        }

//        primaryFilteredAnnotations.sort(new AnnotationComparator());
//        filteredAnnotations = new FilteredList<>(primaryFilteredAnnotations);
//        sortedAnnotations = new SortedList<>(filteredAnnotations);
//        sortedAnnotations.comparatorProperty().bind(variantsTable.comparatorProperty());
//        filteredAnnotations.predicateProperty().bind(variantsTable.predicateProperty());
//        variantsTable.setItems(sortedAnnotations);
    }


    @FXML
    private void resetVariantSort() {
//        sortedAnnotations.comparatorProperty().unbind();
        variantsTable.getSortOrder().clear();
//        variantsTable.sort();
//        sortedAnnotations.setComparator(annotationComparator);
//        sortedAnnotations.comparatorProperty().bind(variantsTable.comparatorProperty());


//        sortedAnnotations.setComparator(annotationComparator);
    }


    private void initView() {

        searchInTableTf.setLeft(searchIcon);
        // hide searchUI elements
        setSearchUIVisible(false);

        initTableMenu();
        initGenesPanelCombobox();



//        splitPane.setDividerPositions();
    }

    /**
     * Hide the search UI elements
     * @param value
     */
    private void setSearchUIVisible(boolean value) {
        nextSearchBtn.setVisible(value);
        nextSearchBtn.setManaged(value);
        prevSearchBtn.setVisible(value);
        prevSearchBtn.setManaged(value);
        closeSearchBtn.setVisible(value);
        closeSearchBtn.setManaged(value);
    }

    private void initTableMenu() {
        initTableExportButton();
        initColumnVisibilityDropMenu();
        initHideVariantsDropMenu();

        this.tableMenuContainer.getChildren().addAll(columnsVisibilityDropmenu, hideVariantsDropmenu);
    }


    private void initGenesPanelCombobox() {
        try {
            loadGenesPanels();
            panelsCb.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                setAnnotationsFilters();
            });
        } catch (SQLException e) {
            logger.error(e);
        }
    }


    private void initTableExportButton() {

    }


    private void initColumnVisibilityDropMenu() {
        columnsVisibilityDropmenu = new DropDownMenu(App.getBundle().getString("analysisview.variants.menu.columnvisibility"));
        FontIcon icon = new FontIcon("mdmz-view_column");
        columnsVisibilityDropmenu.setGraphic(icon);
        columnsVisibilityDropmenu.getStyleClass().add("button-link");

        ColumnsVisivilityDropDownMenuContent columnsVisivilityDropDownMenuContent = new ColumnsVisivilityDropDownMenuContent();
        columnsVisivilityDropDownMenuContent.setVariantTableBuilder(tableBuilder);
        columnsVisibilityDropmenu.setContentNode(columnsVisivilityDropDownMenuContent);
    }


    private void initHideVariantsDropMenu() {
        hideVariantsDropmenu = new DropDownMenu(App.getBundle().getString("analysisview.variants.menu.hidevariants"));
        FontIcon icon = new FontIcon("mdmz-visibility_off");
        hideVariantsDropmenu.setGraphic(icon);
        hideVariantsDropmenu.getStyleClass().add("button-link");

        hideVariantDropDownMenuContent = new HideVariantDropDownMenuContent();
        hideVariantDropDownMenuContent.predicateProperty().addListener((observable, oldValue, newValue) -> {
            setAnnotationsFilters();
        });
        hideVariantsDropmenu.setContentNode(hideVariantDropDownMenuContent);
    }


    public void setVisibleTranscript(Transcript transcript) {
        analysis.getAnnotations().parallelStream().forEach(a -> {
            for (Map.Entry<String, TranscriptConsequence> entry : a.getTranscriptConsequences().entrySet()) {
                if (entry.getValue().getTranscript().equals(transcript)) {
                    a.setTranscriptConsequence(entry.getValue());
                    break;
                }
            }
        });
        variantsTable.refresh();
    }


    @FXML
    private void exportTableHandler() {
        ExportTableDialog dialog = new ExportTableDialog(App.get().getAppController().getDialogPane(), variantsTable.getItems());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {

            FileChooser fc = FileChooserUtils.getFileChooser();
            fc.setInitialFileName(analysis.getName().replaceAll("[/\\.]", "_") + ".xlsx");
            File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
            if (selectedFile != null) {

                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("exportvariants.msg.exportVariant"));
                wid.addTaskEndNotification(r -> {
                    if (r == 0) {
                        Message.hideDialog(dialog);
                    }
                });
                wid.exec("LoadPanels", inputParam -> {
                    List<TableColumn<Annotation, ?>> columnsToWrite = new ArrayList<>();
                    for (VariantsTableColumns vtc : dialog.getValue().getColumnsExport().getColumns()) {
                        TableColumn<Annotation, ?> col = dialog.getValue().getTableBuilder().getColumn(vtc);
                        if (col != null) {
                            columnsToWrite.add(col);
                        }
                    }
                    try {
                        TableExporter.exportTableToExcel(analysis, dialog.getValue().getTable(), columnsToWrite, selectedFile);
                    } catch (IOException exception) {
                        logger.error(exception);
                        Platform.runLater(() -> Message.error(exception.getMessage(), exception));
                        return 1;
                    }
                    return 0;
                });
            }



        });
    }


    @FXML
    private void searchInTable() {
        setSearchUIVisible(true);
        // get query and update the UI
        String query = searchInTableTf.getText();
        if (!query.isEmpty()) {
            if (query.equals(lastQuery)) {
                // go to the next row matching the query
                nextSearchRslt();
            } else {
                clearSearch();
                lastQuery = query;

                // Store the index of each row that has a cell matching the query
                for (Annotation item : variantsTable.getItems()) {
                    for (TableColumn<Annotation, ?> column : variantsTable.getVisibleLeafColumns()) {

                        if (column.getCellObservableValue(item) != null && column.getCellObservableValue(item).getValue() != null) {
                            if (StringUtils.containsIgnoreCase(column.getCellObservableValue(item).getValue().toString(), query)) {
                                searchRsltRowIndex.add(variantsTable.getItems().indexOf(item));
                                break;
                            }
                        }
//                        //Column is ok when it is neither equal to Name or External Links
//                        //Options that matches the item are Commentary, Vaf or Observed
//                        if (colTitleIsOkAndItemMatchesOptions(query, item, column)) {
//                            searchRsltRowIndex.add(getItemIndex(item));
//                            break;
//                        }
                    }
                }

                // update the UI
                if (searchRsltRowIndex.size() > 0) {
                    showSearchResult();
                    searchInTableTf.getStyleClass().remove("search-text-field-error");
                } else {
                    variantsTable.getSelectionModel().clearSelection();
                    searchInTableTf.getStyleClass().add("search-text-field-error");
                    searchResultLb.setText("no matches");
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
        variantsTable.getSelectionModel().clearSelection();
        variantsTable.getSelectionModel().select(searchRsltRowIndex.get(searchRsltCursor));
        variantsTable.scrollTo(searchRsltRowIndex.get(searchRsltCursor));
        searchResultLb.setText((searchRsltCursor + 1) + " of " + searchRsltRowIndex.size() + " matches");
    }


    /**
     * Hide and clear the search UI
     */
    @FXML
    private void closeSearch() {
        clearSearch();
        setSearchUIVisible(false);
        searchInTableTf.setText("");
    }

    /**
     * Reset the search UI elements
     */
    private void clearSearch() {
        searchRsltCursor = 0;
        lastQuery = "";
        searchRsltRowIndex.clear();
        searchInTableTf.getStyleClass().remove("search-text-field-error");
        searchResultLb.setText("");
    }


    private void loadGenesPanels() throws SQLException {
        GenePanel selectedgenePanel = panelsCb.getValue();
        panelsCb.setItems(DAOController.get().getGenesPanelDAO().getGenesPanels());
        // add a null value for unselect panels
        panelsCb.getItems().add(0, null);
        panelsCb.getSelectionModel().select(selectedgenePanel);
    }


    @FXML
    private void addPanelHandler() {
        AddGenePanelDialog dialog = new AddGenePanelDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        Button b = dialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (dialog.isValid() && dialog.getValue() != null) {
                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addpaneldialog.msg.loading"));
                wid.addTaskEndNotification(r -> {
                    if (r == 0) {
                        try {
                            loadGenesPanels();
                            Message.hideDialog(dialog);
                        } catch (SQLException ex) {
                            logger.error("Error when adding panel", ex);
                            Message.error(ex.getMessage(), ex);
                        }
                    }
                });
                wid.exec("LoadPanels", inputParam -> {
                    try {
                        DAOController.get().getGenesPanelDAO().addGenesPanel(dialog.getValue().getName(), dialog.getValue().getSelectedGenes());
                    } catch (Exception ex) {
                        logger.error("Error when adding panel", ex);
                        Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                        return 1;
                    }
                    return 0;
                });
            }
        });
    }

    public void gotToVariant(Variant v) {
        for (int i = 0; i < variantsTable.getItems().size(); i++) {
            if (variantsTable.getItems().get(i).getVariant().equals(v)) {
                variantsTable.getSelectionModel().select(i);
                variantsTable.scrollTo(i);
                return;
            }
        }
    }

    public void refreshTable() {
        variantsTable.refresh();
    }

    public VariantTableBuilder getTableBuilder() {return tableBuilder;}

    public AnalysisViewVariantDetailController getVariantDetailController() {return variantDetailController;}

    public SplitPane getSplitPane() {return splitPane;}

    public AnchorPane getVariantDetailContainer() {return variantDetailContainer;}

    public void setDividerPosition() {
        double divPos = (splitPane.getHeight() - (variantDetailController.getTest().getHeight() + 8)) / splitPane.getHeight();
        splitPane.setDividerPositions(divPos);
    }
}
