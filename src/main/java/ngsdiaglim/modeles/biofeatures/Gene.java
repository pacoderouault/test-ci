package ngsdiaglim.modeles.biofeatures;

import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;

public class Gene {

    private long id;
    private long geneSetId;
    private final String geneName;
    private final HashMap<String, Transcript> transcripts = new HashMap<>();
    private final SimpleObjectProperty<Transcript> transcriptPreferred = new SimpleObjectProperty<>();

    public Gene(String geneName) {
        this.geneName = geneName.toUpperCase();
    }

    public Gene(long id, long geneSetId, String geneName) {
        this.geneName = geneName;
        this.id = id;
        this.geneSetId = geneSetId;
    }

    public long getId() {return id;}

    public void setId(long id) {
        this.id = id;
    }

    public long getGeneSetId() {return geneSetId;}

    public String getGeneName() {return geneName;}

    public HashMap<String, Transcript> getTranscripts() {return transcripts;}

    public String getTranscriptsAsString() {
        StringJoiner joiner = new StringJoiner(";");
        for (Transcript t : transcripts.values()) {
            joiner.add(t.getName());
        }
        return joiner.toString();
    }

    public boolean hasTranscript(String transcriptName) {
        return transcripts.containsKey(transcriptName.toUpperCase());
    }

    public boolean hasTranscriptWithoutVersion(String transcriptName) {
        return transcripts.containsKey(Transcript.getNameWithoutVersion(transcriptName.toUpperCase()));
    }

    public Transcript getTranscript(String transcriptName) {
        return transcripts.getOrDefault(transcriptName.toUpperCase(), null);
    }

    public void setTranscripts(HashMap<String, Transcript> transcripts) {
        this.transcripts.putAll(transcripts);
    }

    public Transcript getTranscriptWithoutVersion(String transcriptName) {
        return transcripts.getOrDefault(Transcript.getNameWithoutVersion(transcriptName.toUpperCase()), null);
    }

    public Transcript getTranscriptPreferred() {
        return transcriptPreferred.get();
    }

    public SimpleObjectProperty<Transcript> transcriptPreferredProperty() {
        return transcriptPreferred;
    }

    public void setTranscriptPreferred(Transcript transcriptPreferred) {
        this.transcriptPreferred.set(transcriptPreferred);
    }

    public boolean isPreferredTranscript(Transcript t) {
        if (transcriptPreferred.get() == null) return false;
        else {
            return transcriptPreferred.get().equals(t);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gene gene = (Gene) o;

        return geneName.equals(gene.geneName);
    }

    @Override
    public int hashCode() {
        return geneName.hashCode();
    }

    @Override
    public String toString() {
        return geneName;
    }
}
