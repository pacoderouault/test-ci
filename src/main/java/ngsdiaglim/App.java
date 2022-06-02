package ngsdiaglim;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ngsdiaglim.controllers.AppController;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.DatabaseCreatorDAO;
import ngsdiaglim.enumerations.Service;
import ngsdiaglim.importer.Importer;
import ngsdiaglim.modeles.igv.IGVHandler;
import ngsdiaglim.modeles.igv.IGVLinks2;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;


public class App extends Application {

    private static App instance;
    private AppController appController;
    private Scene scene;
    private static final Logger logger = LogManager.getLogger(App.class);
    public static Locale locale = Locale.FRANCE;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("strings/NGSDiag", locale);
    private static Stage primaryStage;
    private final SimpleObjectProperty<User> loggedUser = new SimpleObjectProperty<>();
    private AppSettings appSettings;
//    public final IGVHandler igvHandler = new IGVHandler();

//    public IGVLinks igvLinks1;
    public IGVLinks2 igvLinks;
    private Service service;

    public App() {
        instance = this;
    }

    public void init() {

        instance = this;

        try {
            appSettings = new AppSettings();
        } catch (IOException e) {
            logger.error("Error when reader application properties file");
        }

        DatabaseCreatorDAO databaseCreatorDAO = new DatabaseCreatorDAO();
        if (!databaseCreatorDAO.exists()) {
            try {
                databaseCreatorDAO.createTables();
            } catch (SQLException e) {
                logger.fatal("Error when creating database", e);
                System.exit(1);
            }
        }
        try {
            databaseCreatorDAO.setupAdminPermissions();
        } catch (SQLException e) {
            logger.error("Error when setup admin roles", e);
        }
        try {
//            Importer.importBGM();
//            Importer.importHemato();
//            Importer.importAnapath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {

        CSSFX.start();
        primaryStage = stage;

        try {
            service = Service.valueOf(appSettings.getProperty(AppSettings.DefaultAppSettings.SERVICE.name()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            service = Service.UNKNOW;
        }

        loggedUser.addListener((obs, oldV, newV) -> {

            if (newV == null) {
                ModuleManager.clearModules();
                try {
                    loadLoggingScreen();
                } catch (IOException e) {
                    logger.fatal("Error when loadding logging view fxml");
                    Message.error(bundle.getString("app.msg.err.loadLoggingScreenErr"), e.getMessage());
                }
            } else {
                if (!newV.equals(oldV)) {
                    ModuleManager.clearModules();
                    try {
                        loadAppscreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.fatal("Error when loadding application view fxml");
                        Message.error(bundle.getString("app.msg.err.loadAppScreenErr"), e.getMessage());
                    }
                }
            }
        });

        try {
            loadLoggingScreen();
        } catch (IOException e) {
            logger.fatal(e);
            Message.error(bundle.getString("app.msg.err.loadLoggingScreenErr"), e.getMessage());
        }

        primaryStage.setMaximized(Boolean.parseBoolean(appSettings.getProperty(AppSettings.DefaultAppSettings.MAXIMIZED.name())));
        primaryStage.maximizedProperty().addListener((obs, oldV, newV) -> {
            appSettings.setValue(AppSettings.DefaultAppSettings.MAXIMIZED, newV);
        });

//        // Auto open admin account
//        try {
//            setLoggedUser(null);
//            setLoggedUser(DAOController.getUsersDAO().getUser("admin"));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//        igvLinks1 = new IGVLinks();
        igvLinks = new IGVLinks2();

        primaryStage.setOnCloseRequest((event) -> {// <----------- this is what you need
            Platform.exit();
        });

        System.out.println(getJarPath());
        logger.warn(App::getJarPath);
        System.out.println(getRunsDataPath());
        logger.warn(App::getRunsDataPath);
        System.out.println(getPanelsDataPath());
        logger.warn(App::getPanelsDataPath);
    }

//    public IGVHandler getIgvHandler() {return igvHandler;}

//    public IGVLinks getIgvLinks() {return igvLinks1;}
    public IGVLinks2 getIgvLinks2() {return igvLinks;}

    private void loadLoggingScreen() throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/Logging.fxml"));
        fxml.setResources(App.bundle);
        AnchorPane page = fxml.load();
        scene = new Scene(page);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void loadAppscreen() throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/App.fxml"));
        fxml.setResources(bundle);
        AnchorPane page = fxml.load();
        appController = fxml.getController();
        scene.setRoot(page);
        appController.showHomeView();
    }


    /**
     *
     * @return The App instance of this application
     */
    public static App get() {
        return instance;
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static Stage getPrimaryStage() {return primaryStage;}

    public AppSettings getAppSettings() {return appSettings;}

    public AppController getAppController() {return appController;}

    public User getLoggedUser() {
        return loggedUser.get();
    }

    public SimpleObjectProperty<User> loggedUserProperty() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser.set(loggedUser);
    }

    /**
     *
     * @return A string corresponding to the path of the executable jar file
     */
    public static String getJarPath() {
        CodeSource codeSource = App.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().getPath());
        String url;
        if (jarFile.isDirectory()) {  // Ugly hack to return the project folder when using IDE
            url = jarFile.getParentFile().getParentFile().getPath();
        }
        else {
            url = jarFile.getParentFile().getPath();
        }

        return java.net.URLDecoder.decode(url, StandardCharsets.UTF_8);
    }


    /**
     *
     * @return A string corresponding to the path of the runs data directory
     */
    public static Path getRunsDataPath() {
        return Paths.get(getJarPath(), AppSettings.TOOLS_DATA, AppSettings.RUNS_PATH);
    }

    /**
     *
     * @return A string corresponding to the path of the runs data directory
     */
    public static Path getPanelsDataPath() {
        return Paths.get(getJarPath(), AppSettings.TOOLS_DATA, AppSettings.PANELS_PATH);
    }

    /**
     *
     * @return A string corresponding to the path of the runs data directory
     */
    public static Path getCNVControlsDataPath() {
        return Paths.get(getJarPath(), AppSettings.TOOLS_DATA, AppSettings.CNV_DIRNAME, AppSettings.CNV_CONTROLES_DIRNAME);
    }

    public static String getAppName() {
        String version = "null";
        final Properties properties = new Properties();
        try {
            properties.load(App.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("artifactId");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getVersion() {
        String version = "null";
        final Properties properties = new Properties();
        try {
            properties.load(App.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Return the CHU service of the application instance
     */
    public Service getService() {return service;}

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", PreloaderSplash.class.getCanonicalName());
        launch(args);
    }


}
