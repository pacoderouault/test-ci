package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DisplayImageDialog extends DialogPane.Dialog<Image> {

    private final ScrollPane imageContainerSp = new ScrollPane();

    public DisplayImageDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);
        setContent(imageContainerSp);
        imageContainerSp.setPannable(true);
        valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                initView();
            }
        });
    }

    private void initView() {
        ImageView imageView = new ImageView(getValue());
        imageContainerSp.setContent(imageView);
    }
}
