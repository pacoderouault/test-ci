package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GenePanel;
import ngsdiaglim.utils.ListSelectionViewUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ListSelectionView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ReportSelectGenes extends ReportPane {

    private static final Logger logger = LogManager.getLogger(ReportSelectGenes.class);

    @FXML private ComboBox<GenePanel> panelCb;
    @FXML private TextField geneFilterTf;
    @FXML private ListSelectionView<Gene> genesListView;

    public ReportSelectGenes(AnalysisViewReportBGMController analysisViewReportBGMController) {
        super(analysisViewReportBGMController);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ReportSelectGenes.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        init();
        panelCb.getSelectionModel().select(0);
    }

    private void init() {
        initSelectionListView();
        initPanelCb();
        initGeneFilter();
        fillSourcesItems();
    }

    private void initSelectionListView() {
        ListSelectionViewUtils.rewriteButtons(genesListView);
    }

    private void initGeneFilter() {
        geneFilterTf.textProperty().addListener((osb, oldV, newV) -> {
            if (newV.isEmpty()) {
                fillSourcesItems();
            }
            else {
                genesListView.getSourceItems().clear();
                if (panelCb.getValue() != null) {
                    panelCb.getValue().getGenes().stream()
                            .filter(g -> StringUtils.containsIgnoreCase(g.getGeneName(), newV))
                            .forEach(this::addGeneToListView);
                }
            }
        });
    }

    private void initPanelCb() {
        // fill panels combobox
        try {
            panelCb.getItems().setAll(DAOController.getGenesPanelDAO().getGenesPanels());
            panelCb.getItems().add(0, null);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage(), e);
        }

        panelCb.valueProperty().addListener(o -> fillSourcesItems());
    }

    private void fillSourcesItems() {
        genesListView.getSourceItems().clear();
        if (panelCb.getValue() != null) {
            panelCb.getValue().getGenes().forEach(this::addGeneToListView);
        } else {
            reportController.getAnalysis().getAnalysisParameters().getGeneSet().getGenes().forEach((k, v) -> addGeneToListView(v));
        }
    }

    private void addGeneToListView(Gene gene) {
        boolean sourceContainsGene;
        boolean targetContainsGene;
        sourceContainsGene = genesListView.getSourceItems().stream().anyMatch(g -> g.equals(gene));
        targetContainsGene = genesListView.getTargetItems().stream().anyMatch(g -> g.equals(gene));
        if (!targetContainsGene && !sourceContainsGene) {
            genesListView.getSourceItems().add(gene);
        }
    }


    public ObservableList<Gene> getGenes() {
        return genesListView.getTargetItems();
    }

    public Set<String> getGenesSet() {
        Set<String> genesSet = new HashSet<>();
        genesListView.getTargetItems().forEach(g -> genesSet.add(g.getGeneName().toUpperCase()));
        return genesSet;
    }


    @Override
    String checkForm() {
        if (genesListView.getTargetItems().isEmpty()) {
            return App.getBundle().getString("analysisviewreports.selectgenes.msg.err.emptyGenes");
        }
        return null;
    }

}
