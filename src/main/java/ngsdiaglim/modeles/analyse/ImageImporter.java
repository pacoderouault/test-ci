package ngsdiaglim.modeles.analyse;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import ngsdiaglim.utils.ImageUtils;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageImporter {

    private final Analysis analysis;
    private final File imagesDirectory;

    public ImageImporter(Analysis analysis) {
        this.analysis = analysis;
        imagesDirectory = Paths.get(analysis.getDirectoryPath(), RunConstants.ANALYSIS_IMAGES_DIRNAME).toFile();
    }

    public File importImage(File imageFile) throws IOException {
        // create image directory if not exists
        File imagesDirectory = createImageDirectory();

        // copy image
        File targetImageFile = new File(imagesDirectory, imageFile.getName());
        if (targetImageFile.exists()) {

            // try to rename targetFile with unique name
            targetImageFile = renameFile(imagesDirectory, imageFile.getName());
            if (targetImageFile == null) {
                throw new IOException("Impossible to copy file : " + imageFile + ". File exists");
            }
        }
        FileUtils.copyFile(imageFile, targetImageFile);

        return targetImageFile;
    }

    public File importImage(Image image) throws IOException {
        // create image directory if not exists
        File imagesDirectory = createImageDirectory();

        // copy image
        File targetImageFile = new File(imagesDirectory, analysis.getSampleName() + ".png");
        if (targetImageFile.exists()) {

            // try to rename targetFile with unique name
            targetImageFile = renameFile(imagesDirectory, targetImageFile.getName());
            if (targetImageFile == null) {
                throw new IOException("Impossible to copy file. File exists");
            }
        }
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", targetImageFile);
        return targetImageFile;
    }

    private File createImageDirectory() throws IOException {
        if (!imagesDirectory.exists()) {
            Files.createDirectories(imagesDirectory.toPath());
        }
        return imagesDirectory;
    }

    private File renameFile(File targetDirectory, String filename) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String newFilename = i + "_" + filename;
            File newFile = new File(targetDirectory, newFilename);
            if (!newFile.exists()) {
                return newFile;
            }
        }
        return null;
    }

    public List<AdditionalImage> loadImages() throws IOException {
        File imagesDirectory = Paths.get(analysis.getDirectoryPath(), RunConstants.ANALYSIS_IMAGES_DIRNAME).toFile();
        if (imagesDirectory.exists()) {
            return Files.list(imagesDirectory.toPath()).map(Path::toFile).filter(ImageUtils::isImage).map(AdditionalImage::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    public void removeImage(File imageFile) throws IOException {
        if (imageFile.exists()) {
            Files.delete(imageFile.toPath());
        }
    }
}
