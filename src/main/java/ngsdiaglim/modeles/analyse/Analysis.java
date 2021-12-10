package ngsdiaglim.modeles.analyse;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.modeles.TabixGetter;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.parsers.CoverageFileParser;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

public class Analysis {

    private static final Logger logger = LogManager.getLogger(Analysis.class);
    private final long id;
    private String directoryPath;
    private final File vcfFile;
    private final File bamFile;
    private final File depthFile;
    private File coverageFile;
    private Run run;
    private LocalDateTime creationDate;
    private String creationUser;
    private String sampleName;
    private AnalysisParameters analysisParameters;
    private final SimpleStringProperty metadata = new SimpleStringProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<AnalysisStatus> status = new SimpleObjectProperty<>();
    private final ObservableList<Annotation> annotations = FXCollections.observableArrayList();
    private ObservableList<CoverageRegion> coverageRegions = null;
    private TabixGetter tabixGetter;

    public Analysis(long id, String name, String directoryPath, String vcfPath, String bamPath, String depthPath, String coveragePath,
                    Run run, LocalDateTime creationDate, String creationUser, String sampleName, AnalysisParameters analysisParameters,
                    AnalysisStatus status, String metadata) {
        this.id = id;
        this.name.set(name);
        this.directoryPath = directoryPath;
        this.vcfFile = new File(vcfPath);
        this.bamFile = bamPath == null ? null : new File(bamPath);
        this.depthFile = depthPath == null ? null : new File(depthPath);
        this.coverageFile = coveragePath == null ? null : new File(coveragePath);
        this.run = run;
        this.creationDate = creationDate;
        this.creationUser = creationUser;
        this.sampleName = sampleName;
        this.analysisParameters = analysisParameters;
        this.status.set(status);
        this.metadata.set(metadata);
    }

    public long getId() {return id;}

    public String getDirectoryPath() {return directoryPath;}

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public Run getRun() {return run;}

    public void setRun(Run run) {
        this.run = run;
    }

    public LocalDateTime getCreationDate() {return creationDate;}

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationUser() {return creationUser;}

    public void setCreationUser(String creationUser) {
        this.creationUser = creationUser;
    }

    public String getSampleName() {return sampleName;}

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public AnalysisParameters getAnalysisParameters() {return analysisParameters;}

    public void setAnalysisParameters(AnalysisParameters analysisParameters) {
        this.analysisParameters = analysisParameters;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public AnalysisStatus getStatus() {
        return status.get();
    }

    public SimpleObjectProperty<AnalysisStatus> statusProperty() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status.set(status);
    }

    public File getVcfFile() {return vcfFile;}

    public File getBamFile() {return bamFile;}

    public File getDepthFile() {return depthFile;}

    public File getCoverageFile() {return coverageFile;}

    public void setCoverageFile(File coverageFile) {
        this.coverageFile = coverageFile;
    }

    public ObservableList<Annotation> getAnnotations() {return annotations;}

    public void setAnnotations(Collection<Annotation> annotations) {
        this.annotations.setAll(annotations);
    }

    public ObservableList<CoverageRegion> getCoverageRegions() {return coverageRegions;}

    public TabixGetter getTabixGetter() throws IOException {
        if (tabixGetter == null) {
            tabixGetter = new TabixGetter(this, this.getVcfFile());
        }
        return tabixGetter;
    }

    public void loadCoverage() throws IOException, MalformedCoverageFile {
        File coverageFile = getCoverageFile();
        if (coverageFile != null && coverageFile.exists()) {
//            try {
            coverageRegions = CoverageFileParser.parseCoverageFile(coverageFile, getAnalysisParameters());
//            } catch (IOException | MalformedCoverageFile e) {
//                logger.error(e);
//                Message.error(e.getMessage(), e);
//            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Analysis analysis = (Analysis) o;

        return id == analysis.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return getName() + " - " + getSampleName();
    }
}
