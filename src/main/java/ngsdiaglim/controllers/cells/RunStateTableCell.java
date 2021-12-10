package ngsdiaglim.controllers.cells;

import htsjdk.samtools.util.Tuple;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.SegmentedBar;

import java.sql.SQLException;
import java.util.List;

public class RunStateTableCell extends TableCell<Run, Void> {

    Logger logger = LogManager.getLogger(RunStateTableCell.class);

    private final SegmentedBar<AnalysisStatusSegment> statusbar = new SegmentedBar<>();
    private final Tooltip segmentTooltip = new Tooltip();
    private int analysesDoneCount = 0;

    public RunStateTableCell() {
        statusbar.setOrientation(Orientation.HORIZONTAL);
        statusbar.setSegmentViewFactory(AnalysisStatusSegmentView::new);
        statusbar.setInfoNodeFactory(null);
        statusbar.setTooltip(segmentTooltip);
        segmentTooltip.setShowDelay(Duration.ZERO);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if(empty) {
            setGraphic(null);
        }
        else {
            Run run = getTableRow().getItem();
            if (run != null) {
                statusbar.getSegments().setAll(createSegments(run));
            }
            Object[] arguments = {analysesDoneCount, statusbar.getTotal()};
            segmentTooltip.setText(BundleFormatter.format("analysisStatus.tp", arguments));
            setGraphic(statusbar);
        }
    }

    private ObservableList<AnalysisStatusSegment> createSegments(Run run) {
        ObservableList<AnalysisStatusSegment> segments = FXCollections.observableArrayList();
        int inProgressAnalysesCount = 0;
        int analysesDoneCount = 0;
        try {
            Tuple<Integer, Integer> analysisCount = DAOController.get().getAnalysisDAO().getAnalysisCount(run.getId());
            inProgressAnalysesCount = analysisCount.a;
            analysesDoneCount = analysisCount.b;
        } catch (SQLException e) {
            logger.error("Error when getting run analyses", e);
        }
        this.analysesDoneCount = analysesDoneCount;
        if (analysesDoneCount > 0) {
            segments.add(new AnalysisStatusSegment(analysesDoneCount, AnalysisStatus.DONE));
        }
        if (inProgressAnalysesCount > 0) {
            segments.add(new AnalysisStatusSegment(inProgressAnalysesCount, AnalysisStatus.INPROGRESS));
        }
        return segments;
    }


    public static class AnalysisStatusSegment extends SegmentedBar.Segment {

        private final AnalysisStatus status;

        public AnalysisStatusSegment(double value, AnalysisStatus status) {
            super(value);
            this.status = status;
        }

        public AnalysisStatus getStatus() {
            return status;
        }
    }

//    public enum AnalysisStatus {
//        DONE(App.getBundle().getString("analysisStatus.done")),
//        INPROGRESS(App.getBundle().getString("analysisStatus.inprogress")),
//        TODO(App.getBundle().getString("analysisStatus.todo"));
//
//        private final String value;
//
//        AnalysisStatus(String value) {
//            this.value = value;
//        }
//
//        public String getValue() {return value;}
//    }


    public static class AnalysisStatusSegmentView extends Region {

        public AnalysisStatusSegmentView(final AnalysisStatusSegment segment) {
            setPrefHeight(8);
            setPrefWidth(8);

            switch (segment.getStatus()) {
                case DONE:
                    setStyle("-fx-background-color: green;");
                    break;
                case INPROGRESS:
                    setStyle("-fx-background-color: orange;");
                    break;
            }
        }
    }
}
