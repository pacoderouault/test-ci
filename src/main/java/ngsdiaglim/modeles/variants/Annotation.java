package ngsdiaglim.modeles.variants;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import ngsdiaglim.enumerations.SangerState;
import ngsdiaglim.enumerations.Zygotie;
import ngsdiaglim.modeles.analyse.AnnotationSangerCheck;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.variants.predictions.GnomADFrequencies;

import java.util.*;

public class Annotation {

    private final Variant variant;
    private SimpleIntegerProperty depth;
    private SimpleIntegerProperty alleleDepth;
    private SimpleIntegerProperty referenceDepth;
    private SimpleStringProperty allelesDepth;
    private SimpleStringProperty allelesStrandDepth;
    private SimpleIntegerProperty alleleForwardCount;
    private SimpleIntegerProperty alleleReverseCount;
    private SimpleFloatProperty vaf;
    private final LinkedHashMap<String, TranscriptConsequence> transcriptConsequences = new LinkedHashMap<>();
    private final SimpleObjectProperty<TranscriptConsequence> transcriptConsequence = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Gene> gene = new SimpleObjectProperty<>();
    private final ObservableSet<String> geneNameSet = FXCollections.observableSet();
    private SimpleStringProperty geneNames;
    private SimpleObjectProperty<AnnotationSangerCheck> sangerState;
    private final GnomADFrequencies gnomADFrequencies;
    private final SimpleBooleanProperty reported = new SimpleBooleanProperty(false);

    public Annotation(Variant variant) {
        this.variant = variant;
        gnomADFrequencies = new GnomADFrequencies(this);
        geneNameSet.addListener((SetChangeListener<String>) change -> {
            StringJoiner sj = new StringJoiner(";");
            for (String gene : geneNameSet) {
                sj.add(gene);
            }
            geneNamesProperty().set(sj.toString());
        });
    }

    public Variant getVariant() {return variant;}

    public Integer getDepth() {
        if (depth == null) return null;
        return depth.get();
    }

    public SimpleIntegerProperty depthProperty() {
        if (depth == null) depth = new SimpleIntegerProperty();
        return depth;
    }

    public void setDepth(int depth) {
        depthProperty().set(depth);
    }

    public Integer getAlleleDepth() {
        if (alleleDepth == null) return null;
        return alleleDepth.get();
    }

    public SimpleIntegerProperty alleleDepthProperty() {
        if (alleleDepth == null) alleleDepth = new SimpleIntegerProperty();
        return alleleDepth;
    }

    public void setAlleleDepth(int alleleDepth) {
        alleleDepthProperty().set(alleleDepth);
    }


    public Integer getReferenceDepth() {
        if (referenceDepth == null) return null;
        return referenceDepth.get();
    }

    public SimpleIntegerProperty referenceDepthProperty() {
        if (referenceDepth == null) referenceDepth = new SimpleIntegerProperty();
        return referenceDepth;
    }

    public void setReferenceDepth(int referenceDepth) {
        referenceDepthProperty().set(referenceDepth);
    }

    public String getAllelesDepth() {
        if (allelesDepth == null) return null;
        return allelesDepth.get();
    }

    public SimpleStringProperty allelesDepthProperty() {
        if (allelesDepth == null && alleleDepth != null && referenceDepth != null) {
            allelesDepth = new SimpleStringProperty(getReferenceDepth() + "/" + getAlleleDepth());
        }
        return allelesDepth;
    }


    public Integer getAlleleForwardCount() {
        if (alleleForwardCount == null) return null;
        return alleleForwardCount.get();
    }

    public SimpleIntegerProperty alleleForwardCountProperty() {
        if (alleleForwardCount == null) alleleForwardCount = new SimpleIntegerProperty();
        return alleleForwardCount;
    }

    public void setAlleleForwardCount(int alleleForwardCount) {
        alleleForwardCountProperty().set(alleleForwardCount);
    }

    public Integer getAlleleReverseCount() {
        if (alleleReverseCount == null) return null;
        return alleleReverseCount.get();
    }

