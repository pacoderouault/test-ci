package ngsdiaglim.database;

public class Datasource {

    private final String JDBC_DRIVER;
    private final String JDBC_CONNECTION;
    private final String JDBC_PATH;
    private final String JDBC_FEATURES;
    private final String USER;
    private final String PASS;

    public Datasource(String JDBC_DRIVER, String JDBC_CONNECTION, String JDBC_PATH, String JDBC_FEATURES, String USER, String PASS) {
        this.JDBC_DRIVER = JDBC_DRIVER;
        this.JDBC_CONNECTION = JDBC_CONNECTION;
        this.JDBC_PATH = JDBC_PATH;
        this.JDBC_FEATURES = JDBC_FEATURES;
        this.USER = USER;
        this.PASS = PASS;
    }

    public String getJDBC_DRIVER() { return JDBC_DRIVER; }

    public String getJDBC_CONNECTION() { return JDBC_CONNECTION; }

    public String getJDBC_PATH() {return JDBC_PATH;}

    public String getJDBC_FEATURES() { return JDBC_FEATURES; }

    public String getUSER() { return USER; }

    public String getPASS() { return PASS; }


}
