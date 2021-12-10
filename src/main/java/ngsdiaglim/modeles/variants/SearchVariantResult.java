package ngsdiaglim.modeles.variants;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.time.LocalDateTime;

public class SearchVariantResult {

    private final long analysisId;
    private final long runId;
    private final SimpleStringProperty analysisName = new SimpleStringProperty();
    private final SimpleStringProperty runName = new SimpleStringProperty();
    private final SimpleStringProperty sampleName = new SimpleStringProperty();
    private final File vcfFile;
    private final LocalDateTime analysisDate;
    private final SimpleIntegerProperty depth = new SimpleIntegerProperty();
    private final SimpleFloatProperty vaf = new SimpleFloatProperty();
    public SearchVariantResult(long analysisId, long runId, String analysisName, String runName, String sampleName, LocalDateTime analysisDate, File vcfFile) {
        this.analysisId = analysisId;
        this.runId = runId;
        this.analysisName.set(analysisName);
        this.runName.set(runName);
        this.sampleName.set(sampleName);
        this.analysisDate = analysisDate;
        this.vcfFile = vcfFile;
    }

    public long getAnalysisId() {return analysisId;}

    public long getRunId() {return runId;}

    public String getAnalysisName() {
        return analysisName.get();
    }

    public SimpleStringProperty analysisNameProperty() {
        return analysisName;
    }

    public String getRunName() {
        return runName.get();
    }

    public SimpleStringProperty runNameProperty() {
        return runName;
    }

    public String getSampleName() {
        return sampleName.get();
    }

    public SimpleStringProperty sampleNameProperty() {
        return sampleName;
    }

    public LocalDateTime getAnalysisDate() {return analysisDate;}

    public File getVcfFile() {return vcfFile;}

    public int getDepth() {
        return depth.get();
    }

    public SimpleIntegerProperty depthProperty() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth.set(depth);
    }

    public float getVaf() {
        return vaf.get();
    }

    public SimpleFloatProperty vafProperty() {
        return vaf;
    }

    public void setVaf(float vaf) {
        this.vaf.set(vaf);
    }
}
