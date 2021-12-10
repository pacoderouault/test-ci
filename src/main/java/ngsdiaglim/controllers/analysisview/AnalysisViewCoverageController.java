package ngsdiaglim.controllers.analysisview;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.coverageTableCells.CoverageActionTableCell;
import ngsdiaglim.controllers.cells.coverageTableCells.CoverageQualityTableCell;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.parsers.CoverageFileParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class AnalysisViewCoverageController extends HBox {

    private final Logger logger = LogManager.getLogger(AnalysisViewCoverageController.class);

    @FXML private TableView<CoverageRegion> coverageTable;
    @FXML private TableColumn<CoverageRegion, CoverageQuality> qualityCol;
    @FXML private TableColumn<CoverageRegion, String> contigCol;
    @FXML private TableColumn<CoverageRegion, Integer> startCol;
    @FXML private TableColumn<CoverageRegion, Integer> endCol;
    @FXML private TableColumn<CoverageRegion, Integer> sizeCol;
    @FXML private TableColumn<CoverageRegion, Double> depthCol;
    @FXML private TableColumn<CoverageRegion, String> genesCol;
    @FXML private TableColumn<CoverageRegion, Void> actionsCol;

    private final Analysis analysis;

    public AnalysisViewCoverageController(Analysis analysis) {
        this.analysis = analysis;
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
//        loadCoverage();
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
                analysis.getAnalysisParameters().getPanel().getRegions().stream()
                        .filter(r -> r.overlaps(data.getValue()))
                        .forEach(r -> sj.add(r.getName()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(sj.toString());
        });
        actionsCol.setCellFactory(data -> new CoverageActionTableCell());

        coverageTable.setItems(analysis.getCoverageRegions());
    }

//    private void loadCoverage() {
//        File coverageFile = analysis.getCoverageFile();
//        if (coverageFile != null && coverageFile.exists()) {
//            ObservableList<CoverageRegion> coverageRegions = null;
//            try {
//                coverageRegions = CoverageFileParser.parseCoverageFile(coverageFile, analysis.getAnalysisParameters());
//
//            } catch (IOException | MalformedCoverageFile e) {
//                logger.error(e);
//                Message.error(e.getMessage(), e);
//            }
//            coverageTable.setItems(coverageRegions);
//        }
//    }
}
