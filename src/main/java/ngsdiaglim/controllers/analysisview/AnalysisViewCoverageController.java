package ngsdiaglim.controllers.analysisview;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.coverageTableCells.CoverageActionTableCell;
import ngsdiaglim.controllers.cells.coverageTableCells.CoverageQualityTableCell;
import ngsdiaglim.controllers.cells.coverageTreetableCell.*;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.StringJoiner;

public class AnalysisViewCoverageController extends HBox {

    private final static Logger logger = LogManager.getLogger(AnalysisViewCoverageController.class);

    @FXML private TableView<CoverageRegion> coverageTable;
    @FXML private TableColumn<CoverageRegion, CoverageQuality> qualityCol;
    @FXML private TableColumn<CoverageRegion, String> contigCol;
    @FXML private TableColumn<CoverageRegion, Integer> startCol;
    @FXML private TableColumn<CoverageRegion, Integer> endCol;
    @FXML private TableColumn<CoverageRegion, Integer> sizeCol;
    @FXML private TableColumn<CoverageRegion, Double> depthCol;
    @FXML private TableColumn<CoverageRegion, String> genesCol;
    @FXML private TableColumn<CoverageRegion, Void> actionsCol;
    @FXML private VBox specificCoverageBox;
    @FXML private TableView<SpecificCoverageRegion> specificCoverageTable;
    @FXML private TableColumn<SpecificCoverageRegion, String> specificCoverageNameCol;
    @FXML private TableColumn<SpecificCoverageRegion, String> specificCoverageContigCol;
    @FXML private TableColumn<SpecificCoverageRegion, Integer> specificCoverageStartCol;
    @FXML private TableColumn<SpecificCoverageRegion, Integer> specificCoverageEndCol;
    @FXML private TableColumn<SpecificCoverageRegion, Integer> specificCoverageTargetCovCol;
    @FXML private TableColumn<SpecificCoverageRegion, Double> specificCoverageMeanCovCol;
    @FXML private TableColumn<SpecificCoverageRegion, Integer> specificCoverageSizeCol;
    @FXML private TableColumn<CoverageRegion, Void> specificCoverageActionsCol;

    @FXML private TreeTableView<SpecificCoverageRegion> treeTableView;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageNameTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageContigTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageStartTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageEndTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageTargetCovTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageMeanCovTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, SpecificCoverageRegion> specificCoverageSizeTreeCol;
    @FXML private TreeTableColumn<SpecificCoverageRegion, Void> specificCoverageActionsTreeCol;


    @FXML private HBox coverageRegionsTableContainer;
    @FXML private VBox specificCoverageRegionsTableContainer;
    @FXML private Label noCoverageAnalysisLb;
    @FXML private Label noSpecificCoverageAnalysisLb;

    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();

