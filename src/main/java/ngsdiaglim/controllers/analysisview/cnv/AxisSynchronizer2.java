package ngsdiaglim.controllers.analysisview.cnv;

import de.gsi.chart.axes.Axis;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ngsdiaglim.utils.PlatformUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AxisSynchronizer2 {
    private boolean updating;
    private final ArrayList<Axis> axes = new ArrayList<>();
    private final ChangeListener<Number> upperBoundChangeListener = this::upperBoundChanged;
    private final ChangeListener<Number> lowerBoundChangeListener = this::lowerBoundChanged;
    private final SimpleBooleanProperty lowerChanged = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty upperChanged = new SimpleBooleanProperty(true);
    private Thread upperBoundChangeThread;
    private Thread lowerBoundChangeThread;

    public AxisSynchronizer2() {
        super();
    }

    public void add(Axis axis) {
        axes.add(axis);
        axis.maxProperty().addListener(upperBoundChangeListener);
        axis.minProperty().addListener(lowerBoundChangeListener);

    }

    private Axis findAxis(ObservableValue<? extends Number> property) {
        for (final Axis chart : axes) {
            if (property == chart.maxProperty()) {
                return chart;
            }
            if (property == chart.minProperty()) {
                return chart;
            }
        }
        return null;
    }

    private void lowerBoundChanged(ObservableValue<? extends Number> property, Number oldValue, Number newValue) {

        if (!updating) {
            final double value = newValue.doubleValue();
            if (Double.isNaN(value)) {
                return;
            }
            if (value == oldValue.doubleValue())
                return;
            updating = true;
            lowerChanged.set(false);
            final Axis sender = findAxis(property);
            if (sender == null) {
                updating = false;
                lowerChanged.set(true);
                return;
            }
            if (upperChanged.get()) {
                ChangeListener<Boolean> l = new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        System.out.println("UPPER DRAW");
                        final double tickUnit = sender.getTickUnit();
                        for (final Axis axis : axes) {
                            if (axis != sender) {
                                axis.setMin(value);
                                axis.setAutoRanging(false);
                            }
                            axis.setTickUnit(tickUnit);
                        }
                        updating = false;
                        lowerChanged.set(true);
                        upperChanged.removeListener(this);
                    }
                };
                upperChanged.addListener(l);
            } else {
                final double tickUnit = sender.getTickUnit();
                for (final Axis axis : axes) {
                    if (axis != sender) {
                        axis.setMin(value);
                        axis.setAutoRanging(false);
                    }
                    axis.setTickUnit(tickUnit);
                }
                updating = false;
                lowerChanged.set(true);
            }
        }
    }

    public void remove(Axis axis) {
        axes.remove(axis);
        axis.maxProperty().removeListener(upperBoundChangeListener);
        axis.minProperty().removeListener(lowerBoundChangeListener);
        axis.setAutoRanging(true);
    }

    private void upperBoundChanged(ObservableValue<? extends Number> property, Number oldValue, Number newValue) {
        if (!updating) {
            final double value = newValue.doubleValue();
            if (Double.isNaN(value)) {
                return;
            }
            if (value == oldValue.doubleValue())
                return;
            updating = true;
            upperChanged.set(false);
            final Axis sender = findAxis(property);
            if (sender == null) {
                updating = false;
                upperChanged.set(true);
                return;
            }
            if (lowerChanged.get()) {
                ChangeListener<Boolean> l = new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        System.out.println("UPPDER DRAW");
                        final double tickUnit = sender.getTickUnit();
                        for (final Axis axis : axes) {
                            if (axis != sender) {
                                axis.setAutoRanging(false);
                                axis.setMax(value);
                            }
                            axis.setTickUnit(tickUnit);
                        }
                        updating = false;
                        upperChanged.set(true);
                        lowerChanged.removeListener(this);
                    }
                };
                lowerChanged.addListener(l);
            } else {
                final double tickUnit = sender.getTickUnit();
                for (final Axis axis : axes) {
                    if (axis != sender) {
                        axis.setAutoRanging(false);
                        axis.setMax(value);
                    }
                    axis.setTickUnit(tickUnit);
                }
                updating = false;
                upperChanged.set(true);
            }
        }
    }


}
