package ngsdiaglim.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;

public class SplashController extends VBox {

    @FXML private Label appNameLb;
    @FXML private Label appVersionLb;

    @FXML
    private void initialize() {
        appNameLb.setText(App.getAppName());
        appVersionLb.setText(App.getVersion());
    }

}
