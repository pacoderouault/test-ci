package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public abstract class ReportPane extends VBox {


    protected AnalysisViewReportBGMController reportController;

    public ReportPane(AnalysisViewReportBGMController analysisViewReportBGMController){
        this.reportController = analysisViewReportBGMController;
    }

    abstract String checkForm();

    public void setReportController(AnalysisViewReportBGMController reportController) {
        this.reportController = reportController;
    }

}
