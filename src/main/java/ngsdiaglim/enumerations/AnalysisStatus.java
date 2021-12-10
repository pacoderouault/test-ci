package ngsdiaglim.enumerations;

import ngsdiaglim.App;

public enum AnalysisStatus {

    INPROGRESS(App.getBundle().getString("analysisStatus.inprogress")),
    DONE(App.getBundle().getString("analysisStatus.done"));

    private final String value;

    AnalysisStatus(String value) {
        this.value = value;
    }

    public String getValue() {return value;}

    public String toString() {
        return value;
    }
}
