package ngsdiaglim;

import com.dlsc.workbenchfx.Workbench;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ngsdiaglim.database.DatabaseConnection;
import ngsdiaglim.modeles.users.HashUtils;
import ngsdiaglim.modeles.users.PasswordAuthentication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {

        VBox root = new VBox();
        Label label = new Label("HELLOW WORLD !");
        Button button = new Button("Click me !");
        button.setOnAction(e -> {
            label.setText("Java version " + System.getProperty("java.version") + " with javafx " + System.getProperty("javafx.version"));
            for (int i = 0; i < 1000; i++) {
                try {
                    int t = 1 / 0;
                } catch (Exception ex) {
                    logger.error("erreur fatale", ex);
                }
            }
        });
        root.getChildren().addAll(button, label);
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();

        Workbench workbench = new Workbench();

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        String hash = passwordAuthentication.createHash("test");
        System.out.println(hash);

        String hash2 = passwordAuthentication.createHash("test");
        System.out.println(hash2);

        String hash3 = passwordAuthentication.createHash("testu");
        System.out.println(hash3);

//        System.out.println(passwordAuthentication.validatePassword("test", "test"));
        System.out.println(passwordAuthentication.validatePassword("test", "1000:0d14d233b0070430bdfaf3c45c638b082ceba14633729038:c9bb1b2c8f1e7f0dd0c952f9506dc057728b094b85247863"));
        System.out.println(passwordAuthentication.validatePassword("test", hash2));
        System.out.println(passwordAuthentication.validatePassword("test", hash3));


            DatabaseConnection dc = DatabaseConnection.instance();
//            dc.test();



    }

    public static void main(String[] args) {
        launch(args);
    }

}
