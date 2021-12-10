package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.VariantTableBuilder;
import ngsdiaglim.controllers.analysisview.ColumnsVisivilityDropDownMenuContent2;
import ngsdiaglim.controllers.ui.DropDownMenu;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.users.ColumnsExport;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.PredicateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Predicate;

public class ExportTableDialog extends DialogPane.Dialog<ExportTableDialog.ExportTableData> {

    private static final Logger logger = LogManager.getLogger(ExportTableDialog.class);

    @FXML private VBox dialogContainer;
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
    @FXML private HBox columnsDropMenuContainer;
    private DropDownMenu exportTableDropmenu;

//    private final Analysis analysis;
    private VariantTableBuilder tableBuilder;
    private final ObservableList<Annotation> annotations;
    private final FilteredList<Annotation> filteredAnnotations;
    private ColumnsExport columnsExport;
    
    public ExportTableDialog(DialogPane pane, ObservableList<Annotation> annotations) {
        super(pane, DialogPane.Type.INPUT);
//        this.analysis = analysis;
//        this.variantTableBuilder = variantTableBuilder;
        this.annotations = annotations;
        this.filteredAnnotations = new FilteredList<>(this.annotations);
//        setValue(annotations);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ExportTableDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("exporttabledialog.title"));
        setContent(dialogContainer);


        initView();

        setValue(new ExportTableData(previewTable, tableBuilder, columnsExport));
    }

    private void initView() {
        initTable();
        initCollunsVisibleDropMenu();
        initFilters();

        filterTable();
    }


