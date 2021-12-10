package ngsdiaglim.enumerations;

public enum VariantsTableTheme {

    THEME1("BGM"),
    THEME2("Anapath/Hémato");

    private final String themeName;

    VariantsTableTheme(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeName() {return themeName;}

    @Override
    public String toString() {
        return themeName;
    }

}
