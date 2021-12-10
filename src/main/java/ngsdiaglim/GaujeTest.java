package ngsdiaglim;

import java.util.*;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import ngsdiaglim.controllers.charts.PredictionGauge;
import ngsdiaglim.controllers.charts.PredictionGauge2;
import ngsdiaglim.controllers.charts.PredictionGauge3;
import ngsdiaglim.controllers.charts.PredictionGaugeLabel;
import ngsdiaglim.controllers.dialogs.Message;

/**
 * Example illustrating the use of a custom renderer to plot graphs with gaps
 *
 * @author akrimm
 */
public class GaujeTest extends Application {

    private static final int N_SAMPLES = 500; // default number of data points
    private static final int N_DATA_SETS_MAX = 3;

    @Override
    public void start(final Stage primaryStage) {
        CSSFX.start();
        try {
            VBox vbox = new VBox();
            vbox.getStyleClass().add("module-box");
            GridPane grid = new GridPane();
            grid.getStyleClass().add("module-box-container");
            TextField tf = new TextField();
            Button btn = new Button("go");
            List<PredictionGaugeLabel> siftLabels = new ArrayList<>();
            siftLabels.add(new PredictionGaugeLabel(0.05f, "D"));
            List<Double> siftStops = new ArrayList<>();
            siftStops.add(0.05d);
            siftStops.add(0.1d);
            siftStops.add(1d);
            PredictionGauge3 siftGauge = new PredictionGauge3(0, 1, true, siftStops, siftLabels);

            HBox box = new HBox();
            box.getChildren().addAll(tf, btn);
            grid.add(box, 0, 0);
            grid.add(siftGauge, 1, 0);

            vbox.getChildren().add(grid);

            final Scene scene = new Scene(vbox, 800, 600);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
            primaryStage.setTitle(getClass().getSimpleName());
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(evt -> Platform.exit());
            siftGauge.setScore(0.6f);
            btn.setOnAction(e -> {
                siftGauge.setScore(Float.valueOf(tf.getText()));
            });
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error("Impossible to load the Home page", e);
//            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }
}

