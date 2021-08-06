package ngsdiaglim;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class PreloaderSplash extends Preloader {

    private Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;

        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"), App.getBundle());
        Scene scene = new Scene(fxml.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/app.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            preloaderStage.hide();
        }
    }

    public void handleApplicationNotification(Preloader.PreloaderNotification info) {
        super.handleApplicationNotification(info);
    }
}
