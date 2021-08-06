package ngsdiaglim.modeles.analyse;

import ngsdiaglim.enumerations.Genome;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AnalysisParameters {

    private final long id;
    private Genome genome;
    private String analysisName;
    private int warningDepth;
    private int minDepth;
    private float minVAF;
    private boolean isActive;
    private Panel panel;

    public AnalysisParameters(long id, Genome genome, String analysisName, int warningDepth, int minDepth, float minVAF, boolean isActive, Panel panel) {
        this.id = id;
        this.genome = genome;
        this.analysisName = analysisName;
        this.warningDepth = warningDepth;
        this.minDepth = minDepth;
        this.minVAF = minVAF;
        this.isActive = isActive;
        this.panel = panel;
    }

    public long getId() {return id;}

    public Genome getGenome() {return genome;}

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public String getAnalysisName() {return analysisName;}

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    public int getWarningDepth() {return warningDepth;}

    public void setWarningDepth(int warningDepth) {
        this.warningDepth = warningDepth;
    }

    public int getMinDepth() {return minDepth;}

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public float getMinVAF() {return minVAF;}

    public void setMinVAF(float minVAF) {
        this.minVAF = minVAF;
    }

    public boolean isActive() {return isActive;}

    public void setActive(boolean active) {
        isActive = active;
    }

    public Panel getPanel() {return panel;}

    public void setPanel(Panel panel) {
        this.panel = panel;
    }

    @Override
    public String toString() {
        return analysisName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisParameters that = (AnalysisParameters) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
