package ngsdiaglim.modeles.analyse;

import ngsdiaglim.exceptions.DuplicateSampleInRun;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AnalysesInputDirParserTest {

    private static final File resourcesDirectory = new File("src/test/resources");
    private final File dataDir = Paths.get(resourcesDirectory.getPath(), "data/run/runtest").toFile();


    @Test
    void parseInputDir() throws DuplicateSampleInRun, IOException, SQLException {
        AnalysesInputDirParser parser = new AnalysesInputDirParser(null, dataDir);
        parser.parseInputDir();

        assertEquals(1, parser.getRunFiles().size());
        assertTrue(parser.getAnalysesFiles().containsKey("M21.05_15B68a"));
        assertNotNull(parser.getAnalysesFiles().get("M21.05_15B68a").getVcfFile());
        assertNotNull(parser.getAnalysesFiles().get("M21.05_15B68a").getBamFile());
        assertNotNull(parser.getAnalysesFiles().get("M21.05_15B68a").getDepthFile());
    }
}