    public SimpleIntegerProperty alleleReverseCountProperty() {
        if (alleleReverseCount == null) alleleReverseCount = new SimpleIntegerProperty();
        return alleleReverseCount;
    }

    public void setAlleleReverseCount(int alleleReverseCount) {
        alleleReverseCountProperty().set(alleleReverseCount);
    }

    public SimpleStringProperty allelesStrandDepthProperty() {
        if (allelesStrandDepth == null && alleleForwardCount != null && alleleReverseCount != null) {
            allelesStrandDepth = new SimpleStringProperty(getAlleleForwardCount() + "/" + getAlleleReverseCount());
        }
        else {
            allelesStrandDepth = new SimpleStringProperty();
        }
        return allelesStrandDepth;
    }

    public Float getVaf() {
        if (vaf == null) return null;
        return vaf.get();
    }

    public SimpleFloatProperty vafProperty() {
        if (vaf == null) vaf = new SimpleFloatProperty();
        return vaf;
    }

    public void setVaf(float vaf) {
        vafProperty().set(vaf);
    }

    public void addTranscriptConsequence(TranscriptConsequence transcriptConsequence) {
        transcriptConsequences.putIfAbsent(transcriptConsequence.getTranscript().getNameWithoutVersion(), transcriptConsequence);
    }

    public LinkedHashMap<String, TranscriptConsequence> getTranscriptConsequences() {
        return transcriptConsequences;
    }

    public AnnotationSangerCheck getSangerState() {
        if (sangerState == null) return null;
        return sangerState.get();
    }

    public SimpleObjectProperty<AnnotationSangerCheck> sangerStateProperty() {
        if (sangerState == null) sangerState = new SimpleObjectProperty<>();
        return sangerState;
    }

    public void setSangerState(AnnotationSangerCheck sangerState) {
        sangerStateProperty().set(sangerState);
    }

    public Gene getGene() {
        return gene.get();
    }

    public SimpleObjectProperty<Gene> geneProperty() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene.set(gene);
    }

    public ObservableSet<String> getGeneNameSet() {return geneNameSet;}

    public void setGeneNames(Collection<String> geneNames) {
        this.geneNameSet.addAll(geneNames);
    }

    public String getGeneNames() {
        if (geneNames == null) return null;
        return geneNames.get();
    }

    public SimpleStringProperty geneNamesProperty() {
        if (geneNames == null) geneNames = new SimpleStringProperty();
        return geneNames;
    }

    public TranscriptConsequence getTranscriptConsequence() {
        return transcriptConsequence.get();
    }

    public SimpleObjectProperty<TranscriptConsequence> transcriptConsequenceProperty() {
        return transcriptConsequence;
    }

    public void setTranscriptConsequence(TranscriptConsequence transcriptConsequence) {
        this.transcriptConsequence.set(transcriptConsequence);
    }

    public GnomADFrequencies getGnomADFrequencies() {return gnomADFrequencies;}

    public boolean isReported() {
        return reported.get();
    }

    public SimpleBooleanProperty reportedProperty() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported.set(reported);
    }

    public Zygotie getZygotie() {
        if (getVaf() != null && getVaf() < 0.8) {
            return Zygotie.HETEROZYGOUS;
        } else {
            return Zygotie.HOMOZYGOUS;
        }
    }


    @Override
    public String toString() {

        if(getTranscriptConsequence() != null) {
            StringBuilder sb = new StringBuilder();
            if (getGene() != null) {
                sb.append("(").append(getGene().getGeneName()).append(") ");
            } else if (getTranscriptConsequence().getGeneName() != null){
                sb.append("(").append(getTranscriptConsequence().getGeneName()).append(") ");
            }
            if (getTranscriptConsequence().getHgvsp() != null) {
                sb.append(getTranscriptConsequence().getHgvsp());
            } else if (getTranscriptConsequence().getHgvsc() != null) {
                sb.append(getTranscriptConsequence().getHgvsc());
            } else {
                sb.append(getVariant().toString());
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            if (getGene() != null) {
                sb.append("(").append(getGene().getGeneName()).append(") ");
            }
            sb.append(getVariant().toString());
            return sb.toString();
        }
    }
}
