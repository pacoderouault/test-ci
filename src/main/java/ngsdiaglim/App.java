package ngsdiaglim;

import fr.brouillard.oss.cssfx.CSSFX;
import htsjdk.samtools.*;
import htsjdk.variant.vcf.VCFFileReader;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ngsdiaglim.controllers.AppController;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.DatabaseConnection;
import ngsdiaglim.modeles.parsers.SamtoolsDepthParser;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.dao.DatabaseCreatorDAO;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BamUtils;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;


public class App extends Application {

    private static App instance;
    private AppController appController;
    private static final Logger logger = LogManager.getLogger(App.class);
    public static Locale locale = Locale.FRANCE;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("strings/NGSDiag", locale);
    private static Stage primaryStage;
    private final SimpleObjectProperty<User> loggedUser = new SimpleObjectProperty<User>();
    private AppSettings appSettings;


    public void init() {
        instance = this;
        DatabaseCreatorDAO databaseCreatorDAO = new DatabaseCreatorDAO();
//        if (!DatabaseConnection.instance().isDbExists()) {
//        System.out.println(databaseCreatorDAO.exists());
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
    }

    @Override
    public void start(Stage stage) {
        try {
            appSettings = new AppSettings();
        } catch (IOException e) {
            logger.error("Error when reader application properties file");
        }
        CSSFX.start();
        primaryStage = stage;

        primaryStage.setMaximized(Boolean.parseBoolean(appSettings.getProperty(AppSettings.DefaultAppSettings.MAXIMIZED.name())));
        primaryStage.maximizedProperty().addListener((obs, oldV, newV) -> {
            appSettings.setValue(AppSettings.DefaultAppSettings.MAXIMIZED, newV);
        });

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
            logger.fatal("Error when loadding logging view fxml");
            Message.error(bundle.getString("app.msg.err.loadLoggingScreenErr"), e.getMessage());
        }

        try {
            setLoggedUser(DAOController.get().getUsersDAO().getUser("admin"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            DAOController.get().getRunsDAO().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadLoggingScreen() throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/Logging.fxml"));
        fxml.setResources(App.bundle);
        AnchorPane page = fxml.load();
        Scene scene = new Scene(page);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void loadAppscreen() throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/App.fxml"));
        fxml.setResources(bundle);
        AnchorPane page = fxml.load();
        appController = fxml.getController();
        Scene scene = new Scene(page);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/theme.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

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

    public static void main(String[] args) {
        launch(args);
    }

}
