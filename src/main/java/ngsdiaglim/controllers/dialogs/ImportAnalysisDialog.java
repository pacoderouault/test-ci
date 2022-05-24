package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import htsjdk.tribble.TribbleException;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.cells.*;
import ngsdiaglim.controllers.ui.RunFileNode;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.exceptions.DuplicateSampleInRun;
import ngsdiaglim.modeles.analyse.*;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ImportAnalysisDialog extends DialogPane.Dialog<AnalysisInputData> {

    private final static Logger logger = LogManager.getLogger(ImportAnalysisDialog.class);
    private static final NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    @FXML private VBox dialogContainer;
    @FXML private Label runNameLb;
    @FXML private Button importFromDirBtn;
    @FXML private Button importSingleAnalysisBtn;
    @FXML private FlowPane runFilesFp;
    @FXML private TableView<AnalysisInputData> analysisTable;
    @FXML private TableColumn<AnalysisInputData, AnalysisInputData.AnalysisInputState> colAnalysisState;
    @FXML private TableColumn<AnalysisInputData, String> colAnalysisName;
    @FXML private TableColumn<AnalysisInputData, String> colSampleName;
    @FXML private TableColumn<AnalysisInputData, File> colVcfFile;
    @FXML private TableColumn<AnalysisInputData, File> colBamFile;
    @FXML private TableColumn<AnalysisInputData, File> colDepth;
    @FXML private TableColumn<AnalysisInputData, AnalysisParameters> colAnalysisParameters;
    @FXML private TableColumn<AnalysisInputData, CIQModel> colCIQ;
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

        try {
            initView();
        } catch (SQLException e) {
            logger.error("Error when getting run files from db", e);
            Message.error(e.getMessage(), e);
        }

        setMaximize(true);
    }

    private void initView() throws SQLException {

        Object[] arguments = {run.getName()};
        runNameLb.setText(BundleFormatter.format("importanalysesdialog.lb.runName", arguments));

        initTable();
        fillRunFiles();
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
                    t.getRowValue().setAnalysisName(t.getNewValue().trim());
                }
                analysisTable.refresh();
                checkDuplicateAnalysisNames();
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
        colCIQ.setCellValueFactory(data -> data.getValue().ciqModelProperty());
        colCIQ.setCellFactory(data-> new ImportAnalysisCIQTableCell());

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

        analysisTable.getItems().addListener((ListChangeListener<AnalysisInputData>) c -> checkDuplicateAnalysisNames());
    }


    private void fillRunFiles() throws SQLException {
        List<RunFile> runFiles = DAOController.getRunFilesDAO().getRunFiles(run);
        for (RunFile runFile : runFiles) {
            runFilesFp.getChildren().add(new RunFileNode(runFile));
        }
    }

    @FXML
    private void addRunFile() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Reports Files", "*.pdf", "*.html"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            RunFile runFile = new RunFile(selectedFile, run);
            runFilesFp.getChildren().add(new RunFileNode(runFile));
        }
    }


    public List<RunFile> getRunsFiles() {
        List<RunFile> runsFiles = new ArrayList<>();
        for (Node node :  runFilesFp.getChildren()) {
            if (node instanceof RunFileNode) {
                runsFiles.add(((RunFileNode) node).getRunFile());
            }
        }
        return runsFiles;
    }


    @FXML
    private void importAnalysesFromDirectory() {
        DirectoryChooser dc = FileChooserUtils.getDirectoryChooser();
        File directory = dc.showDialog(App.getPrimaryStage());
        if (directory != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(directory));
            user.savePreferences();
            WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("importanalysesdialog.msg.parseDirectory"));
            wid.exec("parseDir", inputParam -> {
                AnalysesInputDirParser analysesInputDirParser = new AnalysesInputDirParser(run, directory);
                try {
                    analysesInputDirParser.parseInputDir();
                    HashMap<String, AnalysisInputData> analysesInput = analysesInputDirParser.getAnalysesFiles();
                    List<RunFile> runFiles = analysesInputDirParser.getRunFiles();
                    Platform.runLater(() -> analysesInput.keySet().stream().sorted(naturalSortComparator).forEach(s -> analysisTable.getItems().add(analysesInput.get(s))));
                    for (RunFile runFile : runFiles) {
                        Platform.runLater(() -> runFilesFp.getChildren().add(new RunFileNode(runFile)));
                    }
                } catch (IOException | DuplicateSampleInRun | SQLException ex) {
                    logger.error(ex.getMessage(), ex);
                    Platform.runLater(() ->Message.error(ex.getMessage(), ex));
                    return 1;
                }
                return 0;
            });

        }
    }


    @FXML
    private void importAnalysesFromFile() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        fc.setTitle(App.getBundle().getString("importanalysesdialog.lb.importingVCFFile"));
        File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            String sampleName = "";
            String analysisName = selectedFile.getName().replaceAll("\\.vcf|\\.gz", "");
            if (VCFUtils.isVCFReadable(selectedFile)) {
                try {
                    sampleName = VCFUtils.getSamplesName(selectedFile).get(0);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    Message.error(e.getMessage(), e);
                }
            }
            AnalysisInputData aid = new AnalysisInputData(run, analysisName, sampleName);
            aid.setVcfFile(selectedFile);

            analysisTable.getItems().add(aid);
        }
    }


    private void checkDuplicateAnalysisNames() {
        for (AnalysisInputData aid : analysisTable.getItems()) {
            long count = analysisTable.getItems().stream().filter(p -> p.getAnalysisName().equals(aid.getAnalysisName())).count();
            if (count > 1) {
                aid.setState(AnalysisInputData.AnalysisInputState.DUPLICATE_ANALYSIS);
            } else {
                aid.setState(AnalysisInputData.AnalysisInputState.VALID);
                aid.computeState();
            }
        }
    }

    public boolean hasAnalysesInError() {
        checkDuplicateAnalysisNames();
        return analysisTable.getItems().stream().anyMatch(a -> !a.getState().equals(AnalysisInputData.AnalysisInputState.VALID));
    }

    public ObservableList<AnalysisInputData> getAnalysisInputData() {
        return analysisTable.getItems();
    }


}
