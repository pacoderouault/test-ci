package ngsdiaglim.enumerations;

import ngsdiaglim.App;

public enum CNVControlType {

    NONE(App.getBundle().getString("cnvcontroltype.none")),
    SAMPLES(App.getBundle().getString("cnvcontroltype.samples")),
    EXTERNAL(App.getBundle().getString("cnvcontroltype.external"));

    private final String typeName;

    CNVControlType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {return typeName;}

    @Override
    public String toString() {
        return typeName;
    }
}