    private void initFilters() {

        User user = App.get().getLoggedUser();

        filterFalsePositiveCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_FALSE_POSITIVE)));
        filterFalsePositiveCb.selectedProperty().addListener(l -> filterTable());

        filterSynonymousCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_SYNONYMOUS)));
        filterSynonymousCb.selectedProperty().addListener(l -> filterTable());

        filterSynonymousCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_NON_CODING)));
        filterNonCodingCb.selectedProperty().addListener(l -> filterTable());

        filterVAFCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_NON_CODING)));
        filterVAFCb.selectedProperty().addListener(l -> filterTable());
        filterVafTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_VAF_MIN));
        filterVafTf.textProperty().addListener(l -> filterTable());

        filterDepthCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_DEPTH)));
        filterDepthCb.selectedProperty().addListener(l -> filterTable());
        filterDepthTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_DEPTH_MIN));
        filterDepthTf.textProperty().addListener(l -> filterTable());

        filterGnomadCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_GNOMAD)));
        filterGnomadCb.selectedProperty().addListener(l -> filterTable());
        filterGnomadTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_GNOMAD_MAX));
        filterGnomadTf.textProperty().addListener(l -> filterTable());


        filterOccurenceCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_OCC)));
        filterOccurenceCb.selectedProperty().addListener(l -> filterTable());
        filterOccurenceTf.setText(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_OCC_MAX));
        filterOccurenceTf.textProperty().addListener(l -> filterTable());

        filterPathogenicityCb.setSelected(Boolean.parseBoolean(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_PATHOGENICITY)));
        filterPathogenicityCb.selectedProperty().addListener(l -> filterTable());
        filterPathogenicityCbx.getItems().setAll(ACMG.values());
        int minPathoValue = Integer.parseInt(user.getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_EXPORT_PATHOGENICITY_MIN));
        try {
            filterPathogenicityCbx.getSelectionModel().select(ACMG.getFromPathogenicityValue(minPathoValue));
        } catch (Exception ignored) {}

        filterPathogenicityCbx.valueProperty().addListener(l -> filterTable());
    }

    private void initCollunsVisibleDropMenu() {
        exportTableDropmenu = new DropDownMenu(App.getBundle().getString("exportvariants.lb.colonnes"));

        ColumnsVisivilityDropDownMenuContent2 exportColumnsDropDownMenu = new ColumnsVisivilityDropDownMenuContent2(previewTable, columnsExport);
        exportTableDropmenu.setContentNode(exportColumnsDropDownMenu);
        columnsDropMenuContainer.getChildren().add(exportTableDropmenu);
    }

    private void initTable() {

        tableBuilder = new VariantTableBuilder(previewTable);

        try {
            tableBuilder.buildTable(false);

            columnsExport = DAOController.getColumnsExportDAO().getColumnsExport(App.get().getLoggedUser().getId());
            if (columnsExport == null) {
                // init columns exports from visibles columns
                columnsExport = new ColumnsExport();
                for (VariantsTableColumns variantsTableColumns : ModuleManager.getAnalysisViewController().getVariantsViewController().getTableBuilder().getDefaultColumnsOrder()) {
                    if (ModuleManager.getAnalysisViewController().getVariantsViewController().getTableBuilder().getColumn(variantsTableColumns).isVisible()) {
                        columnsExport.addColumn(variantsTableColumns);
                    }
                }
//                for (VariantsTableColumns variantsTableColumns : tableBuilder.getDefaultColumnsOrder()) {
//                    if (tableBuilder.getColumn(variantsTableColumns).isVisible()) {
//                        columnsExport.addColumn(variantsTableColumns);
//                    }
//                }

            }
            for (VariantsTableColumns column : tableBuilder.getDefaultColumnsOrder()) {
                TableColumn<Annotation, ? > col = tableBuilder.getColumn(column);
                if (col != null) col.setVisible(columnsExport.hasColumn(column));
            }

            previewTable.getVisibleLeafColumns().addListener((ListChangeListener<TableColumn<Annotation, ?>>) change -> {
                try {
                    DAOController.getColumnsExportDAO().setColumnsExport(App.get().getLoggedUser().getId(), columnsExport);
                } catch (SQLException e) {
                    logger.error(e);
                }
            });

        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
        previewTable.setItems(filteredAnnotations);
    }


    private void filterTable() {
        filteredAnnotations.setPredicate(computePredicate());
    }

    private Predicate<Annotation> computePredicate() {

        if (!filterVafTf.getText().isEmpty() && !NumberUtils.isDouble(filterVafTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidVaf"));
            return null;
        }
        if (!NumberUtils.isDouble(filterOccurenceTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidOccurence"));
            return null;
        }
        if (!NumberUtils.isDouble(filterDepthTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidDepth"));
            return null;
        }
        if (!NumberUtils.isDouble(filterGnomadTf.getText())) {
            Message.error(App.getBundle().getString("exportvariants.msg.err.invalidGnomad"));
            return null;
        }

        return a -> {
            if (a.isReported()) return true;

            if (filterPathogenicityCb.isSelected() && filterPathogenicityCbx.getValue() != null) {
                if (a.getVariant().getAcmg().getPathogenicityValue() < filterPathogenicityCbx.getValue().getPathogenicityValue()) return false;
            }

            if (filterFalsePositiveCb.isSelected() && a.getVariant().isFalsePositive()) return false;

            if (filterVAFCb.isSelected()) {
                double minVaf = Double.parseDouble(filterVafTf.getText());
                if (a.getVaf() < minVaf) return false;
            }

            if (filterOccurenceCb.isSelected()) {
                double minOcc = Double.parseDouble(filterOccurenceTf.getText());
                if (a.getVariant().getOccurrence() > minOcc) return false;
            }

            if (filterDepthCb.isSelected()) {
                double minDepth = Double.parseDouble(filterDepthTf.getText());
                if (a.getDepth() < minDepth) return false;
            }

            if (filterGnomadCb.isSelected() && NumberUtils.isDouble(filterGnomadTf.getText())) {
                double maxGnomad = Double.parseDouble(filterGnomadTf.getText());
               if (a.getGnomADFrequencies().getMax() != null && a.getGnomADFrequencies().getMax().getAf() > maxGnomad) return false;
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
//        if (!filterFalsePositiveCb.isSelected()) {
//            predicate = PredicateUtils.addPredicates(predicate, a -> !a.getVariant().isFalsePositive());
//        }
//        if (filterPathogenicityCb.isSelected() && filterPathogenicityCbx.getValue() != null) {
//            if (filterPathogenicityCbx.getValue() == null) {
//                Message.error(App.getBundle().getString("exportvariants.msg.err.invalidPathogenicity"));
//                return null;
//            }
//            ACMG minPatho = filterPathogenicityCbx.getValue();
//            PredicateUtils.addPredicates(predicate, a -> a.getVariant().getAcmg().getPathogenicityValue() >= minPatho.getPathogenicityValue());
////            reportedPredicate = reportedPredicate.or(pathogenicityPredicate);
//        }
//        if (filterVAFCb.isSelected()) {
//            if (!filterVafTf.getText().isEmpty() && !NumberUtils.isDouble(filterVafTf.getText())) {
//                Message.error(App.getBundle().getString("exportvariants.msg.err.invalidVaf"));
//                return null;
//            }
//            double minVaf = Double.parseDouble(filterVafTf.getText());
//            predicate = PredicateUtils.addPredicates(predicate, a -> a.getVaf() >= minVaf);
//        }
//        if (filterOccurenceCb.isSelected()) {
//            if (!NumberUtils.isDouble(filterOccurenceTf.getText())) {
//                Message.error(App.getBundle().getString("exportvariants.msg.err.invalidOccurence"));
//                return null;
//            }
//            double minOcc = Double.parseDouble(filterOccurenceTf.getText());
//            predicate = PredicateUtils.addPredicates(predicate, a -> a.getVariant().getOccurrence() <= minOcc);
//        }
//        if (filterDepthCb.isSelected()) {
//            if (!NumberUtils.isDouble(filterDepthTf.getText())) {
//                Message.error(App.getBundle().getString("exportvariants.msg.err.invalidDepth"));
//                return null;
//            }
//            double minDepth = Double.parseDouble(filterDepthTf.getText());
//            predicate = PredicateUtils.addPredicates(predicate, a -> a.getDepth() >= minDepth);
//        }

//        if (filterGnomadCb.isSelected() && NumberUtils.isDouble(filterGnomadTf.getText())) {
//            if (!NumberUtils.isDouble(filterGnomadTf.getText())) {
//                Message.error(App.getBundle().getString("exportvariants.msg.err.invalidGnomad"));
//                return null;
//            }
//            double maxGnomad = Double.parseDouble(filterGnomadTf.getText());
//            predicate = PredicateUtils.addPredicates(predicate, a -> a.getGnomADFrequencies().getMax() == null || a.getGnomADFrequencies().getMax().getAf() <= maxGnomad);
//        }
//        if (!filterSynonymousCb.isSelected()) {
//            predicate = PredicateUtils.addPredicates(predicate, a -> !a.getTranscriptConsequence().getConsequences().contains(EnsemblConsequence.SYNONYMOUS_VARIANT));
//        }
//        if (!filterNonCodingCb.isSelected()) {
//            predicate = PredicateUtils.addPredicates(predicate, a -> {
//                if (a.getTranscriptConsequence().getExon() == null || a.getTranscriptConsequence().getExon().isEmpty()) {
//                    for (EnsemblConsequence cons : a.getTranscriptConsequence().getConsequences()) {
//                        if (cons.equals(EnsemblConsequence.SPLICE_ACCEPTOR_VARIANT) || cons.equals(EnsemblConsequence.SPLICE_DONOR_VARIANT)) {
//                            return true;
//                        }
//                    }
//                    return false;
//                }
//                return true;
//            });
//        }


//        Predicate<Annotation> reportedPredicate = Annotation::isReported;
//        if (filterPathogenicityCb.isSelected() && filterPathogenicityCbx.getValue() != null) {
//            if (filterPathogenicityCbx.getValue() == null) {
//                Message.error(App.getBundle().getString("exportvariants.msg.err.invalidPathogenicity"));
//                return null;
//            }
//            ACMG minPatho = filterPathogenicityCbx.getValue();
//            Predicate<Annotation> pathogenicityPredicate = a -> a.getVariant().getAcmg().getPathogenicityValue() >= minPatho.getPathogenicityValue();
//            reportedPredicate = reportedPredicate.or(pathogenicityPredicate);
//        }
//        if (predicate == null) {
//            return reportedPredicate;
//        } else {
//            return reportedPredicate.or(predicate);
//        }
    }


    public static class ExportTableData {

        private final TableView<Annotation> table;
        private final VariantTableBuilder tableBuilder;
        private final ColumnsExport columnsExport;


        public ExportTableData(TableView<Annotation> table, VariantTableBuilder tableBuilder, ColumnsExport columnsExport) {
            this.table = table;
            this.tableBuilder = tableBuilder;
            this.columnsExport = columnsExport;
        }

        public TableView<Annotation> getTable() {return table;}

        public VariantTableBuilder getTableBuilder() {return tableBuilder;}

        public ColumnsExport getColumnsExport() {return columnsExport;}
    }
}
