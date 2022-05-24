package ngsdiaglim.controllers.analysisview.reports.anapath;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.comparators.AnnotationComparator;
import ngsdiaglim.controllers.VariantTableBuilder;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.reports.anapath.AnnotationExporter;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.AnnotationCommentary;
import ngsdiaglim.modeles.variants.VariantCommentary;
import ngsdiaglim.modeles.variants.populations.GnomAD;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class AnalysisViewReportAnapathController extends VBox {

    private final Logger logger = LogManager.getLogger(AnalysisViewReportAnapathController.class);

    @FXML private CheckBox disableFiltersCb;

    @FXML private FlowPane filtersFlowpane;
    @FXML private CheckBox filterFalsePositiveCb;
    @FXML private CheckBox filterVAFCb;
    @FXML private CheckBox filterDepthCb;
    @FXML private CheckBox filterSynonymousCb;
    @FXML private CheckBox filterNonCodingCb;
    @FXML private CheckBox filterGnomadCb;
    @FXML private CheckBox filterOccurenceCb;
    @FXML private CheckBox filterPathogenicityCb;
    @FXML private TextField filterVafTf;
    @FXML private TextField filterDepthTf;
    @FXML private TextField filterOccurenceTf;
    @FXML private TextField filterGnomadTf;
    @FXML private ComboBox<ACMG> filterPathogenicityCbx;
    @FXML private TableView<Annotation> previewTable;


    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();
    private FilteredList<Annotation> filteredAnnotations;
    private VariantTableBuilder tableBuilder;

    private final AnnotationComparator annotationComparator = new AnnotationComparator();

    public AnalysisViewReportAnapathController() {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewReportAnapath.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (
                IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }


        analysis.addListener((obs, oldV, newV) -> {
            clear();
            updateView();
        });


        init();
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis.set(analysis);
    }

    private void init() {

        filtersFlowpane.disableProperty().bind(disableFiltersCb.selectedProperty());

        initTable();
        initFilters();

    }

    private void initTable() {
        this.tableBuilder = new VariantTableBuilder(previewTable);
        tableBuilder.initColumnsMap();

        addTableColumn(VariantsTableColumns.HOTSPOT);
        addTableColumn(VariantsTableColumns.CONTIG);
        addTableColumn(VariantsTableColumns.POSITION);
        addTableColumn(VariantsTableColumns.REF);
        addTableColumn(VariantsTableColumns.ALT);
        addTableColumn(VariantsTableColumns.CONSEQUENCE);
        addTableColumn(VariantsTableColumns.GENE);
        addTableColumn(VariantsTableColumns.TRANSCRIPT);
        previewTable.getColumns().add(createCodingMutColumn());
        previewTable.getColumns().add(createProteinMutColumn());
        addTableColumn(VariantsTableColumns.VAF);
        addTableColumn(VariantsTableColumns.DEPTH);
        addTableColumn(VariantsTableColumns.ACMG);
        addTableColumn(VariantsTableColumns.OCCURENCE);
        addTableColumn(VariantsTableColumns.OCCURENCE_IN_RUN);
        previewTable.getColumns().add(createCommentColumn());
        addTableColumn(VariantsTableColumns.EXON);
        addTableColumn(VariantsTableColumns.INTRON);
        addTableColumn(VariantsTableColumns.GNOMAD_MAX);
        addTableColumn(VariantsTableColumns.CLIVAR_SIGN);
        addTableColumn(VariantsTableColumns.CADD);
        addTableColumn(VariantsTableColumns.SIFT);
        addTableColumn(VariantsTableColumns.POLYPHEN2_HDIV);
        addTableColumn(VariantsTableColumns.POLYPHEN2_HVAR);
        addTableColumn(VariantsTableColumns.REVEL);
        addTableColumn(VariantsTableColumns.MVP);
        addTableColumn(VariantsTableColumns.GERP);
        addTableColumn(VariantsTableColumns.SPLICE_AI);

        tableBuilder.getColumn(VariantsTableColumns.ACMG).setPrefWidth(150);
        tableBuilder.getColumn(VariantsTableColumns.OCCURENCE_IN_RUN).setPrefWidth(75);
    }

    private void addTableColumn(VariantsTableColumns col) {
        TableColumn<Annotation, ?> indexCol = tableBuilder.getColumn(col);
        if (indexCol != null) previewTable.getColumns().add(indexCol);
    }

    private TableColumn<Annotation, String> createCodingMutColumn() {
        TableColumn<Annotation, String> col = new TableColumn<>(App.getBundle().getString("analysisview.variants.table.codingmut"));
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTranscriptConsequence().getCodingMutation()));
        return col;
    }

    private TableColumn<Annotation, String> createProteinMutColumn() {
        TableColumn<Annotation, String> col = new TableColumn<>(App.getBundle().getString("analysisview.variants.table.proteinmut"));
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTranscriptConsequence().getProteinMutation()));
        return col;
    }

    private TableColumn<Annotation, String> createCommentColumn() {
        TableColumn<Annotation, String> col = new TableColumn<>(App.getBundle().getString("analysisview.variants.table.comments"));
        col.setCellValueFactory(data -> new SimpleStringProperty(getComments(data.getValue())));
        return col;
    }


    private String getComments(Annotation a) {
        StringJoiner sj = new StringJoiner("|");
        try {
            List<VariantCommentary> variantComments = DAOController.getVariantCommentaryDAO().getVariantCommentaries(a.getVariant().getId());
            List<AnnotationCommentary> annotationComments = DAOController
                    .getAnnotationCommentaryDAO()
                    .getAnnotationCommentaries(a.getVariant().getId(), analysis.get().getId());
            for (VariantCommentary c : variantComments) {
                sj.add(c.getUsername() + ":'" + c.getComment() + "'");
            }
            for (AnnotationCommentary c : annotationComments) {
                sj.add(c.getUsername() + ":'" + c.getComment() + "'");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Platform.runLater(() -> Message.error(e.getMessage(), e));
        }
        return sj.toString();
    }


    private void initFilters() {

        User user = App.get().getLoggedUser();

        disableFiltersCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_DISABLE_FILTERS)));
        disableFiltersCb.selectedProperty().addListener(l -> filterTable());

        filterFalsePositiveCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_FALSE_POSITIVE)));
        filterFalsePositiveCb.selectedProperty().addListener(l -> filterTable());

        filterSynonymousCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_SYNONYMOUS)));
        filterSynonymousCb.selectedProperty().addListener(l -> filterTable());

        filterNonCodingCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_NON_CODING)));
        filterNonCodingCb.selectedProperty().addListener(l -> filterTable());

        filterVAFCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_NON_CODING)));
        filterVAFCb.selectedProperty().addListener(l -> filterTable());
        filterVafTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_VAF_MIN));
        filterVafTf.textProperty().addListener(l -> filterTable());

        filterDepthCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_DEPTH)));
        filterDepthCb.selectedProperty().addListener(l -> filterTable());
        filterDepthTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_DEPTH_MIN));
        filterDepthTf.textProperty().addListener(l -> filterTable());

        filterGnomadCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_GNOMAD)));
        filterGnomadCb.selectedProperty().addListener(l -> filterTable());
        filterGnomadTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_GNOMAD_MAX));
        filterGnomadTf.textProperty().addListener(l -> filterTable());


        filterOccurenceCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_OCC)));
        filterOccurenceCb.selectedProperty().addListener(l -> filterTable());
        filterOccurenceTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_OCC_MAX));
        filterOccurenceTf.textProperty().addListener(l -> filterTable());

        filterPathogenicityCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_PATHOGENICITY)));
        filterPathogenicityCb.selectedProperty().addListener(l -> filterTable());
        filterPathogenicityCbx.getItems().setAll(ACMG.values());
        int minPathoValue = Integer.parseInt(user.getPreferences().getPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_PATHOGENICITY_MIN));
        try {
            filterPathogenicityCbx.getSelectionModel().select(ACMG.getFromPathogenicityValue(minPathoValue));
        } catch (Exception ignored) {}

        filterPathogenicityCbx.valueProperty().addListener(l -> filterTable());
    }

    private void filterTable() {
        saveFiltersState();
        filteredAnnotations.setPredicate(computePredicate());
    }


    private Predicate<Annotation> computePredicate() {

        if (disableFiltersCb.isSelected()) return null;

        if (!StringUtils.isBlank(filterVafTf.getText()) && !NumberUtils.isDouble(filterVafTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidVaf"));
            return null;
        }
        if (!StringUtils.isBlank(filterOccurenceTf.getText()) && !NumberUtils.isDouble(filterOccurenceTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidOccurence"));
            return null;
        }
        if (!StringUtils.isBlank(filterDepthTf.getText()) && !NumberUtils.isDouble(filterDepthTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidDepth"));
            return null;
        }
        if (!StringUtils.isBlank(filterGnomadTf.getText()) && !NumberUtils.isDouble(filterGnomadTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidGnomad"));
            return null;
        }

        return a -> {
            if (a.isReported()) return true;

            if (filterPathogenicityCb.isSelected() && filterPathogenicityCbx.getValue() != null) {
                if (a.getVariant().getAcmg().getPathogenicityValue() < filterPathogenicityCbx.getValue().getPathogenicityValue()) return false;
            }

            if (!filterFalsePositiveCb.isSelected() && a.getVariant().isFalsePositive()) return false;

            if (filterVAFCb.isSelected()) {
                double minVaf;
                if (NumberUtils.isDouble(filterVafTf.getText())) {
                    minVaf = Double.parseDouble(filterVafTf.getText());
                } else {
                    minVaf = 0;
                }
                if (a.getVaf() < minVaf) return false;
            }

            if (filterOccurenceCb.isSelected()) {
                double minOcc;
                if (NumberUtils.isDouble(filterOccurenceTf.getText())) {
                    minOcc = Double.parseDouble(filterOccurenceTf.getText());
                } else {
                    minOcc = 0;
                }
                if (a.getVariant().getOccurrence() > minOcc) return false;
            }

            if (filterDepthCb.isSelected()) {
                double minDepth;
                if (NumberUtils.isDouble(filterDepthTf.getText())) {
                    minDepth = Double.parseDouble(filterDepthTf.getText());
                } else {
                    minDepth = 0;
                }
                if (a.getDepth() < minDepth) return false;
            }

            if (filterGnomadCb.isSelected() && NumberUtils.isDouble(filterGnomadTf.getText())) {
                double maxGnomad;
                if (NumberUtils.isDouble(filterGnomadTf.getText())) {
                    maxGnomad = Double.parseDouble(filterGnomadTf.getText());
                } else {
                    maxGnomad = 1;
                }
                GnomadPopulationFreq maxPop = a.getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME);
                if (maxPop != null && maxPop.getAf() > maxGnomad) return false;
            }
            if (!filterSynonymousCb.isSelected()) {
                if (a.getTranscriptConsequence().getConsequences().contains(EnsemblConsequence.SYNONYMOUS_VARIANT)) return false;
            }
            if (!filterNonCodingCb.isSelected()) {
                if (a.getTranscriptConsequence().getExon() == null || a.getTranscriptConsequence().getExon().isEmpty()) {
                    for (EnsemblConsequence cons : a.getTranscriptConsequence().getConsequences()) {
                        if (!cons.equals(EnsemblConsequence.SPLICE_ACCEPTOR_VARIANT) && !cons.equals(EnsemblConsequence.SPLICE_DONOR_VARIANT)) {
                            return false;
                        }
                    }
                } else {
                    return !a.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.PRIME_3_UTR_VARIANT) && !a.getTranscriptConsequence().getConsequence().equals(EnsemblConsequence.PRIME_5_UTR_VARIANT);
                }
            }
            return true;
        };
    }



    private void saveFiltersState() {

        User user = App.get().getLoggedUser();
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_DISABLE_FILTERS, String.valueOf(disableFiltersCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_FALSE_POSITIVE, String.valueOf(filterFalsePositiveCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_SYNONYMOUS, String.valueOf(filterSynonymousCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_NON_CODING, String.valueOf(filterNonCodingCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_VAF, String.valueOf(filterVAFCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_VAF_MIN, filterVafTf.getText());
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_DEPTH, String.valueOf(filterDepthCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_DEPTH_MIN, filterDepthTf.getText());
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_GNOMAD, String.valueOf(filterGnomadCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_GNOMAD_MAX, filterGnomadTf.getText());
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_OCC, String.valueOf(filterOccurenceCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_OCC_MAX, filterOccurenceTf.getText());
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_PATHOGENICITY, String.valueOf(filterPathogenicityCb.isSelected()));
        user.setPreference(DefaultPreferencesEnum.REPORT_ANAPATH_FILTER_PATHOGENICITY_MIN, String.valueOf(filterPathogenicityCbx.getValue().getPathogenicityValue()));

        user.savePreferences();
    }

    private void updateView() {
        this.filteredAnnotations = new FilteredList<>(analysis.get().getAnnotations().sorted(annotationComparator));
        previewTable.setItems(filteredAnnotations);
        filterTable();
    }

    @FXML
    private void exportFile() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        fc.setInitialFileName(analysis.get().getSampleName() + ".xls");
        File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("analysisviewreports.msg.createReport"));
//            wid.addTaskEndNotification(r -> {
//
//            });
            wid.exec("createReport", inputParams -> {
                AnnotationExporter annotationExporter = new AnnotationExporter(analysis.get(), previewTable);
                try {
                    annotationExporter.export(selectedFile);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    Platform.runLater(() -> Message.error(e.getMessage(), e));
                }
                return 0;
            });
        }
    }

    public void clear() {

    }
}
