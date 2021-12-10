package ngsdiaglim.enumerations;

import ngsdiaglim.App;

public enum SangerState {

    NONE, ON_PROGRESS, ON_DEMAND, COMPLETE_POSITIVE, COMPLETE_NEGATIVE;

    public String getName() {
        switch (this) {
            case ON_PROGRESS:
                return App.getBundle().getString("sangerstate.onProgress");
            case ON_DEMAND:
                return App.getBundle().getString("sangerstate.onDemand");
            case COMPLETE_POSITIVE:
                return App.getBundle().getString("sangerstate.completePositive");
            case COMPLETE_NEGATIVE:
                return App.getBundle().getString("sangerstate.completeNegative");
            default:
                return App.getBundle().getString("sangerstate.none");
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
