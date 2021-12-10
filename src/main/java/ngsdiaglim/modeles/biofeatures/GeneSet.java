package ngsdiaglim.modeles.biofeatures;

import ngsdiaglim.database.DAOController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GeneSet {

    private final static Logger logger = LogManager.getLogger(GeneSet.class);

    private final long id;
    private String name;
    private final HashMap<String, Gene> genes = new HashMap<>();
    private final HashMap<String, Transcript> transcripts = new HashMap<>();
    private boolean active;

    public GeneSet(long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public long getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Gene> getGenes() {return genes;}

    public void addGene(Gene gene) {
        this.genes.putIfAbsent(gene.getGeneName().toUpperCase(), gene);
        for (Transcript t : gene.getTranscripts().values()) {
            transcripts.put(t.getNameWithoutVersion(), t);
        }
    }

    public boolean isActive() {return active;}

    public void setActive(boolean active) {
        this.active = active;
    }

    public Gene getGene(String geneName) {
        return genes.get(geneName.toUpperCase());
    }

    public Transcript getTranscript(String transcriptName) {
        return transcripts.get(transcriptName);
    }

    public int getGenesCount() {
        try {
            return DAOController.getGeneDAO().getGeneCount(id);
        } catch (SQLException e) {
            logger.error("Error when getting gene count", e);
            return 0;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
