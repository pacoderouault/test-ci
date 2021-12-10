package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FilesUtilsTest {

    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void removeFileExtension() {
        String fileName = "test.txt.gz";
        assertEquals("test.txt", FilesUtils.removeFileExtension(fileName));
        assertEquals("test", FilesUtils.removeFileExtension(fileName, true));
    }

    @Test
    void convertAbsolutePathToRelative() {
        File testFile = Paths.get(resourcesDirectory.getPath(), "data/15B68a_subsamplePMP22.bam").toFile();
        try {
            assertEquals("src/test/resources/data/15B68a_subsamplePMP22.bam",
                    FilesUtils.convertAbsolutePathToRelative(testFile.getAbsoluteFile().toPath()).toString());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void computesSHA256() {
        File testFile = Paths.get(resourcesDirectory.getPath(), "data/15B68a_subsamplePMP22.bam").toFile();
        try {
            assertEquals("508e679dbf04dcaa3e4c97690fdc09f30eda39681a6c8e58ac88d4657f4942c0", FilesUtils.computesSHA256(testFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void compareFiles() {
        File file1 = Paths.get(resourcesDirectory.getPath(), "data/genes.tsv").toFile();
        File file2 = Paths.get(resourcesDirectory.getPath(), "data/genes_copy.tsv").toFile();
        File file3 = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel_PMP22.bed").toFile();
        File noFile = Paths.get(resourcesDirectory.getPath(), "data/noFile").toFile();
        try {
            assertTrue(FilesUtils.compareFiles(file1, file2));
            assertFalse(FilesUtils.compareFiles(file1, file3));
            assertFalse(FilesUtils.compareFiles(file1, noFile));
        } catch (IOException e) {
            fail(e);
        }

    }
}