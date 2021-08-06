package ngsdiaglim.modules;

import javafx.scene.layout.AnchorPane;
import ngsdiaglim.controllers.CreateAnalysisParametersController;
import ngsdiaglim.controllers.HomeController;
import ngsdiaglim.controllers.UsersManageController;

public class ModuleManager {

    private static HomeController homeController;
    private static UsersManageController usersManageController;
    private static CreateAnalysisParametersController createAnalysisParameters;

    public static void config(AnchorPane box, AnchorPane content) {
        box.getChildren().clear();
        box.getChildren().add(content);
        Resize.margin(content, 0);
    }

    public static void clearModules(){
        homeController = null;
        usersManageController = null;
        createAnalysisParameters = null;
    }

    public static UsersManageController getUsersManageController() {return usersManageController;}

    public static void setUsersManageController(AnchorPane box) {
        usersManageController = usersManageController == null ? new UsersManageController() : usersManageController;
        config(box, usersManageController);
    }

    public static HomeController getHomeController() {return homeController;}

    public static void setHomeController(AnchorPane box) {
        homeController = homeController == null ? new HomeController() : homeController;
        config(box, homeController);
    }

    public static CreateAnalysisParametersController getCreateAnalysisParameters() {return createAnalysisParameters;}

    public static void setCreateAnalysisParameters(AnchorPane box) {
        createAnalysisParameters = createAnalysisParameters == null ? new CreateAnalysisParametersController() : createAnalysisParameters;
        config(box, createAnalysisParameters);
    }
}
