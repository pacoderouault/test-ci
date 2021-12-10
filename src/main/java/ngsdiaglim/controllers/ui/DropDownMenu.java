package ngsdiaglim.controllers.ui;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;

/**
 * A drop down menu able to display custom node
 */
public class DropDownMenu extends MenuButton {

    private final AnchorPane contentPane = new AnchorPane();
    private Node contentNode;

    public DropDownMenu(String title) {
        super(title);
        getStyleClass().add("dropdown-menu");
        CustomMenuItem item = new CustomMenuItem(contentPane);
        item.setHideOnClick(false); // this will stop the ContextMenu from being hidden when clicking inside of it.
//        item.setMnemonicParsing(false);
//        item.setOnAction(Event::consume);
//        this.setMouseTransparent(true);
//        contentPane.setOnMouseMoved(e -> e.consume());
        this.setOnMouseMoved(Event::consume);
        this.setOnMouseDragEntered(Event::consume);
        getItems().add(item);
    }

    public void setContentNode(Node n) {
        contentNode = n;
        contentPane.getChildren().setAll(contentNode);


//        contentPane.setOnMouseMoved(e -> e.consume());
    }

    public Node getContentNode() {
        return contentNode;
    }

    public void setPrefSize(double width, double height) {
        contentPane.setPrefSize(width, height);
    }
}
