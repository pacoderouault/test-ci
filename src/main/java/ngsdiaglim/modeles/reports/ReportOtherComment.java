package ngsdiaglim.modeles.reports;

import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;

public class ReportOtherComment extends ReportCommentary {


    public ReportOtherComment(long id, ReportType reportType, String title, String comment) {
        super(id, reportType, title, comment);
    }
}
