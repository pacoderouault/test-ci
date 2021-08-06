package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.User;

import java.io.IOException;
import java.sql.SQLException;

public class LoggingController {

    @FXML private StackPane dialogPaneContainer;
    @FXML private TextField loginTf;
    @FXML private PasswordField passwordField;
    private final DialogPane dialogPane = new DialogPane();
    @FXML
    public void initialize(){
        dialogPaneContainer.getChildren().add(dialogPane);
    }

    @FXML
    private void connect() {
        String login = loginTf.getText();
        String password = passwordField.getText();

        String error = checkCredentials(login, password);
        if (error != null) {
            dialogPane.showError("Erreur", error);
            return;
        }

        try {
            User user = DAOController.get().getUsersDAO().checkUserConnection(login, password);
            if (user == null) {
                dialogPane.showError("TIELE", App.getBundle().getString("login.msg.err.badConnection"));
            }
            else {
                App.get().setLoggedUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String checkCredentials(String login, String password) {
        if (login.trim().isEmpty()) {
            return App.getBundle().getString("login.msg.err.badLoginFormat");
        }
        else if (password.trim().isEmpty()) {
            return App.getBundle().getString("login.msg.err.badPasswordFormat");
        }
        return null;
    }
}
