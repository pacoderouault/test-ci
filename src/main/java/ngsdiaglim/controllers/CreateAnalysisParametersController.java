package ngsdiaglim.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CreateAnalysisParametersController extends Module {

    private final static Logger logger = LogManager.getLogger(CreateAnalysisParametersController.class);

    @FXML private Tab analysisParamsTab;
    @FXML private Tab panelsTab;
    @FXML private Tab genesTranscriptsTab;
    @FXML private Tab hotspotsTab;
    @FXML private Tab ciqsTab;

    private AnalysisParametersCreateController analysisParametersCreateController;
    private AnalysisParametersPanelsController analysisParametersPanelsController;
    private AnalysisParametersGenesTrancriptsController analysisParametersGenesTrancriptsController;
    private AnalysisParametersHotspotsController analysisParametersHotspotsController;
    private AnalysisParametersCIQController analysisParametersCIQController;

//    private final Tooltip inactivePanelTooltip = new Tooltip(App.getBundle().getString("createAnalasisParameters.tooltip.inactivePanel"));

    public CreateAnalysisParametersController() {

        super(App.getBundle().getString("createAnalasisParameters.title"));
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
            return;
        }
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CreateAnalysisParameters.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
            logger.error("Problem when loading the create analysis panel", e);
        }
        analysisParametersPanelsController = new AnalysisParametersPanelsController(this);
        panelsTab.setContent(analysisParametersPanelsController);

        analysisParametersGenesTrancriptsController = new AnalysisParametersGenesTrancriptsController(this);
        genesTranscriptsTab.setContent(analysisParametersGenesTrancriptsController);

        analysisParametersHotspotsController = new AnalysisParametersHotspotsController(this);
        hotspotsTab.setContent(analysisParametersHotspotsController);

        analysisParametersCreateController = new AnalysisParametersCreateController(this);
        analysisParamsTab.setContent(analysisParametersCreateController);

        analysisParametersCIQController = new AnalysisParametersCIQController(this);
        ciqsTab.setContent(analysisParametersCIQController);
    }

    @FXML
    private void initialize() {

    }

    public AnalysisParametersCreateController getAnalysisParametersCreateController() {return analysisParametersCreateController;}

    public AnalysisParametersPanelsController getAnalysisParametersPanelsController() {return analysisParametersPanelsController;}

    public AnalysisParametersGenesTrancriptsController getAnalysisParametersGenesTrancriptsController() {return analysisParametersGenesTrancriptsController;}

    public AnalysisParametersHotspotsController getAnalysisParametersHotspotsController() {return analysisParametersHotspotsController;}

    public AnalysisParametersCIQController getAnalysisParametersCIQController() {return analysisParametersCIQController;}
}
