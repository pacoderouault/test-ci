package ngsdiaglim.modeles.analyse;

import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.variants.HotspotsSet;

public class AnalysisParameters {

    private final long id;
    private final Genome genome;
    private final String analysisName;
    private final int warningDepth;
    private final int minDepth;
    private final float minVAF;
    private boolean isActive;
    private final Panel panel;
    private final GeneSet geneSet;
    private final HotspotsSet hotspotsSet;
    private final TargetEnrichment targetEnrichment;

    public AnalysisParameters(long id, Genome genome, String analysisName, int minDepth, int warningDepth,
                              float minVAF, boolean isActive, Panel panel, GeneSet geneSet,
                              HotspotsSet hotspotsSet, TargetEnrichment targetEnrichment) {
        this.id = id;
        this.genome = genome;
        this.analysisName = analysisName;
        this.warningDepth = warningDepth;
        this.minDepth = minDepth;
        this.minVAF = minVAF;
        this.isActive = isActive;
        this.panel = panel;
        this.geneSet = geneSet;
        this.hotspotsSet = hotspotsSet;
        this.targetEnrichment = targetEnrichment;
    }

    public long getId() {return id;}

    public Genome getGenome() {return genome;}

    public String getAnalysisName() {return analysisName;}

    public int getWarningDepth() {return warningDepth;}

    public int getMinDepth() {return minDepth;}

    public float getMinVAF() {return minVAF;}

    public boolean isActive() {return isActive;}

    public void setActive(boolean active) {
        isActive = active;
    }

    public Panel getPanel() {return panel;}

    public GeneSet getGeneSet() {return geneSet;}

    public HotspotsSet getHotspotsSet() {return hotspotsSet;}

    public TargetEnrichment getTargetEnrichment() {return targetEnrichment;}

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
