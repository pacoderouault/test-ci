package ngsdiaglim.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;

public abstract class Module extends AnchorPane {

    @FXML private HBox breadcrumb;
    private final String title;

    public Module(String title) {
        this.title = title;
    }

    private void initBreadcrumb() {
    }

    @FXML
    private void goHome() {
        App.get().getAppController().showUsersManageView();
    }

    public String getModuleTitle() {
        return title;
    }
}
