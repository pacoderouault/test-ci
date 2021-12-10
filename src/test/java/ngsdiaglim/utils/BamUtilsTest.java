package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BamUtilsTest {

    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void getBamSampleName() {
        File bamFile = Paths.get(resourcesDirectory.getPath(), "data/15B68a_subsamplePMP22.bam").toFile();
        assertEquals("M21.05_15B68a", BamUtils.getBamSampleName(bamFile));
    }

    @Test
    void isBamFile() {
        File bamFile = Paths.get(resourcesDirectory.getPath(), "data/15B68a_subsamplePMP22.bam").toFile();
        File noBamFile = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel.bed").toFile();
        assertTrue(BamUtils.isBamFile(bamFile));
        assertFalse(BamUtils.isBamFile(noBamFile));
    }
}