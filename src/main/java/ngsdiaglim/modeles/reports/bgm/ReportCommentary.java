package ngsdiaglim.modeles.reports.bgm;

import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.ReportType;

public class ReportCommentary {

    private long id;
    private final ReportType reportType;
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty comment = new SimpleStringProperty();

    public ReportCommentary(long id, ReportType reportType, String title, String comment) {
        this(reportType, title, comment);
        this.id = id;
    }

    public ReportCommentary(ReportType reportType, String title, String comment) {

        this.reportType = reportType;
        this.comment.set(comment);
        this.title.set(title);
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public ReportType getReportType() {return reportType;}

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getComment() {
        return comment.get();
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }
}