    public AnalysisViewCoverageController() {

        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewCoverage.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        initView();

        analysis.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                updateView();
            }
        });
    }

    public Analysis getAnalysis() {
        return analysis.get();
    }

    public SimpleObjectProperty<Analysis> analysisProperty() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis.set(analysis);
    }

    private void initView() {
        qualityCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCoverageQuality()));
        qualityCol.setCellFactory(data -> new CoverageQualityTableCell());
        contigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        startCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        endCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        sizeCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSize()).asObject());
        depthCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAverageDepth()).asObject());
        genesCol.setCellValueFactory(data -> {
            StringJoiner sj = new StringJoiner(";");
            try {
                if (analysis.get() != null) {
                    analysis.get().getAnalysisParameters().getPanel().getRegions().stream()
                            .filter(r -> r.overlaps(data.getValue()))
                            .forEach(r -> sj.add(r.getName()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(sj.toString());
        });
        specificCoverageActionsCol.setCellFactory(data -> new CoverageActionTableCell());

        initSpecificCoverageTable();
        iniSpecificCoverageTreeTable();
    }

    private void initSpecificCoverageTable() {
//        specificCoverageNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecificCoverage().getName()));
//        specificCoverageNameCol.setCellFactory(data -> new SpecificCoverageRegionNameTableCell());
//        specificCoverageContigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
//        specificCoverageStartCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
//        specificCoverageEndCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
//        specificCoverageTargetCovCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSpecificCoverage().getMinCov()).asObject());
//        specificCoverageMeanCovCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAverageDepth()).asObject());
//        specificCoverageSizeCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSize()).asObject());
//        actionsCol.setCellFactory(data -> new CoverageActionTableCell());
    }

    private void iniSpecificCoverageTreeTable() {
        System.out.println(specificCoverageNameTreeCol);
        specificCoverageNameTreeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getValue()));
        specificCoverageNameTreeCol.setCellFactory(data -> new CoverageNameTreeTableCell());
        specificCoverageContigTreeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getValue()));
        specificCoverageContigTreeCol.setCellFactory(data -> new CoverageContigTreeTableCell());
        specificCoverageStartTreeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getValue()));
        specificCoverageStartTreeCol.setCellFactory(data -> new CoverageStartTreeTableCell());
        specificCoverageEndTreeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getValue()));
        specificCoverageEndTreeCol.setCellFactory(data -> new CoverageEndTreeTableCell());
        specificCoverageTargetCovTreeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getValue()));
        specificCoverageTargetCovTreeCol.setCellFactory(data -> new CoverageTargetDepthTreeTableCell());
        specificCoverageMeanCovTreeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getValue()));
        specificCoverageMeanCovTreeCol.setCellFactory(data -> new CoverageMeanDepthTreeTableCell());
    }

    private void fillSpecificCoverageTreeTable(ObservableList<SpecificCoverageRegion> specificCoverageRegions) {
        TreeItem<SpecificCoverageRegion> itemRoot = new TreeItem<>(null);

        for (SpecificCoverageRegion scr : specificCoverageRegions) {
            TreeItem<SpecificCoverageRegion> item = new TreeItem<>(scr);
            itemRoot.getChildren().add(item);
        }
        treeTableView.setRoot(itemRoot);
    }


    private void updateView() {

        if (analysis.get().getCoverageFile() == null || !analysis.get().getCoverageFile().exists()) {
            noCoverageAnalysisLb.setVisible(true);
            noCoverageAnalysisLb.setManaged(true);
            coverageTable.setManaged(false);
            coverageTable.setVisible(false);
        } else {
            noCoverageAnalysisLb.setVisible(false);
            noCoverageAnalysisLb.setManaged(false);
            coverageTable.setManaged(true);
            coverageTable.setVisible(true);
        }

        if (analysis.get().getSpecCoverageFile() == null || !analysis.get().getSpecCoverageFile().exists()) {
            noSpecificCoverageAnalysisLb.setVisible(true);
            noSpecificCoverageAnalysisLb.setManaged(true);
            specificCoverageTable.setManaged(false);
            specificCoverageTable.setVisible(false);
        } else {
            noSpecificCoverageAnalysisLb.setVisible(false);
            noSpecificCoverageAnalysisLb.setManaged(false);
            specificCoverageTable.setManaged(true);
            specificCoverageTable.setVisible(true);
        }


        coverageTable.setItems(analysis.get().getCoverageRegions());
        specificCoverageBox.setVisible(analysis.get().getAnalysisParameters().getSpecificCoverageSet() != null);
        specificCoverageBox.setManaged(analysis.get().getAnalysisParameters().getSpecificCoverageSet() != null);

        if (analysis.get().getAnalysisParameters().getSpecificCoverageSet() == null || analysis.get().getSpecificCoverageRegions() == null) {
            specificCoverageTable.getItems().clear();
            treeTableView.setRoot(null);
        } else {
            specificCoverageTable.getItems().setAll(analysis.get().getSpecificCoverageRegions());
            fillSpecificCoverageTreeTable(analysis.get().getSpecificCoverageRegions());
        }
    }
}
