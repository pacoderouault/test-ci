package ngsdiaglim.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import java.util.function.Function;

public class ScrollBarUtil {

    private final TableView<?> table;
    private final EventHandler<MouseEvent> dragHandler;
    private final EventHandler<ScrollEvent> scrollHandler;
    private final ChangeListener<Parent> parentListener;
    private final Timeline timeline = new Timeline();
    private final Orientation orientation;

    private final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
    private double[] pushes = {0};
    private final double[] derivatives = new double[frictions.length];
    private double[] lastVPos = {0};

    public ScrollBarUtil(TableView<?> table, Orientation orientation) {
        this.table = table;
        this.orientation = orientation;

        dragHandler = event -> timeline.stop();

        scrollHandler = event -> {
            ScrollBar scrollBar = getScrollbarComponent();
            if (scrollBar == null) {
                return;
            }
            scrollBar.setUnitIncrement(5);
            scrollBar.valueProperty().set(lastVPos[0]);
            if (event.getEventType() == ScrollEvent.SCROLL) {
                double direction = event.getDeltaY() > 0 ? -1 : 1;
                for (int i = 0; i < pushes.length; i++) {
                    derivatives[i] += direction * pushes[i];
                }
                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }

            }
            event.consume();
        };

        parentListener = (o,oldVal, newVal)->{
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                oldVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
            if (newVal != null) {
                newVal.addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                newVal.addEventHandler(ScrollEvent.ANY, scrollHandler);
            }
        };
    }

    private ScrollBar getScrollbarComponent() {
        Node n = table.lookup(".scroll-bar");
        if (n instanceof ScrollBar ) {
            ScrollBar bar = (ScrollBar) n;
            if (bar.getOrientation().equals(orientation)) {
                return bar;
            }
        }

        return null;
    }

    public void smoothScrollingTableView(double speed) {
        smoothScrollingTableView(speed, Bounds::getHeight);
    }

    public void smoothHScrollingTableView(double speed) {
        smoothScrollingTableView(speed, Bounds::getHeight);
    }

    private void smoothScrollingTableView(double speed, Function<Bounds, Double> sizeFunc) {
        ScrollBar scrollBar = getScrollbarComponent();
        if (scrollBar == null) {
            return;
        }
//        scrollBar.setUnitIncrement(5);
//        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        pushes = new double[]{speed};
//        final double[] derivatives = new double[frictions.length];
        lastVPos = new double[]{0};
//        Timeline timeline = new Timeline();


        if (scrollBar.getParent() != null) {
            scrollBar.getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollBar.getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollBar.parentProperty().addListener(parentListener);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3), (event) -> {
            for (int i = 0; i < derivatives.length; i++) {
                derivatives[i] *= frictions[i];
            }
            for (int i = 1; i < derivatives.length; i++) {
                derivatives[i] += derivatives[i - 1];
            }
            double dy = derivatives[derivatives.length - 1];
            double size = sizeFunc.apply(scrollBar.getLayoutBounds());
            scrollBar.valueProperty().set(Math.min(Math.max(scrollBar.getValue() + dy / size, 0), 1));
            lastVPos[0] = scrollBar.getValue();
            if (Math.abs(dy) < 1) {
                if (Math.abs(dy) < 0.001) {
                    timeline.stop();
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }


    public void resetScrollBar() {
        ScrollBar scrollBar = getScrollbarComponent();
        if (scrollBar == null) {
            return;
        }
        if (scrollBar.getParent() != null) {
            scrollBar.getParent().removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollBar.getParent().removeEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollBar.parentProperty().addListener((o,oldVal, newVal)->{
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                oldVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
            if (newVal != null) {
                newVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                newVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
        });
    }
}


