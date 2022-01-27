package ngsdiaglim.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.ChangePasswordDialog;
import ngsdiaglim.controllers.dialogs.ChangeUsernameDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.VariantsTableTheme;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class AccountController extends Module {

    private static final Logger logger = LogManager.getLogger(AccountController.class);
    @FXML private TextField userNameTf;
    @FXML private ComboBox<VariantsTableTheme> themesCb;
    @FXML private CheckBox colorUniqueVariantsCb;
    @FXML private CheckBox useSmoothScrolling;
    @FXML private ImageView themeIv;

    public AccountController() {
        super(App.getBundle().getString("account.title"));
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/Account.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Problem when loading the manage user panel", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
    }

    @FXML
    public void initialize() {
        userNameTf.textProperty().bind(App.get().getLoggedUser().usernameProperty());
        for (VariantsTableTheme t : VariantsTableTheme.values()) {
            themesCb.getItems().add(t);
        }
        themesCb.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                colorUniqueVariantsCb.setVisible(newV.equals(VariantsTableTheme.THEME1));
                App.get().getLoggedUser().setPreference(DefaultPreferencesEnum.VARIANT_TABLE_THEME, newV.name());
                App.get().getLoggedUser().savePreferences();
            }
        });
        String themeName = App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.VARIANT_TABLE_THEME);
        if (themeName != null) {
            VariantsTableTheme theme = VariantsTableTheme.valueOf(themeName);
            themesCb.getSelectionModel().select(theme);
        }
        colorUniqueVariantsCb.setSelected(Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.COLOR_UNIQUE_VARIANTS)));
        colorUniqueVariantsCb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                App.get().getLoggedUser().setPreference(DefaultPreferencesEnum.COLOR_UNIQUE_VARIANTS, newV);
                App.get().getLoggedUser().savePreferences();
            }
        });

        useSmoothScrolling.setSelected(Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.USE_SMOOTH_SCROLLING)));
        useSmoothScrolling.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                App.get().getLoggedUser().setPreference(DefaultPreferencesEnum.USE_SMOOTH_SCROLLING, newV);
                App.get().getLoggedUser().savePreferences();
            }
        });
    }

    @FXML
    private void changeUsername() {
        ChangeUsernameDialog dialog = new ChangeUsernameDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        dialog.getButton(ButtonType.OK).setOnAction(event -> {
            try {
                if (DAOController.getUsersDAO().checkUserConnection(App.get().getLoggedUser().getUsername(), dialog.getValue().getPassword()) == null) {
                    Message.error(App.getBundle().getString("changeusernamedialog.msg.err.invalidPassword"));
                } else {
                    App.get().getLoggedUser().setUsername(dialog.getValue().getNewUsername());
                    DAOController.getUsersDAO().updateUsername(App.get().getLoggedUser());
                    Message.hideDialog(dialog);
                }
            } catch (SQLException e) {
                logger.error(e);
                Message.error(e.getMessage(), e);
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(dialog);
        dialog.getButton(ButtonType.OK).setOnAction(event -> {
            try {
                if (DAOController.getUsersDAO().checkUserConnection(App.get().getLoggedUser().getUsername(), dialog.getValue().getOldPassword()) == null) {
                    Message.error(App.getBundle().getString("changeusernamedialog.msg.err.invalidPassword"));
                } else {
                    DAOController.getUsersDAO().updatePassword(App.get().getLoggedUser(), dialog.getValue().getNewPassword());
                    Message.hideDialog(dialog);
                }
            } catch (SQLException e) {
                logger.error(e);
                Message.error(e.getMessage(), e);
                e.printStackTrace();
            }
        });
    }
}
