package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.variantsTableCells.EnsemblConsequenceTableCell;
import ngsdiaglim.controllers.cells.variantsTableCells.PopulationFrequencyTableCell;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomAD;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ReportVous extends ReportPane {

    private static final Logger logger = LogManager.getLogger(ReportVous.class);

    @FXML private CheckBox dontReportVous;
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

    @FXML private FlowPane filtersContainer;
    @FXML private HBox tableMenuContainer;
    @FXML private TableView<Annotation> previewTable;
    @FXML private TableColumn<Annotation, String> geneCol;
    @FXML private TableColumn<Annotation, EnsemblConsequence> consequenceCol;
    @FXML private TableColumn<Annotation, String> hgvscCol;
    @FXML private TableColumn<Annotation, String> hgvspCol;
    @FXML private TableColumn<Annotation, Float> vafCol;
    @FXML private TableColumn<Annotation, Integer> depthCol;
    @FXML private TableColumn<Annotation, Integer> occurenceCol;
    @FXML private TableColumn<Annotation, GnomadPopulationFreq> maxGnomadCol;
    @FXML private TableColumn<Annotation, Void> deleteCol;

    private final Set<Annotation> toDeleteAnnotations = new HashSet<>();
    private final Set<Annotation> deletedAnnotations = new HashSet<>();
    private ObservableList<Annotation> annotations;
    private FilteredList<Annotation> filteredAnnotations;

    public ReportVous(AnalysisViewReportBGMController analysisViewReportBGMController) {
        super(analysisViewReportBGMController);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ReportVous.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        init();

    }

    @Override
    String checkForm() {
        return null;
    }

    private void init() {
        initFilters();
        initTable();
        fillTable();

        dontReportVous.selectedProperty().addListener((obs, oldV, newV) -> {
            filtersContainer.setDisable(newV);
            previewTable.setDisable(newV);
            tableMenuContainer.setDisable(newV);
        });

        reportController.getReportSelectGenes().getGenes().addListener((ListChangeListener<Gene>) change -> fillTable());
        reportController.getReportSelectVariants().getReportedVariants().addListener((ListChangeListener<Annotation>) change -> computePredicate());
    }

    private void initFilters() {
        filterFalsePositiveCb.selectedProperty().addListener(l -> filterTable());
        filterSynonymousCb.selectedProperty().addListener(l -> filterTable());
        filterNonCodingCb.selectedProperty().addListener(l -> filterTable());
        filterVAFCb.selectedProperty().addListener(l -> filterTable());
        filterVafTf.textProperty().addListener(l -> filterTable());
        filterDepthCb.selectedProperty().addListener(l -> filterTable());
        filterDepthTf.textProperty().addListener(l -> filterTable());
        filterGnomadCb.selectedProperty().addListener(l -> filterTable());
        filterGnomadTf.textProperty().addListener(l -> filterTable());
        filterOccurenceCb.selectedProperty().addListener(l -> filterTable());
        filterOccurenceTf.textProperty().addListener(l -> filterTable());
        filterPathogenicityCb.selectedProperty().addListener(l -> filterTable());
        filterPathogenicityCbx.getItems().setAll(ACMG.values());
        filterPathogenicityCbx.getSelectionModel().select(ACMG.UNCERTAIN_SIGNIGICANCE);
        filterPathogenicityCbx.valueProperty().addListener(l -> filterTable());
    }

    private void initTable() {

        geneCol.setCellValueFactory(data -> {
            if (data.getValue().getTranscriptConsequence() != null) {
                return data.getValue().getTranscriptConsequence().geneNameProperty();
            } else {
                return null;
            }
        });
        consequenceCol.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().consequenceProperty());
        consequenceCol.setCellFactory(data -> new EnsemblConsequenceTableCell<>());
        hgvscCol.setCellValueFactory(data -> {
            if (data.getValue().getTranscriptConsequence() != null) {
                return data.getValue().getTranscriptConsequence().hgvscProperty();
            } else {
                return null;
            }
        });
        hgvspCol.setCellValueFactory(data -> {
            if (data.getValue().getTranscriptConsequence() != null) {
                return data.getValue().getTranscriptConsequence().hgvspProperty();
            } else {
                return null;
            }
        });
        vafCol.setCellValueFactory(data -> data.getValue().vafProperty().asObject());
        depthCol.setCellValueFactory(data -> data.getValue().depthProperty().asObject());
        occurenceCol.setCellValueFactory(data -> data.getValue().getVariant().occurrenceProperty().asObject());
        maxGnomadCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME)));
        maxGnomadCol.setCellFactory(data -> new PopulationFrequencyTableCell());
        deleteCol.setCellFactory(data -> new TableCell<>() {
            private final CheckBox cb = new CheckBox();
            private final ChangeListener<Boolean> selectedListener = (obs, oldV, newV) -> {
                if (newV) {
                    addToDeleteList();
                } else {
                    removeToDeleteList();
                }
            };

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty) {
                    setGraphic(null);
                } else {
                    Annotation a = getTableRow().getItem();
                    if (a != null) {
                        cb.selectedProperty().removeListener(selectedListener);
                        cb.setSelected(toDeleteAnnotations.contains(a));
                        cb.selectedProperty().addListener(selectedListener);
                        setGraphic(cb);
                    } else {
                        setGraphic(null);
                    }
                }
            }

            private void addToDeleteList() {
                Annotation a = getTableRow().getItem();
                if (a != null) {
                    toDeleteAnnotations.add(a);
                }
            }

            private void removeToDeleteList() {
                Annotation a = getTableRow().getItem();
                if (a != null) {
                    toDeleteAnnotations.remove(a);
                }
            }
        });
    }


    @FXML
    private void fillTable() {
        toDeleteAnnotations.clear();
        deletedAnnotations.clear();
        annotations = reportController.getAnalysis().getAnnotations();
        filteredAnnotations = new FilteredList<>(annotations);
        filterTable();
        previewTable.setItems(filteredAnnotations);
        previewTable.refresh();
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

            // get variants on reported genes
            if (!reportController.getReportSelectGenes().getGenesSet().contains(a.getTranscriptConsequence().getGeneName().toUpperCase()))
                return false;

            // dont keep reported variants
            if (reportController.getReportSelectVariants().getReportedVariants().contains(a)) return false;

            // dont whow manually deleted annotations
            if (deletedAnnotations.contains(a)) return false;

            if (filterPathogenicityCb.isSelected() && filterPathogenicityCbx.getValue() != null) {
                if (a.getVariant().getAcmg().getPathogenicityValue() < filterPathogenicityCbx.getValue().getPathogenicityValue())
                    return false;
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
                GnomadPopulationFreq maxPop = a.getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME);
                if (maxPop != null && maxPop.getAf() > maxGnomad)
                    return false;
            }
            if (!filterSynonymousCb.isSelected()) {
                if (a.getTranscriptConsequence().getConsequences().contains(EnsemblConsequence.SYNONYMOUS_VARIANT))
                    return false;
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


    @FXML
    private void removeSelectedVariants() {
        deletedAnnotations.addAll(toDeleteAnnotations);
        filterTable();
    }


    public List<Annotation> getVous() {
        return previewTable.getItems();
    }
}
