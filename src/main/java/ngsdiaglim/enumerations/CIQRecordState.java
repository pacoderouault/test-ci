package ngsdiaglim.enumerations;

import ngsdiaglim.App;

public enum CIQRecordState {
    UNKNOWN(App.getBundle().getString("ciq.unknown")),
    ACCEPTED(App.getBundle().getString("ciq.accepted")),
    NOT_ACCEPTED(App.getBundle().getString("ciq.notaccepted"));

    private final String text;

    CIQRecordState(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
