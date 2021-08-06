package ngsdiaglim.modeles.users;

public enum DefaultPreferencesEnum {

    FULL_SCREEN("false"),
    INITIAL_DIR(".");

    private final String value;

    DefaultPreferencesEnum(String value) {
        this.value = value;
    }

    public String getValue() { return value; }
}
