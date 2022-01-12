package ngsdiaglim.enumerations;

import ngsdiaglim.App;

public enum FalsePositiveVariantEnum {

    FP(App.getBundle().getString("editfalsepositivedialog.lb.fp"), true),
    VP(App.getBundle().getString("editfalsepositivedialog.lb.vp"), false);

    private final String valueName;
    private final boolean value;

    FalsePositiveVariantEnum(String valueName, boolean value) {
        this.valueName = valueName;
        this.value = value;
    }

    public String getValueName() {return valueName;}

    public boolean getValue() {return value;}

    @Override
    public String toString() {
        return valueName;
    }
}
