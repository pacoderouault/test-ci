package ngsdiaglim.controllers.cells;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ngsdiaglim.App;
import ngsdiaglim.modeles.Prescriber;

import java.util.Objects;

public class PrescriberStateCell <T> extends TableCell<T, Prescriber.PrescriberState> {

    private final ImageView imageview;

    public PrescriberStateCell() {
        imageview = new ImageView();
        imageview.setFitWidth(16);
        imageview.setFitHeight(16);
        imageview.setPreserveRatio(true);

    }

    @Override
    protected void updateItem(Prescriber.PrescriberState item, boolean empty) {
        super.updateItem(item, empty);
        getTableRow().getStyleClass().removeAll("tablerow-valid", "tablerow-warning", "tablerow-error");
        if (empty || item == null) {
            // set back to look of empty cell
            setGraphic(null);
            setText(null);
            imageview.setImage(null);
        } else {
            // set image for non-empty cell
            if (item.getState().equals(Prescriber.PrescriberState.State.WARNING)) {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/alert.png")));
                imageview.setImage(img);
                setGraphic(imageview);
                setText(item.getMessage());
                getTableRow().getStyleClass().add("tablerow-warning");
            }
            else if (item.getState().equals(Prescriber.PrescriberState.State.ERROR)) {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/erro.png")));
                imageview.setImage(img);
                setGraphic(imageview);
                setText(item.getMessage());
                getTableRow().getStyleClass().add("tablerow-error");
            }
            else {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/confirmar.png")));
                imageview.setImage(img);
                setGraphic(imageview);
                setText(null);
                getTableRow().getStyleClass().add("tablerow-valid");
            }
        }
    }
}
