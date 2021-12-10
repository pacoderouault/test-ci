package ngsdiaglim.modeles.reports;

import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;

public class ReportGeneCommentary extends ReportCommentary {

    private final SimpleStringProperty geneName = new SimpleStringProperty();

    public ReportGeneCommentary(long id, ReportType reportType, String geneName, String title, String comment) {
        super(id, reportType, title, comment);
        this.geneName.set(geneName);
    }

    public ReportGeneCommentary(ReportType reportType, String geneName, String title, String comment) {
        super(reportType, title, comment);
        this.geneName.set(geneName);
    }

    public String getGeneName() {
        return geneName.get();
    }

    public SimpleStringProperty geneNameProperty() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName.set(geneName);
    }
}
