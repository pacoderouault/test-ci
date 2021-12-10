package ngsdiaglim.modeles.variants;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.modeles.analyse.ExternalVariation;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.variants.predictions.DbscSNVPredictions;
import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import ngsdiaglim.utils.ProteinUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class TranscriptConsequence {


    private SimpleStringProperty geneName;
    private SimpleStringProperty proteinName;
    private SimpleStringProperty exon;
    private SimpleStringProperty intron;
    private SimpleStringProperty hgvsc;
    private SimpleStringProperty hgvsp;
    private SimpleStringProperty cDNAPosition;
    private SimpleStringProperty cdsPosition;
    private SimpleStringProperty proteinPosition;
    private SimpleStringProperty codons;
    private SimpleStringProperty aminoAcids;
    private SimpleStringProperty clinvarId;
    private SimpleStringProperty clinvarSign;

    private SimpleObjectProperty<Transcript> transcript;
    private SimpleObjectProperty<EnsemblConsequence> consequence;
    private SimpleObjectProperty<VariantPrediction> polyphen2HvarPred;
    private SimpleObjectProperty<VariantPrediction> polyphen2HdivPred;
    private SimpleObjectProperty<VariantPrediction> siftPred;
    private SimpleObjectProperty<VariantPrediction> caddRawPred;
    private SimpleObjectProperty<VariantPrediction> caddPhredPred;
    private SimpleObjectProperty<VariantPrediction> gerpPred;
    private SimpleObjectProperty<VariantPrediction> revelPred;
    private SimpleObjectProperty<VariantPrediction> mvpPred;
    private SimpleObjectProperty<VariantPrediction> phastCons100WayPred;
    private SimpleObjectProperty<VariantPrediction> phastCons30WayPred;
    private SimpleObjectProperty<VariantPrediction> phylop100WayPred;
    private SimpleObjectProperty<VariantPrediction> phylop30WayPred;
    private SimpleObjectProperty<VariantPrediction> siphyPred;
    private SimpleObjectProperty<VariantPrediction> fathmmPred;
    private SimpleObjectProperty<VariantPrediction> vest4Pred;
    private SimpleObjectProperty<VariantPrediction> mcapPred;
    private SimpleObjectProperty<VariantPrediction> metaLRPred;
    private SimpleObjectProperty<VariantPrediction> metaSVMPred;
    private SimpleObjectProperty<SpliceAIPredictions> spliceAIPreds;
    private SimpleObjectProperty<DbscSNVPredictions> dbscSNVPreds;

    private SimpleIntegerProperty distance;

    private SimpleListProperty<ExternalVariation> externalVariations;
    private final ObservableList<EnsemblConsequence> consequences = FXCollections.observableArrayList();
    private final ObservableList<String> clinvarSig = FXCollections.observableArrayList();
    private ObservableList<String> pubmedIds;

    private final Annotation annotation;

    public TranscriptConsequence(Annotation annotation) {
        this.annotation = annotation;
        clinvarSig.addListener((ListChangeListener<String>) c -> {
            StringJoiner sj = new StringJoiner(";");
            for (String clinvar : clinvarSig) {
                sj.add(clinvar);
            }
            clinvarSignProperty().set(sj.toString());
        });
    }

    public Annotation getAnnotation() {return annotation;}

    public Transcript getTranscript() {
        if (transcript == null) {
            return null;
        }
        return transcript.get();
    }

    public SimpleObjectProperty<Transcript> transcriptProperty() {
        if (transcript == null) {
            transcript = new SimpleObjectProperty<>();
        }
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        if (this.transcript == null) {
            this.transcript = new SimpleObjectProperty<>(transcript);
        }
        else {
            this.transcript.set(transcript);
        }
    }

    public EnsemblConsequence getConsequence() {
        if (consequence == null) {
            return null;
        }
        return consequence.get();
    }

    public SimpleObjectProperty<EnsemblConsequence> consequenceProperty() {
        if (consequence == null) {
            consequence = new SimpleObjectProperty<>();
        }
        return consequence;
    }

    public void setConsequence(EnsemblConsequence consequence) {
        if (this.consequence == null) {
            this.consequence = new SimpleObjectProperty<>(consequence);
        }
        else {
            this.consequence.set(consequence);
        }
    }

    public String getGeneName() {
        if (geneName == null) {
            return null;
        }
        return geneName.get();
    }

    public SimpleStringProperty geneNameProperty() {
        if (geneName == null) {
            geneName = new SimpleStringProperty();
        }
        return geneName;
    }

    public void setGeneName(String geneName) {
        if (this.geneName == null) {
            this.geneName = new SimpleStringProperty(geneName);
        }
        else {
            this.geneName.set(geneName);
        }
    }

    public String getProteinName() {
        if (proteinName == null) {
            return null;
        }
        return proteinName.get();
    }

    public SimpleStringProperty proteinNameProperty() {
        if (proteinName == null) {
            proteinName = new SimpleStringProperty();
        }
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        if (this.proteinName == null) {
            this.proteinName = new SimpleStringProperty(proteinName);
        }
        else {
            this.proteinName.set(proteinName);
        }
    }

    public String getExon() {
        if (exon == null) {
            return null;
        }
        return exon.get();
    }

    public SimpleStringProperty exonProperty() {
        if (exon == null) {
            exon = new SimpleStringProperty();
        }
        return exon;
    }

    public void setExon(String exon) {
        if (this.exon == null) {
            this.exon = new SimpleStringProperty(exon);
        }
        else {
            this.exon.set(exon);
        }
    }

    public String getIntron() {
        if (intron == null) {
            return null;
        }
        return intron.get();
    }

    public SimpleStringProperty intronProperty() {
        if (intron == null) intron = new SimpleStringProperty();
        return intron;
    }

    public void setIntron(String intron) {
        if (this.intron == null) this.intron = new SimpleStringProperty(intron);
        else this.intron.set(intron);
    }

    public String getHgvsc() {
        if (hgvsc == null) return null;
        return hgvsc.get();
    }

    public SimpleStringProperty hgvscProperty() {
        if (hgvsc == null) hgvsc = new SimpleStringProperty();
        return hgvsc;
    }

    public void setHgvsc(String hgvsc) {
        if (this.hgvsc == null) this.hgvsc = new SimpleStringProperty(hgvsc);
        else this.hgvsc.set(hgvsc);
    }

    public String getHgvscWithoutVersion() {
        if (hgvsc != null) {
            String[] hgvscTks = hgvsc.get().split(":");
            if (hgvscTks.length > 1) {
                String transcript = hgvscTks[0].split("\\.")[0];
                return transcript + ":" + hgvscTks[1];
            }
        }
        return null;
    }

    public String getCodingMutation() {
        if (hgvsc != null && hgvsc.get() != null && !hgvsc.get().isEmpty()) {
            String[] tokens = hgvsc.get().split(":");
            if (tokens.length > 1) {
                return tokens[1];
            }
        }
        return null;
    }

    public String getHgvsp() {
        if (hgvsp == null) return null;
        return hgvsp.get();
    }

    public SimpleStringProperty hgvspProperty() {
        if (hgvsp == null) hgvsp = new SimpleStringProperty();
        return hgvsp;
    }

    public void setHgvsp(String hgvsp) {
        if (this.hgvsp == null) this.hgvsp = new SimpleStringProperty(hgvsp);
        else this.hgvsp.set(hgvsp);
    }

    public String getProteinMutation() {
        if (hgvsp != null && hgvsp.get() != null && !hgvsp.get().isEmpty()) {
            String[] tokens = hgvsp.get().split(":");
            if (tokens.length > 1) {
                return tokens[1];
            }
        }
        return null;
    }

    public String getProteinMutationOneLetter() {
        if (hgvsp != null && hgvsp.get() != null && !StringUtils.isBlank(hgvsp.get())) {
            return ProteinUtils.mutationThreeToOne(getProteinMutation());
        }
        return null;
    }

    public String getcDNAPosition() {
        if (cDNAPosition == null) return null;
        return cDNAPosition.get();
    }

    public SimpleStringProperty cDNAPositionProperty() {
        if (cDNAPosition == null) cDNAPosition = new SimpleStringProperty();
        return cDNAPosition;
    }

    public void setcDNAPosition(String cDNAPosition) {
        if (this.cDNAPosition == null) this.cDNAPosition = new SimpleStringProperty(cDNAPosition);
        else this.cDNAPosition.set(cDNAPosition);
    }

    public String getCdsPosition() {
        if (cdsPosition == null) return null;
        return cdsPosition.get();
    }

    public SimpleStringProperty cdsPositionProperty() {
        if (cdsPosition == null) cdsPosition = new SimpleStringProperty();
        return cdsPosition;
    }

    public void setCdsPosition(String cdsPosition) {
        if (this.cdsPosition == null) this.cdsPosition = new SimpleStringProperty(cdsPosition);
        else this.cdsPosition.set(cdsPosition);
    }

    public String getProteinPosition() {
        if (proteinPosition == null) return null;
        return proteinPosition.get();
    }

    public SimpleStringProperty proteinPositionProperty() {
        if (proteinPosition == null) proteinPosition = new SimpleStringProperty();
        return proteinPosition;
    }

    public void setProteinPosition(String proteinPosition) {
        if (this.proteinPosition == null) this.proteinPosition = new SimpleStringProperty(proteinPosition);
        else this.proteinPosition.set(proteinPosition);
    }

    public String getCodons() {
        if (codons == null) return null;
        return codons.get();
    }

    public SimpleStringProperty codonsProperty() {
        if (codons == null) codons = new SimpleStringProperty();
        return codons;
    }

    public void setCodons(String codons) {
        if (this.codons == null) this.codons = new SimpleStringProperty(codons);
        else this.codons.set(codons);
    }

    public String getAminoAcids() {
        if (aminoAcids == null) return null;
        return aminoAcids.get();
    }

    public SimpleStringProperty aminoAcidsProperty() {
        if (aminoAcids == null) aminoAcids = new SimpleStringProperty();
        return aminoAcids;
    }

    public void setAminoAcids(String aminoAcids) {
        if (this.aminoAcids == null) this.aminoAcids = new SimpleStringProperty(aminoAcids);
        else this.aminoAcids.set(aminoAcids);
    }

    public String getClinvarId() {
        if (clinvarId == null) return null;
        return clinvarId.get();
    }

    public SimpleStringProperty clinvarIdProperty() {
        if (clinvarId == null) clinvarId = new SimpleStringProperty();
        return clinvarId;
    }

    public void setClinvarId(String clinvarId) {
        if (this.clinvarId == null) this.clinvarId = new SimpleStringProperty(clinvarId);
        else this.clinvarId.set(clinvarId);
    }

    public int getDistance() {
        if (distance == null) return 0;
        return distance.get();
    }

    public SimpleIntegerProperty distanceProperty() {
        return distance;
    }

    public void setDistance(int distance) {
        if (this.distance == null) this.distance = new SimpleIntegerProperty(distance);
        else this.distance.set(distance);
    }


    public ObservableList<ExternalVariation> getExternalVariations() {
        if (externalVariations == null) return null;
        return externalVariations.get();
    }

    public SimpleListProperty<ExternalVariation> externalVariationsProperty() {
        if (externalVariations == null) externalVariations = new SimpleListProperty<>();
        return externalVariations;
    }

    public void setExternalVariations(ObservableList<ExternalVariation> externalVariations) {
        externalVariationsProperty().set(externalVariations);
    }

    public List<ExternalVariation> getExternalVariations(ExternalVariation.ExternalVariationDb db) {
        if (externalVariations == null) return null;
        return externalVariations.stream().filter(p -> p.getDb().equals(db)).collect(Collectors.toList());
    }
    public ObservableList<String> getClinvarSig() {return clinvarSig;}

    public void setClinvarSig(Collection<String> clinvarSig) {
        this.clinvarSig.setAll(clinvarSig);
    }

    public ObservableList<EnsemblConsequence> getConsequences() {return consequences;}

    public void setConsequences(Collection<EnsemblConsequence> consequences) {
        this.consequences.setAll(consequences);
    }

    public void addConsequence(EnsemblConsequence consequence) {
        this.consequences.add(consequence);
    }

    public String getClinvarSign() {
        if (clinvarSign == null) return null;
        return clinvarSign.get();
    }

    public SimpleStringProperty clinvarSignProperty() {
        if (clinvarSign == null) clinvarSign = new SimpleStringProperty();
        return clinvarSign;
    }

    public ObservableList<String> getPubmedIds() {return pubmedIds;}

    public void setPubmedIds(ObservableList<String> pubmedIds) {
        this.pubmedIds = pubmedIds;
    }

    public VariantPrediction getPolyphen2HvarPred() {
        if (polyphen2HvarPred == null) return null;
        return polyphen2HvarPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> polyphen2HvarPredProperty() {
        if (polyphen2HvarPred == null) polyphen2HvarPred = new SimpleObjectProperty<>();
        return polyphen2HvarPred;
    }

    public void setPolyphen2HvarPred(VariantPrediction polyphen2HvarPred) {
        polyphen2HvarPredProperty().set(polyphen2HvarPred);
    }

    public VariantPrediction getPolyphen2HdivPred() {
        if (polyphen2HdivPred == null) return null;
        return polyphen2HdivPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> polyphen2HdivPredProperty() {
        if (polyphen2HdivPred == null) polyphen2HdivPred = new SimpleObjectProperty<>();
        return polyphen2HdivPred;
    }

    public void setPolyphen2HdivPred(VariantPrediction polyphen2HdivPred) {
        polyphen2HdivPredProperty().set(polyphen2HdivPred);
    }

    public VariantPrediction getSiftPred() {
        if (siftPred == null) return null;
        return siftPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> siftPredProperty() {
        if (siftPred == null) siftPred = new SimpleObjectProperty<>();
        return siftPred;
    }

    public void setSiftPred(VariantPrediction siftPred) {
        siftPredProperty().set(siftPred);
    }

    public VariantPrediction getCaddRawPred() {
        if (caddRawPred == null) return null;
        return caddRawPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> caddRawPredProperty() {
        if (caddRawPred == null) caddRawPred = new SimpleObjectProperty<>();
        return caddRawPred;
    }

    public void setCaddRawPred(VariantPrediction caddRawPred) {
        caddRawPredProperty().set(caddRawPred);
    }

    public VariantPrediction getCaddPhredPred() {
        if (caddPhredPred == null) return null;
        return caddPhredPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> caddPhredPredProperty() {
        if (caddPhredPred == null) caddPhredPred = new SimpleObjectProperty<>();
        return caddPhredPred;
    }

    public void setCaddPhredPred(VariantPrediction caddPhredPred) {
        caddPhredPredProperty().set(caddPhredPred);
    }

    public VariantPrediction getGerpPred() {
        if (gerpPred == null) return null;
        return gerpPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> gerpPredProperty() {
        if (gerpPred == null) gerpPred = new SimpleObjectProperty<>();
        return gerpPred;
    }

    public void setGerpPred(VariantPrediction gerpPred) {
        gerpPredProperty().set(gerpPred);
    }

    public VariantPrediction getRevelPred() {
        if (revelPred == null) return null;
        return revelPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> revelPredProperty() {
        if (revelPred == null) revelPred = new SimpleObjectProperty<>();
        return revelPred;
    }

    public void setRevelPred(VariantPrediction revelPred) {
        revelPredProperty().set(revelPred);
    }

    public VariantPrediction getMvpPred() {
        if (mvpPred == null) return null;
        return mvpPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> mvpPredProperty() {
        if (mvpPred == null) mvpPred = new SimpleObjectProperty<>();
        return mvpPred;
    }

    public void setMVPPred(VariantPrediction mvpPred) {
        mvpPredProperty().set(mvpPred);
    }

    public VariantPrediction getPhastCons100WayPred() {
        if(phastCons100WayPred == null) return null;
        else return phastCons100WayPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> phastCons100WayPredProperty() {
        if (phastCons100WayPred == null) phastCons100WayPred = new SimpleObjectProperty<>();
        return phastCons100WayPred;
    }

    public void setPhastCons100WayPred(VariantPrediction phastCons100WayPred) {
        phastCons100WayPredProperty().set(phastCons100WayPred);
    }

    public VariantPrediction getPhastCons30WayPred() {
        if (phastCons30WayPred == null) return null;
        else return phastCons30WayPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> phastCons30WayPredProperty() {
        if (phastCons30WayPred == null) phastCons30WayPred = new SimpleObjectProperty<>();
        return phastCons30WayPred;
    }

    public void setPhastCons30WayPred(VariantPrediction phastCons30WayPred) {
        phastCons30WayPredProperty().set(phastCons30WayPred);
    }

    public VariantPrediction getPhylop100WayPred() {
        if (phylop100WayPred == null) return null;
        return phylop100WayPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> phylop100WayPredProperty() {
        if (phylop100WayPred == null) phylop100WayPred = new SimpleObjectProperty<>();
        return phylop100WayPred;
    }

    public void setPhylop100WayPred(VariantPrediction phylop100WayPred) {
        phylop100WayPredProperty().set(phylop100WayPred);
    }

    public VariantPrediction getPhylop30WayPred() {
        if (phylop30WayPred == null) return null;
        return phylop30WayPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> phylop30WayPredProperty() {
        if (phylop30WayPred == null) phylop30WayPred = new SimpleObjectProperty<>();
        return phylop30WayPred;
    }

    public void setPhylop30WayPred(VariantPrediction phylop30WayPred) {
        phylop30WayPredProperty().set(phylop30WayPred);
    }

    public VariantPrediction getSiphyPred() {
        if (siphyPred == null) return null;
        return siphyPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> siphyPredProperty() {
        if (siphyPred == null) siphyPred = new SimpleObjectProperty<>();
        return siphyPred;
    }

    public void setSiphyPred(VariantPrediction siphyPred) {
        siphyPredProperty().set(siphyPred);
    }

    public VariantPrediction getFathmmPred() {
        if (fathmmPred == null) return null;
        return fathmmPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> fathmmPredProperty() {
        if (fathmmPred == null) fathmmPred = new SimpleObjectProperty<>();
        return fathmmPred;
    }

    public void setFathmmPred(VariantPrediction fathmmPred) {
        fathmmPredProperty().set(fathmmPred);
    }

    public VariantPrediction getVest4Pred() {
        if (vest4Pred == null) return null;
        return vest4Pred.get();
    }

    public SimpleObjectProperty<VariantPrediction> vest4PredProperty() {
        if (vest4Pred == null) vest4Pred = new SimpleObjectProperty<>();
        return vest4Pred;
    }

    public void setVest4Pred(VariantPrediction vest4Pred) {
        vest4PredProperty().set(vest4Pred);
    }

    public VariantPrediction getMcapPred() {
        if (mcapPred == null) return null;
        return mcapPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> mcapPredProperty() {
        if (mcapPred == null) mcapPred = new SimpleObjectProperty<>();
        return mcapPred;
    }

    public void setMcapPred(VariantPrediction mcapPred) {
        mcapPredProperty().set(mcapPred);
    }

    public VariantPrediction getMetaLRPred() {
        if (metaLRPred == null) return null;
        return metaLRPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> metaLRPredProperty() {
        if (metaLRPred == null) metaLRPred = new SimpleObjectProperty<>();
        return metaLRPred;
    }

    public void setMetaLRPred(VariantPrediction metaLRPred) {
        metaLRPredProperty().set(metaLRPred);
    }

    public VariantPrediction getMetaSVMPred() {
        if (metaSVMPred == null) return null;
        return metaSVMPred.get();
    }

    public SimpleObjectProperty<VariantPrediction> metaSVMPredProperty() {
        if (metaSVMPred == null) metaSVMPred = new SimpleObjectProperty<>();
        return metaSVMPred;
    }

    public void setMetaSVMPred(VariantPrediction metaSVMPred) {
        metaSVMPredProperty().set(metaSVMPred);
    }

    public DbscSNVPredictions getDbscSNVPreds() {
        if (dbscSNVPreds == null) return null;
        return dbscSNVPreds.get();
    }

    public SimpleObjectProperty<DbscSNVPredictions> dbscSNVPredsProperty() {
        if (dbscSNVPreds == null) dbscSNVPreds = new SimpleObjectProperty<>();
        return dbscSNVPreds;
    }

    public void setDbscSNVPreds(DbscSNVPredictions dbscSNVPreds) {
        dbscSNVPredsProperty().set(dbscSNVPreds);
    }

    public SpliceAIPredictions getSpliceAIPreds() {
        if (spliceAIPreds == null) return null;
        return spliceAIPreds.get();
    }

    public SimpleObjectProperty<SpliceAIPredictions> spliceAIPredsProperty() {
        if (spliceAIPreds == null) spliceAIPreds = new SimpleObjectProperty<>();
        return spliceAIPreds;
    }

    public void setspliceAIPreds(SpliceAIPredictions spliceAIPreds) {
        spliceAIPredsProperty().set(spliceAIPreds);
    }


    @Override
    public String toString() {
        return getTranscript().getName();
    }
}
