package ngsdiaglim.modeles.parsers;

import ngsdiaglim.modeles.biofeatures.Gene;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GenesPanelParserTest {

    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void parseGenes() {
        File geneFile = Paths.get(resourcesDirectory.getPath(), "data/genes.tsv").toFile();
        try {
            Set<Gene> genes = GenesPanelParser.parseGenes(geneFile);
            assertEquals(4, genes.size());
            assertTrue(genes.contains(new Gene("PMP22")));
            assertTrue(genes.contains(new Gene("KIF5A")));
            assertTrue(genes.contains(new Gene("GJB1")));
            assertTrue(genes.contains(new Gene("ATP7A")));
        } catch (IOException e) {
            fail(e);
        }
    }
}