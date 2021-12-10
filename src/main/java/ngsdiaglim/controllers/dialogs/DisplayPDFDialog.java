package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import ngsdiaglim.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class DisplayPDFDialog extends DialogPane.Dialog<File> {

    private final Logger logger = LogManager.getLogger(AddUserDialog.class);

    private final ScrollPane pdfcontainerSp = new ScrollPane();

    public DisplayPDFDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);
//        setTitle(App.getBundle().getString("runinfodialog.title"));
        setContent(pdfcontainerSp);
        valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                initView();
            }
        });
    }

    private void initView() {
        setTitle(getValue().getName());
        try (PDDocument document = PDDocument.load(getValue())){
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage pageImage;
            pageImage = renderer.renderImage(0);
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(pageImage, null));
            pdfcontainerSp.setContent(imageView);
//            pdfcontainerSp.setVvalue(pdfcontainerSp.getVmax());
        } catch (IOException ex) {
            throw new UncheckedIOException("PDFRenderer throws IOException", ex);
        }
    }
}
