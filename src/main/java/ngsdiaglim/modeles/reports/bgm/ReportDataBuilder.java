package ngsdiaglim.modeles.reports.bgm;

import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.enumerations.SamplingType;
import ngsdiaglim.modeles.Prescriber;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.variants.Annotation;

import java.time.LocalDate;
import java.util.List;

public class ReportDataBuilder {
    private Run run;
    private Analysis analysis;
    private Gender gender;
    private String firstName;
    private String lastName;
    private String maidenName;
    private boolean isChild;
    private String barcode;
    private LocalDate birthdate;
    private Prescriber prescriber;
    private SamplingType samplingType;
    private LocalDate samplingDate;
    private LocalDate samplingArrivedDate;
    private List<Gene> genesList;
    private List<Annotation> reportedVariants;
    private List<ReportCommentary> commentaries;

    public ReportDataBuilder setRun(Run run) {
        this.run = run;
        return this;
    }

    public ReportDataBuilder setAnalysis(Analysis analysis) {
        this.analysis = analysis;
        return this;
    }

    public ReportDataBuilder setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ReportDataBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ReportDataBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public ReportDataBuilder setMaidenName(String maidenName) {
        this.maidenName = maidenName;
        return this;
    }

    public ReportDataBuilder setIsChild(boolean isChild) {
        this.isChild = isChild;
        return this;
    }

    public ReportDataBuilder setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public ReportDataBuilder setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public ReportDataBuilder setPrescriber(Prescriber prescriber) {
        this.prescriber = prescriber;
        return this;
    }

    public ReportDataBuilder setSamplingType(SamplingType samplingType) {
        this.samplingType = samplingType;
        return this;
    }

    public ReportDataBuilder setSamplingDate(LocalDate samplingDate) {
        this.samplingDate = samplingDate;
        return this;
    }

    public ReportDataBuilder setSamplingArrivedDate(LocalDate samplingArrivedDate) {
        this.samplingArrivedDate = samplingArrivedDate;
        return this;
    }

    public ReportDataBuilder setGenesList(List<Gene> genesList) {
        this.genesList = genesList;
        return this;
    }

    public ReportDataBuilder setReportedVariants(List<Annotation> reportedVariants) {
        this.reportedVariants = reportedVariants;
        return this;
    }

    public ReportDataBuilder setCommentaries(List<ReportCommentary> commentaries) {
        this.commentaries = commentaries;
        return this;
    }

    public ReportData createReportData() {
        return new ReportData(run, analysis, gender, firstName, lastName, maidenName,
                isChild, barcode, birthdate, prescriber, samplingType, samplingDate,
                samplingArrivedDate, genesList, reportedVariants, commentaries);
    }
}