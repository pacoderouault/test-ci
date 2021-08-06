package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import ngsdiaglim.App;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;

public class AppController {

    @FXML private StackPane stackpane;
    @FXML private Label logoLb;
    @FXML private SplitMenuButton userMenu;
    @FXML private SplitMenuButton adminMenu;
    @FXML private AnchorPane moduleContainer;
    @FXML private Label moduleName;

    private static final DialogPane dialogPane = new DialogPane();

    public DialogPane getDialogPane() {return dialogPane;}

    @FXML
    public void initialize() {
        stackpane.getChildren().add(dialogPane);
        logoLb.setText(App.getBundle().getString("app.name"));
        userMenu.textProperty().bind(App.get().getLoggedUser().usernameProperty());

        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES)
                || App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)) {
            adminMenu.setVisible(true);
            adminMenu.setVisible(true);
        }
        else {
            adminMenu.setVisible(false);
            adminMenu.setVisible(false);
        }

        moduleContainer.getChildren().addListener((ListChangeListener<Node>) c -> {
            initBreadcrumbs();
        });
    }

    @FXML
    private void initBreadcrumbs() {

    }

    @FXML
    private void disconnect() {
        App.get().setLoggedUser(null);
    }

    @FXML
    public void showHomeView() {
        ModuleManager.setHomeController(moduleContainer);
        this.moduleName.setText(ModuleManager.getHomeController().getModuleTitle());
    }

    @FXML
    public void showUsersManageView() {
        ModuleManager.setUsersManageController(moduleContainer);
        this.moduleName.setText(ModuleManager.getUsersManageController().getModuleTitle());
    }

    @FXML
    public void showCreateAnalysisParametersView() {
        ModuleManager.setCreateAnalysisParameters(moduleContainer);
        this.moduleName.setText(ModuleManager.getCreateAnalysisParameters().getModuleTitle());
    }
}
