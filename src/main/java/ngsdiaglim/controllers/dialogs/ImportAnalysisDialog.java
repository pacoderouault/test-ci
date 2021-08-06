package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.*;
import ngsdiaglim.modeles.analyse.AnalysesInputDirParser;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.FileChooserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ImportAnalysisDialog extends DialogPane.Dialog<AnalysisInputData> {

    private final Logger logger = LogManager.getLogger(ImportAnalysisDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private Label runNameLb;
    @FXML private Button importFromDirBtn;
    @FXML private Button importSingleAnalysisBtn;
    @FXML private TableView<AnalysisInputData> analysisTable;
    @FXML private TableColumn<AnalysisInputData, AnalysisInputData.AnalysisInputState> colAnalysisState;
    @FXML private TableColumn<AnalysisInputData, String> colAnalysisName;
    @FXML private TableColumn<AnalysisInputData, String> colSampleName;
    @FXML private TableColumn<AnalysisInputData, File> colVcfFile;
    @FXML private TableColumn<AnalysisInputData, File> colBamFile;
    @FXML private TableColumn<AnalysisInputData, File> colDepth;
    @FXML private TableColumn<AnalysisInputData, AnalysisParameters> colAnalysisParameters;
    @FXML private TableColumn<AnalysisInputData, Void> colActions;

    private final Run run;

    public ImportAnalysisDialog(DialogPane pane, Run run) {
        super(pane, DialogPane.Type.INPUT);
        this.run = run;
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ImportAnalysesDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("importanalysesdialog.title"));

        setContent(dialogContainer);
        initView();

        setMaximize(true);
    }

    private void initView() {

        Object[] arguments = {run.getName()};
        runNameLb.setText(BundleFormatter.format("importanalysesdialog.lb.runName", arguments));

        initTable();

        importFromDirBtn.setOnAction(e -> {
            DirectoryChooser dc = FileChooserUtils.getDirectoryChooser();
            File directory = dc.showDialog(App.getPrimaryStage());
            if (directory != null) {
                AnalysesInputDirParser analysesInputDirParser = new AnalysesInputDirParser(run, directory);
                try {
                    HashMap<String, AnalysisInputData> analysesInput = analysesInputDirParser.parseInputDir();
                    analysisTable.getItems().addAll(analysesInput.values());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initTable() {

        analysisTable.setEditable(true);
        colAnalysisState.setCellValueFactory(data -> data.getValue().stateProperty());
        colAnalysisState.setCellFactory(data -> new ImportAnalysisStateTableCell());
        colAnalysisName.setCellValueFactory(data -> data.getValue().analysisNameProperty());
        colAnalysisName.setCellFactory(TextFieldTableCell.forTableColumn());
        colAnalysisName.setOnEditCommit(t -> {
            if(t.getRowValue() != null) {
                if (StringUtils.isBlank(t.getNewValue())) {
                    Message.error(App.getBundle().getString("importanalysesdialog.msg.err.emptyAnalysisName"));
                } else {
                    t.getRowValue().setAnalysisName(t.getNewValue());
                }
                analysisTable.refresh();
            }
        });
        colSampleName.setCellValueFactory(data -> data.getValue().sampleNameProperty());
        colSampleName.setCellFactory(TextFieldTableCell.forTableColumn());
        colSampleName.setOnEditCommit(t -> {
            if(t.getRowValue() != null) {
                if (StringUtils.isBlank(t.getNewValue())) {
                    Message.error(App.getBundle().getString("importanalysesdialog.msg.err.emptyName"));
                } else {
                    t.getRowValue().setSampleName(t.getNewValue());
                }
                analysisTable.refresh();
            }
        });
        colVcfFile.setCellValueFactory(data -> data.getValue().vcfFileProperty());
        colVcfFile.setCellFactory(data -> new ImportAnalysisVCFTableCell(true));
        colBamFile.setCellValueFactory(data -> data.getValue().bamFileProperty());
        colBamFile.setCellFactory(data -> new ImportAnalysisBamTableCell(true));
        colDepth.setCellValueFactory(data -> data.getValue().depthFileProperty());
        colDepth.setCellFactory(data -> new ImportAnalysisDepthTableCell(true));
        colAnalysisParameters.setCellValueFactory(data -> data.getValue().analysisParametersProperty());
        colAnalysisParameters.setCellFactory(data-> new ImportAnalysisAnalysisParametersTableCell());
        colActions.setCellFactory(data -> new ImportAnalysisActionsTableCell());

        // auto adjust the width of the column clonotypesNameCol
        colAnalysisName.prefWidthProperty().bind(
                analysisTable.widthProperty()
                        .subtract(colAnalysisState.widthProperty())
                        .subtract(colSampleName.widthProperty())
                        .subtract(colVcfFile.widthProperty())
                        .subtract(colBamFile.widthProperty())
                        .subtract(colDepth.widthProperty())
                        .subtract(colAnalysisParameters.widthProperty())
                        .subtract(colActions.widthProperty())
                        .subtract(2));  // a border stroke?
    }


}
