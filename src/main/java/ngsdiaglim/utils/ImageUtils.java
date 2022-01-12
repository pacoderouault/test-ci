package ngsdiaglim.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    private ImageUtils() {}

    /**
     *
     * @return True if the file is an image
     */
    public static boolean isImage(File file) {
        boolean b = false;
        try {
            b = (ImageIO.read(file) != null);
        } catch (IOException ignored) {}
        return b;
    }

    public static void saveImageToFile(Image img, File outFile) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
        try {
            ImageIO.write(bImage, "png", outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
