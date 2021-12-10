package ngsdiaglim.modeles.parsers;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.GeneDAO;
import ngsdiaglim.database.dao.GeneSetDAO;
import ngsdiaglim.database.dao.TranscriptsDAO;
import ngsdiaglim.exceptions.MalformedGeneTranscriptFile;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.biofeatures.Transcript;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeneSetParserTest extends BaseSetup {

    private static final GeneSetDAO geneSetDAO = new GeneSetDAO();
    private static final GeneDAO geneDAO = new GeneDAO();
    private static final TranscriptsDAO transcriptsDAO = new TranscriptsDAO();

    @TempDir
    File tempDir;
    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void parsegeneSetFile() {
        File geneSetFile = Paths.get(resourcesDirectory.getPath(), "data/liste_transcripts_avec_alternatifs.tsv").toFile();
        try {
            HashSet<Gene> geneSet = GeneSetParser.parseGeneSet(geneSetFile);
            assertEquals(168, geneSet.size());

            Gene pmp22 = new Gene("PMP22");

            assertTrue(geneSet.contains(pmp22));
            Optional<Gene> opt = geneSet.stream().filter(g -> g.equals(pmp22)).findAny();
            assertTrue(opt.isPresent());
            assertTrue(opt.get().getTranscripts().containsKey("NM_000304"));
        } catch (IOException | MalformedGeneTranscriptFile e) {
            fail();
        }
    }

    @Test
    void existsTest() {
        try {
            geneSetDAO.addGeneSet("geneSetTest");
            assertFalse(geneSetDAO.geneSetExists("noEntry"));
            assertTrue(geneSetDAO.geneSetExists("geneSetTest"));
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    void addGeneToDatabaseTest() {

        File geneSetFile = Paths.get(resourcesDirectory.getPath(), "data/liste_transcripts_avec_alternatifs.tsv").toFile();
        try {
            HashSet<Gene> genes = GeneSetParser.parseGeneSet(geneSetFile);
            long geneSetId = geneSetDAO.addGeneSet("GeneSet2");
            for (Gene gene : genes) {
                long geneId = geneDAO.addGene(gene, geneSetId);
                gene.setId(geneId);
                for (Transcript transcript : gene.getTranscripts().values()) {
                    long transcriptId = transcriptsDAO.addTranscript(transcript.getName(), gene.getId());
                    transcript.setId(transcriptId);
                }
                // if only one transcript for the gene, set it as "preferred transcript"
                if (gene.getTranscripts().size() == 1) {
                    Optional<Transcript> opt = gene.getTranscripts().values().stream().findAny();
                    if(opt.isPresent()) {
                        geneDAO.setPreferredTranscript(gene.getId(), opt.get().getId());
                    }
                }
            }

            GeneSet geneSet = geneSetDAO.getGeneSet(geneSetId);
            assertNotNull(geneSet);
            assertEquals(168, geneSet.getGenesCount());
            assertNotNull(geneSet.getGene("pmp22"));
            assertEquals(geneSet.getGene("NEFL").getTranscriptPreferred(), new Transcript("NM_006158"));
        } catch (IOException | MalformedGeneTranscriptFile | SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}