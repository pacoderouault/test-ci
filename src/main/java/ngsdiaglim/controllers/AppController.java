package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AboutDialog;
import ngsdiaglim.controllers.dialogs.DocumentationDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.dialogs.SearchAnalysesDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BrowserUtils;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class AppController {

    private final static Logger logger = LogManager.getLogger(AppController.class);

    @FXML private StackPane stackpane;
    @FXML private Label logoLb;
    @FXML private SplitMenuButton userMenu;
    @FXML private SplitMenuButton adminMenu;
    @FXML private MenuItem userManagementMenuItem;
    @FXML private MenuItem analysisParametersManagementMenuItem;
    @FXML private MenuItem cnvsManagementMenuItem;
    @FXML private Button genePanelsBtn;
    @FXML private AnchorPane moduleContainer;
    @FXML private Label moduleName;
    private static final DialogPane dialogPane = new DialogPane();

    public DialogPane getDialogPane() {return dialogPane;}

    @FXML
    public void initialize() {

        dialogPane.setAnimationDuration(Duration.ZERO);
        stackpane.getChildren().add(dialogPane);
        logoLb.setText(App.getBundle().getString("app.name"));
        userMenu.textProperty().bind(App.get().getLoggedUser().usernameProperty());

        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES)
                || App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT)
                || App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)
                || App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_CNVS_PARAMETERS)
        ) {
            adminMenu.setVisible(true);
            adminMenu.setManaged(true);
        }
        else {
            adminMenu.setVisible(false);
            adminMenu.setManaged(false);
        }

        userManagementMenuItem.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ROLES)
                && !App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ACCOUNT));
        analysisParametersManagementMenuItem.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
        cnvsManagementMenuItem.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_CNVS_PARAMETERS));
        genePanelsBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_GENEPANELS));

        moduleContainer.getChildren().addListener((ListChangeListener<Node>) c -> initBreadcrumbs());
    }

    @FXML
    private void initBreadcrumbs() {

    }

    @FXML
    private void showAccountView() {
        ModuleManager.setAccountController(moduleContainer);
        this.moduleName.setText(ModuleManager.getAccountController().getModuleTitle());
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

    @FXML
    public void showManageGenePanelsView() {
        ModuleManager.setGenePanelsManageController(moduleContainer);
        this.moduleName.setText(ModuleManager.getGenePanelsManageController().getModuleTitle());
    }

    @FXML
    public void showCNVsParametersView() {
        ModuleManager.setManageCNVsController(moduleContainer);
        this.moduleName.setText(ModuleManager.getManageCNVsController().getModuleTitle());
    }

    public void showAnalysisView(Analysis analysis) {
        ModuleManager.setAnalysisViewController(moduleContainer);

    }

    @FXML
    private void showSearchAnalysesDialog() {
        SearchAnalysesDialog dialog = new SearchAnalysesDialog();
        Message.showDialog(dialog);
        dialog.getButton(ButtonType.OK).setOnAction(e -> Message.hideDialog(dialog));
    }

    @FXML
    private void showAboutDialog() {
        AboutDialog dialog = new AboutDialog(dialogPane);
        Message.showDialog(dialog);
    }

    @FXML
    private void showDocumentation() {
//        DocumentationDialog dialog = new DocumentationDialog(dialogPane);
//        Message.showDialog(dialog);
        URL is = getClass().getClassLoader().getResource("documentation.html");
        if (is != null) {
            BrowserUtils.openURL(is.toExternalForm());
        }
    }


    public void openAnalysis(Analysis analysis) {
        App.get().getAppController().showAnalysisView(analysis);
        if (analysis != null) {
            Object[] messageArguments = {analysis.getName()};
            String message = BundleFormatter.format("home.module.analyseslist.msg.openingAnalysis", messageArguments);
            WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), message);
            wid.addTaskEndNotification(r -> {
//                if (r == 0) {
//                    App.get().getAppController().showAnalysisView(analysis);
//                }
                ModuleManager.getAnalysisViewController().setAnalysis(analysis);
//                ModuleManager.getAnalysisViewController().getVariantsViewController().setDividerPosition();
            });
            wid.exec("LoadPanels", inputParam -> {
                try {
                    AnalysisParameters params = DAOController.getAnalysisParametersDAO().getAnalysisParameters(analysis.getAnalysisParameters().getId());
                    analysis.setAnalysisParameters(params);
                    VCFParser vcfParser = new VCFParser(analysis.getVcfFile(), analysis.getAnalysisParameters(), analysis.getRun());
                    vcfParser.parseVCF(true);
                    analysis.setAnnotations(vcfParser.getAnnotations());
                    analysis.loadCoverage();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    Platform.runLater(() -> Message.error(e.getMessage(), e));
                    return 1;
                }
                return 0;
            });
        }
    }
}
