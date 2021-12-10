package ngsdiaglim.modeles.reports;

import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Variant;

public class ReportMutationCommentary extends ReportCommentary {

    private final Annotation annotation;

    public ReportMutationCommentary(long id, ReportType reportType, String title, String comment, Annotation annotation) {
        super(id, reportType, title, comment);
        this.annotation = annotation;
    }

    public ReportMutationCommentary(ReportType reportType, String title, String comment, Annotation annotation) {
        super(reportType, title, comment);
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {return annotation;}
}
