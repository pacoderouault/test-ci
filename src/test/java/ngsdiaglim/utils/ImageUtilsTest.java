package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilsTest {

    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void isImage() {
        File pngFile = Paths.get(resourcesDirectory.getPath(), "data/S5_59_R_2019_08_20_IonXpress_008_19-1237_IonXpress_009_19-1346_CNVs.png").toFile();
        File jpgFile = Paths.get(resourcesDirectory.getPath(), "data/image002.jpg").toFile();
        File txtFile = Paths.get(resourcesDirectory.getPath(), "data/genes.tsv").toFile();
        File noFile = Paths.get(resourcesDirectory.getPath(), "data/noFile").toFile();
        assertTrue(ImageUtils.isImage(pngFile));
        assertTrue(ImageUtils.isImage(jpgFile));
        assertFalse(ImageUtils.isImage(txtFile));
        assertFalse(ImageUtils.isImage(noFile));
    }
}