package ngsdiaglim.controllers.analysisview;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
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
import ngsdiaglim.controllers.ui.FilterTableView;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.TableExporter;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GenePanel;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.TranscriptConsequence;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.PredicateUtils;
import ngsdiaglim.utils.ScrollBarUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AnalysisViewVariantsController2 extends VBox {

    private final Logger logger = LogManager.getLogger(AnalysisViewVariantsController2.class);

    @FXML private ComboBox<GenePanel> panelsCb;
    @FXML private CustomTextField searchInTableTf;
    @FXML private Button closeSearchBtn;
    @FXML private Button prevSearchBtn;
    @FXML private Button nextSearchBtn;
    @FXML private Button addgenePanelBtn;
    @FXML private Button visibleColumnsBtn;
    @FXML private Button hideVariantsBtn;
    @FXML private Label searchResultLb;
    @FXML private HBox tableMenuContainer;
    @FXML private VBox tableContainer;
    @FXML private final FontIcon searchIcon = new FontIcon("mdmz-search");
    @FXML private FilterTableView<Annotation> variantsTable;
    @FXML private SplitPane splitPane;
    @FXML private AnchorPane variantDetailContainer;

//    private DropDownMenu exportTableDropmenu;
//    private DropDownMenu columnsVisibilityDropmenu;
//    private DropDownMenu hideVariantsDropmenu;
    private PopOver visibleColumnsPopOver;
    private PopOver hideVariantPopOver;
    private HideVariantDropDownMenuContent hideVariantDropDownMenuContent;

    private FilteredList<Annotation> filteredAnnotations;
    private final VariantTableBuilder tableBuilder;
    private final AnalysisViewVariantDetailController variantDetailController;
    private final AnnotationComparator annotationComparator = new AnnotationComparator();
    private final ObservableList<Integer> searchRsltRowIndex = FXCollections.observableArrayList();
    private int searchRsltCursor = 0;
    private String lastQuery = "";

    private final ScrollBarUtil scrollBarUtil;

    private final SimpleObjectProperty<Analysis> analysisProperty = new SimpleObjectProperty<>();

    public AnalysisViewVariantsController2() {
//        this.analysis = analysis;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewVariants.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage(), e);
        }

        initView();

        analysisProperty.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                updateView();
            }
        });

        variantDetailController.annotationProperty().bind(variantsTable.getSelectionModel().selectedItemProperty());

        // listen when table is filtered from columns
        variantsTable.predicateProperty().addListener((obs, oldV, newV) -> setAnnotationsFilters());

        scrollBarUtil = new ScrollBarUtil(variantsTable, Orientation.VERTICAL);
    }

    @FXML
    public void initialize() {
//        // init mouse click event on header table
//        Platform.runLater(() -> {
//            getParent().applyCss();
//            getParent().layout();
//            tableBuilder.setColumnsHeaderEvent();
//        });
    }


    private void updateView() {
        variantDetailController.updateReferenceSequence();
        setTableItems();
        setAnnotationsFilters();
        try {
            loadGenesPanels();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // select first variant of the table
        if (variantsTable.getItems().size() > 0) {
            variantsTable.getSelectionModel().select(0, variantsTable.getColumns().get(0));
        }

        addgenePanelBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_EDIT_GENEPANEL));



        Platform.runLater(() -> {
            if (Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.USE_SMOOTH_SCROLLING))) {
                scrollBarUtil.smoothScrollingTableView( (1d / variantsTable.getItems().size()) * 4);
            } else {
                scrollBarUtil.resetScrollBar();
            }
            tableBuilder.setRowFactory();
        });

    }


    private void setTableItems() {
        if (analysisProperty.get() != null) {
            filteredAnnotations = new FilteredList<>(analysisProperty.get().getAnnotations().sorted(annotationComparator));
            SortedList<Annotation> sortedAnnotations = new SortedList<>(filteredAnnotations);
            sortedAnnotations.comparatorProperty().bind(variantsTable.comparatorProperty());
            variantsTable.setItems(sortedAnnotations);
        } else {
            variantsTable.setItems(null);
        }
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
    }


    @FXML
    private void resetVariantSort() {
        variantsTable.getSortOrder().clear();
    }


    private void initView() {

        searchInTableTf.setLeft(searchIcon);
        // hide searchUI elements
        setSearchUIVisible(false);

        initTableMenu();
        initGenesPanelCombobox();



        splitPane.setDividerPositions();
    }

    /**
     * Hide the search UI elements
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
        initColumnVisibilityPopOver();
        initHideVariantsPopOver();
    }


    private void initGenesPanelCombobox() {
//        try {

            panelsCb.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                setAnnotationsFilters();
            });
//        } catch (SQLException e) {
//            logger.error(e.getMessage(), e);
//        }
    }


    private void initTableExportButton() {

    }


    private void initColumnVisibilityPopOver() {
//        if (columnsVisibilityDropmenu == null) {
//            columnsVisibilityDropmenu = new DropDownMenu(App.getBundle().getString("analysisview.variants.menu.columnvisibility"));
//            FontIcon icon = new FontIcon("mdmz-view_column");
//            columnsVisibilityDropmenu.setGraphic(icon);
//            columnsVisibilityDropmenu.getStyleClass().add("button-link");
//        }
//
        visibleColumnsPopOver = new PopOver();
        visibleColumnsPopOver.setAnimated(false);
        visibleColumnsPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        ColumnsVisivilityDropDownMenuContent columnsVisivilityDropDownMenuContent = new ColumnsVisivilityDropDownMenuContent();
        columnsVisivilityDropDownMenuContent.setVariantTableBuilder(tableBuilder);
//        columnsVisibilityDropmenu.setContentNode(columnsVisivilityDropDownMenuContent);

        visibleColumnsPopOver.setContentNode(columnsVisivilityDropDownMenuContent);
;
    }


    private void initHideVariantsPopOver() {
//        hideVariantsDropmenu = new DropDownMenu(App.getBundle().getString("analysisview.variants.menu.hidevariants"));
//        FontIcon icon = new FontIcon("mdmz-visibility_off");
//        hideVariantsDropmenu.setGraphic(icon);
//        hideVariantsDropmenu.getStyleClass().add("button-link");
//
        hideVariantDropDownMenuContent = new HideVariantDropDownMenuContent();
        hideVariantDropDownMenuContent.predicateProperty().addListener((observable, oldValue, newValue) -> {
            setAnnotationsFilters();
        });
//        hideVariantsDropmenu.setContentNode(hideVariantDropDownMenuContent);
        hideVariantPopOver = new PopOver();
        hideVariantPopOver.setAnimated(false);
        hideVariantPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        hideVariantPopOver.setContentNode(hideVariantDropDownMenuContent);

    }


    public void setVisibleTranscript(Transcript transcript) {
        analysisProperty.get().getAnnotations().parallelStream().forEach(a -> {
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
        Button bOk = dialog.getButton(ButtonType.OK);
        Button bCancel = dialog.getButton(ButtonType.CANCEL);
        bOk.setOnAction(e -> {
            dialog.clear();
            FileChooser fc = FileChooserUtils.getFileChooser();
            fc.setInitialFileName(analysisProperty.get().getName().replaceAll("[/.]", "_") + ".xlsx");
            File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
                user.savePreferences();
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
                        TableExporter.exportTableToExcel(analysisProperty.get(), dialog.getValue().getTable(), columnsToWrite, selectedFile);
                    } catch (IOException exception) {
                        logger.error(exception.getMessage(), exception);
                        Platform.runLater(() -> Message.error(exception.getMessage(), exception));
                        return 1;
                    }
                    return 0;
                });
            }
        });
        bCancel.setOnAction(e -> {
            dialog.clear();
            App.get().getAppController().getDialogPane().hideDialog(dialog);
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
        GenePanel selectedGenePanel = panelsCb.getValue();
        panelsCb.setItems(DAOController.getGenesPanelDAO().getGenesPanels());
        // add a null value for unselect panels
        panelsCb.getItems().add(0, null);
        panelsCb.getSelectionModel().select(selectedGenePanel);
    }


    @FXML
    private void addPanelHandler() {
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_EDIT_GENEPANEL)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
            return;
        }
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
                        DAOController.getGenesPanelDAO().addGenesPanel(dialog.getValue().getName(), dialog.getValue().getSelectedGenes());
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

    public FilterTableView<Annotation> getVariantsTable() {return variantsTable;}

    public VariantTableBuilder getTableBuilder() {return tableBuilder;}

    public AnalysisViewVariantDetailController getVariantDetailController() {return variantDetailController;}

    public SplitPane getSplitPane() {return splitPane;}

    public AnchorPane getVariantDetailContainer() {return variantDetailContainer;}

    public void setDividerPosition() {
        double divPos = (splitPane.getHeight() - (340 + 8)) / splitPane.getHeight();
        splitPane.setDividerPositions(divPos);
    }

    public Analysis getAnalysis() {
        return analysisProperty.get();
    }

    public SimpleObjectProperty<Analysis> analysisProperty() {
        return analysisProperty;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysisProperty.set(analysis);
    }

    public void clear() {
//        tableBuilder.clear();
//        variantDetailController.clear();
    }

    @FXML
    private void showVisibleColumnsPopOver() {
        visibleColumnsPopOver.show(visibleColumnsBtn);
    }

    @FXML
    private void showHideVariantsPopOver() {
        hideVariantPopOver.show(hideVariantsBtn);
    }
}
