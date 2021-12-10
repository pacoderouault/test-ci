package ngsdiaglim.controllers;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Public domain. Use as you like. No warranties. P = Input parameter type.
 * Given to the closure as parameter. Return type is always Integer.
 * (cc) @imifos
 */
public class WorkIndicatorDialog<P> {

    private static final Logger logger = LogManager.getLogger(WorkIndicatorDialog.class);

    private Task<Object> animationWorker;
    private Task<Integer> taskWorker;

    private final ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
    private final Stage dialog = new Stage(StageStyle.UNDECORATED);
    private final Label label = new Label();
    private final Label labelProgress = new Label();
    private final Group root = new Group();
    private final Scene scene = new Scene(root, 330, 120, Color.WHITE);
    private final VBox vbox = new VBox();
    private final BorderPane mainPane = new BorderPane();

    /**
     * Placing a listener on this list allows to get notified BY the result when the
     * task has finished.
     */
    public ObservableList<Integer> resultNotificationList = FXCollections.observableArrayList();

    public Integer resultValue;
    public volatile String updateMessage = "";
    public volatile float currentProgress = -1;
    public volatile float maxProgress = 0;

    /**
     *
     */
    public WorkIndicatorDialog(Window owner, String label) {
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setResizable(false);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
        this.label.setText(label);
    }

    public ProgressIndicator getProgressIndicator() {return progressIndicator;}

    public float getCurrentProgress() {return currentProgress;}

    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;
    }

    public float getMaxProgress() {return maxProgress;}

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     *
     */
    public void addTaskEndNotification(Consumer<Integer> c) {
        resultNotificationList.addListener((ListChangeListener<? super Integer>) n -> {
            resultNotificationList.clear();
            c.accept(resultValue);
        });
    }

    /**
     *
     */
    public void exec(P parameter, ToIntFunction<Object> func) {
        setupDialog();
        setupAnimationThread();
        setupWorkerThread(parameter, func);
    }

    /**
     *
     */
    private void setupDialog() {
        vbox.getStyleClass().add("waitingdialog");
//        root.getChildren().add(stackPane);
//        stackPane.setMinSize(dialog.getWidth(), dialog.getHeight());
//        hbox.setAlignment(Pos.CENTER);
//        vbox.setAlignment(Pos.CENTER);
//        vbox.getChildren().add(dialogContainer);
//        hbox.getChildren().add(vbox);
//        stackPane.getChildren().add(hbox);
//
//        dialogContainer.getChildren().addAll(label, progressIndicator, labelProgress);
//
//        dialog.setScene(scene);
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
//
//        dialog.setOnHiding(event -> {
//            /*
//             * Gets notified when task ended, but BEFORE result value is attributed. Using
//             * the observable list above is recommended.
//             */ });
//
//        dialog.show();

        root.getChildren().add(mainPane);
//        vbox.setStyle("-fx-border-width: 1px; -fx-border-color: #00789c;");
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinSize(330, 120);
        vbox.getChildren().addAll(label,progressIndicator, labelProgress);
        mainPane.setTop(vbox);
        dialog.setScene(scene);

        dialog.setOnHiding(event -> { /* Gets notified when task ended, but BEFORE
                     result value is attributed. Using the observable list above is
                     recommended. */ });

        dialog.show();
    }

    /**
     *
     */
    private void setupAnimationThread() {

        animationWorker = new Task<>() {
            @Override
            protected Object call() throws Exception {

                // This is activated when we have a "progressing" indicator.
                // This thread is used to advance progress every XXX milliseconds.
                // In case of an INDETERMINATE_PROGRESS indicator, it's not of use.s

//				for (int i=1;i<=100;i++) {
//					Thread.sleep(500);
//					updateMessage(null);
//					updateProgress(i,100);
//				}

//                if(!progressIndicator.isIndeterminate()) {
//                    while (currentProgress < maxProgress) {
//                        Thread.sleep(200);
//                        updateMessage(null);
//                        updateProgress(currentProgress, maxProgress);
//                    }
//                } else {
                    while (currentProgress < maxProgress) {
                        Thread.sleep(200);
                        updateMessage(updateMessage);
                        updateProgress(currentProgress, maxProgress);
                    }
//                }
                return true;
            }
        };
        progressIndicator.setProgress(0);
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(animationWorker.progressProperty());

        animationWorker.messageProperty().addListener((observable, oldValue, newValue) -> {
            // Do something when the animation value ticker has changed
            labelProgress.setText(newValue);
        });

        new Thread(animationWorker).start();
    }

    /**
     *
     */
    private void setupWorkerThread(P parameter, ToIntFunction<Object> func) {

        taskWorker = new Task<>() {
            @Override
            public Integer call() {
                return func.applyAsInt(parameter);
            }
        };

        EventHandler<WorkerStateEvent> eh = event -> {
            animationWorker.cancel(true);
            progressIndicator.progressProperty().unbind();
            dialog.close();
            try {
                resultValue = taskWorker.get();
                resultNotificationList.add(resultValue);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        };

        taskWorker.setOnSucceeded(eh);
        taskWorker.setOnFailed(eh);

        new Thread(taskWorker).start();
    }

    /**
     * For those that like beans :)
     */
    public Integer getResultValue() {
        return resultValue;
    }

    public Task<Integer> getTask() {
        return taskWorker;
    }
}

