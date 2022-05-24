package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomAD;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.utils.ListSelectionViewUtils;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ListSelectionView;

import java.io.IOException;

public class ReportSelectVariants extends ReportPane {

    private static final Logger logger = LogManager.getLogger(ReportSelectVariants.class);

    @FXML private TextField gnomadFreqTf;
    @FXML private TextField geneNameTf;
    @FXML private CheckBox exonicCb;
    @FXML private CheckBox intronicCb;
    @FXML private CheckBox otherCb;
    @FXML private ListSelectionView<Annotation> annotationListSelection;
    private ObservableList<Annotation> annotationsList;
    private FilteredList<Annotation> filteredAnnotationsList;

    public ReportSelectVariants(AnalysisViewReportBGMController analysisViewReportBGMController) {
        super(analysisViewReportBGMController);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ReportSelectVariants.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }


        init();
        reportController.getReportSelectGenes().getGenes().addListener((ListChangeListener<Gene>) change -> fillListSelection());
    }

    private void init() {
        annotationsList = reportController.getAnalysis().getAnnotations();
        filteredAnnotationsList = new FilteredList<>(annotationsList);
        annotationListSelection.setSourceItems(filteredAnnotationsList);
        gnomadFreqTf.textProperty().addListener(o -> updatePredicate());
        geneNameTf.textProperty().addListener(o -> updatePredicate());
        exonicCb.selectedProperty().addListener(o -> updatePredicate());
        intronicCb.selectedProperty().addListener(o -> updatePredicate());
        otherCb.selectedProperty().addListener(o -> updatePredicate());

        ListSelectionViewUtils.rewriteButtons(annotationListSelection);

    }


    private void fillListSelection() {

        annotationsList.forEach(a -> {
            if (a.isReported()) {
                annotationListSelection.getTargetItems().add(a);
            }
        });
        updatePredicate();
    }


    private void updatePredicate() {
        filteredAnnotationsList.setPredicate(a -> {

            if (!reportController.getReportSelectGenes().getGenesSet().contains(a.getTranscriptConsequence().getGeneName().toUpperCase())) return false;
            if (geneNameTf.getText() != null && !geneNameTf.getText().isEmpty()) {
                if (!StringUtils.containsIgnoreCase(a.getGeneNames(), geneNameTf.getText())) return false;
            }
            if (NumberUtils.isDouble(gnomadFreqTf.getText().replaceAll(",", "."))) {
                double maxGnomadFreq = Double.parseDouble(gnomadFreqTf.getText().replaceAll(",", "."));
                GnomadPopulationFreq maxPop = a.getGnomAD().getMaxFrequency(GnomAD.GnomadSource.EXOME);
                if (maxPop != null && maxPop.getAf() > maxGnomadFreq) return false;
            }
            if (!otherCb.isSelected()) {
                if (!exonicCb.isSelected() && (a.getTranscriptConsequence() != null && a.getTranscriptConsequence().getExon() != null)) {
                    return false;
                }
                if (!intronicCb.isSelected() && (a.getTranscriptConsequence() != null && a.getTranscriptConsequence().getIntron() != null)) {
                    return false;
                }
            }
            return !annotationListSelection.getTargetItems().contains(a);
        });
    }


    public ObservableList<Annotation> getReportedVariants() {
        return annotationListSelection.getTargetItems();
    }

    @Override
    String checkForm() {
        return null;
    }
}
