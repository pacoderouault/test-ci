package ngsdiaglim.modeles.analyse;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.modeles.TabixGetter;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;
import ngsdiaglim.modeles.parsers.CoverageFileParser;
import ngsdiaglim.modeles.variants.Annotation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

public class Analysis {

    private final long id;
    private String directoryPath;
    private final File vcfFile;
    private final File bamFile;
    private final File depthFile;
    private File coverageFile;
    private File specCoverageFile;
    private Run run;
    private LocalDateTime creationDate;
    private String creationUser;
    private String sampleName;
    private AnalysisParameters analysisParameters;
    private final SimpleStringProperty metadata = new SimpleStringProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<AnalysisStatus> status = new SimpleObjectProperty<>();
    public SimpleBooleanProperty importComplete = new SimpleBooleanProperty(false);
    private final ObservableList<Annotation> annotations = FXCollections.observableArrayList();
    private ObservableList<CoverageRegion> coverageRegions = null;
    private ObservableList<SpecificCoverageRegion> specCoverageRegions = null;
    private TabixGetter tabixGetter;

    public Analysis(long id, String name, String directoryPath, String vcfPath, String bamPath, String depthPath, String coveragePath, String specCoveragePath,
                    Run run, LocalDateTime creationDate, String creationUser, String sampleName, AnalysisParameters analysisParameters,
                    AnalysisStatus status, boolean importComplete, String metadata) {
        this.id = id;
        this.name.set(name);
        this.directoryPath = directoryPath;
        this.vcfFile = new File(vcfPath);
        this.bamFile = bamPath == null ? null : new File(bamPath);
        this.depthFile = depthPath == null ? null : new File(depthPath);
        this.coverageFile = coveragePath == null ? null : new File(coveragePath);
        this.specCoverageFile = specCoveragePath == null ? null : new File(specCoveragePath);
        this.run = run;
        this.creationDate = creationDate;
        this.creationUser = creationUser;
        this.sampleName = sampleName;
        this.analysisParameters = analysisParameters;
        this.status.set(status);
        this.importComplete.setValue(importComplete);
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

    public boolean isImportComplete() {
        return importComplete.get();
    }

    public SimpleBooleanProperty importCompleteProperty() {
        return importComplete;
    }

    public void setImportComplete(boolean importComplete) {
        this.importComplete.set(importComplete);
    }

    public File getVcfFile() {return vcfFile;}

    public File getBamFile() {return bamFile;}

    public File getDepthFile() {return depthFile;}

    public File getCoverageFile() {return coverageFile;}

    public void setCoverageFile(File coverageFile) {
        this.coverageFile = coverageFile;
    }

    public File getSpecCoverageFile() {return specCoverageFile;}

    public ObservableList<Annotation> getAnnotations() {return annotations;}

    public void setAnnotations(Collection<Annotation> annotations) {
        this.annotations.setAll(annotations);
    }

    public ObservableList<CoverageRegion> getCoverageRegions() {return coverageRegions;}

    public ObservableList<SpecificCoverageRegion> getSpecificCoverageRegions() {return specCoverageRegions;}


    public TabixGetter getTabixGetter() throws Exception {
        if (tabixGetter == null) {
            tabixGetter = new TabixGetter(this, this.getVcfFile());
        }
        return tabixGetter;
    }

    public void loadCoverage() throws IOException, MalformedCoverageFile {
        File coverageFile = getCoverageFile();
        if (coverageFile != null && coverageFile.exists()) {
            coverageRegions = CoverageFileParser.parseCoverageFile(coverageFile, getAnalysisParameters());
        }

        File specCoverageFile = getSpecCoverageFile();
        if (specCoverageFile != null && specCoverageFile.exists()) {
            specCoverageRegions = CoverageFileParser.parseSpecCoverageFile(specCoverageFile, getAnalysisParameters());
        }
    }

    public String getMetadata() {
        return metadata.get();
    }

    public SimpleStringProperty metadataProperty() {
        return metadata;
    }

    public HashMap<String, String> getmMetadataAsMap() {
        HashMap<String, String> map = new HashMap<>();
        for (String d : metadata.get().split(";")) {
            String[] tks = d.split(":");
            if (tks.length == 2) {
                map.put(tks[0], tks[1]);
            }
        }
        return map;
    }

    public void clear() {
        annotations.clear();
        if (coverageRegions != null) {
            coverageRegions.clear();
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
