package ngsdiaglim.modeles.analyse;

import javafx.scene.image.Image;

import java.io.File;

public class AdditionalImage {

    private final File imageFile;
    private final Image image;

    public AdditionalImage(File imageFile) {
        this.imageFile = imageFile;
        this.image = new Image(imageFile.toURI().toString());
    }

    public File getImageFile() {return imageFile;}

    public Image getImage() { return image; }
}

