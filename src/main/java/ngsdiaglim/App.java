package ngsdiaglim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
//        AnchorPane root = FXMLLoader.load(HelloFXML.class.getResource("hello.fxml"),
//                ResourceBundle.getBundle("hellofx.hello"));
        VBox root = new VBox();
        Label label = new Label("HELLOW WORLD !");
        Button button = new Button("Click me !");
        button.setOnAction(e -> {
            label.setText("Java version " + System.getProperty("java.version") + " with javafx " + System.getProperty("javafx.version"));
        });
        root.getChildren().addAll(button, label);
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
