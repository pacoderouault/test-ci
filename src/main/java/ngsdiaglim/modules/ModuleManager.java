package ngsdiaglim.modules;

import javafx.scene.layout.AnchorPane;
import ngsdiaglim.controllers.*;
import ngsdiaglim.controllers.analysisview.AnalysisViewController;

public class ModuleManager {

    private static HomeController homeController;
    private static UsersManageController usersManageController;
    private static CreateAnalysisParametersController createAnalysisParameters;
    private static AnalysisViewController analysisViewController;
    private static GenePanelsManageController genePanelsManageController;
    private static AccountController accountController;
    private static ManageCNVsController manageCNVsController;

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

    public static GenePanelsManageController getGenePanelsManageController() {return genePanelsManageController;}

    public static void setGenePanelsManageController(AnchorPane box) {
        genePanelsManageController = genePanelsManageController == null ? new GenePanelsManageController() : genePanelsManageController;
        config(box, genePanelsManageController);
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

    public static ManageCNVsController getManageCNVsController() {return manageCNVsController;}

    public static void setManageCNVsController(AnchorPane box) {
        manageCNVsController = manageCNVsController == null ? new ManageCNVsController() : manageCNVsController;
        config(box, manageCNVsController);
    }

    public static AnalysisViewController getAnalysisViewController() {return analysisViewController;}

    public static void setAnalysisViewController(AnchorPane box) {
        analysisViewController = analysisViewController == null ? new AnalysisViewController() : analysisViewController;
//        if (analysisViewController != null) analysisViewController.setAnalysis(null);
//        analysisViewController = new AnalysisViewController();
        config(box, analysisViewController);
    }

    public static AccountController getAccountController() {return accountController;}

    public static void setAccountController(AnchorPane box) {
        accountController = accountController == null ? new AccountController() : accountController;
        config(box, accountController);
    }
}
