package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.AnalysisParametersActionsCol;
import ngsdiaglim.controllers.cells.GeneTranscriptsCell;
import ngsdiaglim.controllers.dialogs.AddGeneTranscriptSetDialog;
import ngsdiaglim.controllers.dialogs.AddPanelDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.analyse.RunConstants;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.parsers.GeneSetParser;
import ngsdiaglim.modeles.parsers.PanelParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.HotspotsSet;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class CreateAnalysisParametersController extends Module {

    private final static Logger logger = LogManager.getLogger(CreateAnalysisParametersController.class);

    @FXML private Tab analysisParamsTab;
    @FXML private Tab panelsTab;
    @FXML private Tab genesTranscriptsTab;
    @FXML private Tab hotspotsTab;

    private AnalysisParametersCreateController analysisParametersCreateController;
    private AnalysisParametersPanelsController analysisParametersPanelsController;
    private AnalysisParametersGenesTrancriptsController analysisParametersGenesTrancriptsController;
    private AnalysisParametersHotspotsController analysisParametersHotspotsController;

    private final Tooltip inactivePanelTooltip = new Tooltip(App.getBundle().getString("createAnalasisParameters.tooltip.inactivePanel"));

    public CreateAnalysisParametersController() {
        super(App.getBundle().getString("createAnalasisParameters.title"));
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CreateAnalysisParameters.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
            logger.error("Problem when loading the create analysis panel", e);
        }
    }

    @FXML
    private void initialize() {
        analysisParametersPanelsController = new AnalysisParametersPanelsController(this);
        panelsTab.setContent(analysisParametersPanelsController);

        analysisParametersGenesTrancriptsController = new AnalysisParametersGenesTrancriptsController(this);
        genesTranscriptsTab.setContent(analysisParametersGenesTrancriptsController);

        analysisParametersHotspotsController = new AnalysisParametersHotspotsController(this);
        hotspotsTab.setContent(analysisParametersHotspotsController);

        analysisParametersCreateController = new AnalysisParametersCreateController(this);
        analysisParamsTab.setContent(analysisParametersCreateController);
    }

    public AnalysisParametersCreateController getAnalysisParametersCreateController() {return analysisParametersCreateController;}

    public AnalysisParametersPanelsController getAnalysisParametersPanelsController() {return analysisParametersPanelsController;}

    public AnalysisParametersGenesTrancriptsController getAnalysisParametersGenesTrancriptsController() {return analysisParametersGenesTrancriptsController;}

    public AnalysisParametersHotspotsController getAnalysisParametersHotspotsController() {return analysisParametersHotspotsController;}
}
