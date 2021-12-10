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

public class ReportData {

    private final Run run;
    private final Analysis analysis;

    private final Gender gender;
    private final String firstName;
    private final String lastName;
    private final String maidenName;
    private final boolean isChild;
    private final String barcode;
    private final LocalDate birthdate;
    private final Prescriber prescriber;
    private final SamplingType samplingType;
    private final LocalDate samplingDate;
    private final LocalDate samplingArrivedDate;
    private final List<Gene> genesList;
    private final List<Annotation> reportedVariants;
    private final List<ReportCommentary> commentaries;

    public ReportData(Run run, Analysis analysis, Gender gender, String firstName, String lastName,
                      String maidenName, boolean isChild, String barcode, LocalDate birthdate,
                      Prescriber prescriber, SamplingType samplingType, LocalDate samplingDate,
                      LocalDate samplingArrivedDate, List<Gene> genesList, List<Annotation> reportedVariants,
                      List<ReportCommentary> commentaries) {
        this.run = run;
        this.analysis = analysis;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maidenName = maidenName;
        this.isChild = isChild;
        this.barcode = barcode;
        this.birthdate = birthdate;
        this.prescriber = prescriber;
        this.samplingType = samplingType;
        this.samplingDate = samplingDate;
        this.samplingArrivedDate = samplingArrivedDate;
        this.genesList = genesList;
        this.reportedVariants = reportedVariants;
        this.commentaries = commentaries;
    }

    public Run getRun() {return run;}

    public Analysis getAnalysis() {return analysis;}

    public Gender getGender() {return gender;}

    public String getFirstName() {return firstName;}

    public String getLastName() {return lastName;}

    public String getMaidenName() {return maidenName;}

    public boolean isChild() {return isChild;}

    public String getBarcode() {return barcode;}

    public LocalDate getBirthdate() {return birthdate;}

    public Prescriber getPrescriber() {return prescriber;}

    public SamplingType getSamplingType() {return samplingType;}

    public LocalDate getSamplingDate() {return samplingDate;}

    public LocalDate getSamplingArrivedDate() {return samplingArrivedDate;}

    public List<Gene> getGenesList() {return genesList;}

    public List<Annotation> getReportedVariants() {return reportedVariants;}

    public List<ReportCommentary> getCommentaries() {return commentaries;}
}
