package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.*;
import ngsdiaglim.controllers.dialogs.AddRunDialog;
import ngsdiaglim.controllers.dialogs.ImportAnalysisDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.RunsStatisticsDAO;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.enumerations.Order;
import ngsdiaglim.enumerations.RunOrder;
import ngsdiaglim.modeles.analyse.*;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HomeController extends Module {

    Logger logger = LogManager.getLogger(HomeController.class);

    @FXML private Pagination runsPagination;
    @FXML private TableView<Run> runsTable;
    @FXML private TableColumn<Run, String> runNameCol;
    @FXML private TableColumn<Run, LocalDate> runDateCol;
    @FXML private TableColumn<Run, Void> runStateCol;
    @FXML private TableColumn<Run, Void> runActionsCol;
    @FXML private TableView<Analysis> analysesTable;
    @FXML private TableColumn<Analysis, String> analysisNameCol;
    @FXML private TableColumn<Analysis, String> analysisSampleNameCol;
    @FXML private TableColumn<Analysis, AnalysisParameters> analysisParametersCol;
    @FXML private TableColumn<Analysis, AnalysisStatus> analysisStateCol;
    @FXML private TableColumn<Analysis, Void> analysisActionsCol;
    @FXML private Button addRunBtn;
    @FXML private Button addAnalysesBtn;
    @FXML private CustomTextField searchRunCtf;
    @FXML private Button closeSearchBtn;
    @FXML private HBox statisticsContainer;
    @FXML private Label statsRunsLb;
    @FXML private Label statsAnalysesLb;
    @FXML private FlowPane statsPanelsFp;
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Label analysesTablePlaceholderLb = new Label(App.getBundle().getString("home.module.analyseslist.table.msg.emptyAnalyses"));
    private final int runsByPage = 20;
    private Thread statisticsThread;

    public HomeController() {
        super(App.getBundle().getString("home.title"));
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
//        Platform.runLater(() -> {
//            TableViewSkin<?> tableSkin = (TableViewSkin<?>) runsTable.getSkin();
//            VirtualFlow<?> virtualFlow = (VirtualFlow<?>) tableSkin.getChildren().get(1);
//            System.out.println(virtualFlow.getLastVisibleCell());
//            System.out.println(runsTable.getHeight());
//        });
//        runsTable.heightProperty().addListener((obs, oldv, newV) -> {
//
//            try {
//                loadRuns();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
        Platform.runLater(() -> {
            try {
                initSearchRunTextField();
                initRunsPagination();
                loadRuns();
                fillStatistics();

            } catch (SQLException e) {
                logger.error("Error when getting runs", e);
                Message.error(e.getMessage(), e);
            }
        });
//        getScene().getWindow().setOnShown(e -> {
//            System.out.println(runsTable.getHeight());
//        });
//        runsTable.heightProperty().addListener((obs, oldV, newV) -> {
//            try {
//                loadRuns();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
    }

    @FXML
    private void initialize() {
        addAnalysesBtn.setDisable(runsTable.getSelectionModel().getSelectedItem() == null || !App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_ANALYSE));

        initRunTable();
        initAnalysisTable();


        addRunBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_RUN));
    }


    private void initRunTable() {

        runNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        runDateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        runStateCol.setCellFactory(data -> new RunStateTableCell());
        runActionsCol.setCellFactory(data -> new RunActionTableCell());

        runNameCol.prefWidthProperty().bind(
                runsTable.widthProperty()
                        .subtract(runDateCol.widthProperty())
                        .subtract(runStateCol.widthProperty())
                        .subtract(runActionsCol.widthProperty())
                        .subtract(17)  // a border stroke?
        );

        runsTable.setOnSort(e -> {
            Optional<TableColumn<Run, ?>> optional = runsTable.getSortOrder().stream().filter(c -> c.equals(runNameCol) || c.equals(runDateCol)).findAny();
            if (optional.isPresent()) {
                try {
                    loadRuns();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        runsTable.setFixedCellSize(35);
        runsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            try {
                fillAnalysesTable();
                addAnalysesBtn.setDisable(newV == null || !App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_ANALYSE));
            } catch (SQLException e) {
                logger.error("Error when filling analysis table", e);
                Message.error(e.getMessage(), e);
            }
        });
    }


    private void initAnalysisTable() {
        analysisNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        analysisNameCol.setCellFactory(data -> new AnalysisNameTableCell());
        analysisSampleNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSampleName()));
        analysisParametersCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAnalysisParameters()));
        analysisStateCol.setCellValueFactory(data -> data.getValue().statusProperty());
        analysisStateCol.setCellFactory(data -> new AnalysisStateTableCell());
        analysisActionsCol.setCellFactory(data -> new AnalysisActionsTableCell());

        analysisNameCol.prefWidthProperty().bind(
                analysesTable.widthProperty()
                        .subtract(analysisSampleNameCol.widthProperty())
                        .subtract(analysisParametersCol.widthProperty())
                        .subtract(analysisStateCol.widthProperty())
                        .subtract(analysisActionsCol.widthProperty())
                        .subtract(17)  // a border stroke?
        );

        // Open analysis when double-clicking on row
        analysesTable.setRowFactory( tv -> {
            TableRow<Analysis> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Analysis rowData = row.getItem();
                    App.get().getAppController().openAnalysis(rowData);
                }
            });
            return row ;
        });
    }

    @FXML
    private void addRunHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_RUN)) {
            AddRunDialog dialog = new AddRunDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.isValid() && dialog.getValue() != null) {
                    long runId = -1;
                    RunCreator runCreator = null;
                    try {
                        runCreator = new RunCreator(dialog.getValue());
                        runId = runCreator.createRun();
                        loadRuns();
                        fillStatistics();
                        Message.hideDialog(dialog);
                    } catch (SQLException | IOException ex) {
                        try {
                            DAOController.getRunsDAO().deleteRun(runId);
                            try {
                                runCreator.deleteRunDirectory();
                            } catch (IOException exception) {
                                logger.error("Error when deleting run directory", exception);
                            }
                        } catch (SQLException exc) {
                            logger.error("Error when deleting run", exc);
                        }
                        logger.error("Error when adding run", ex);
                        Message.error(ex.getMessage(), ex);
                    }
                }
            });
        }
    }


    @FXML
    private void addAnalysesHandler() {
        Run run = runsTable.getSelectionModel().getSelectedItem();
        if (run != null) {
            ImportAnalysisDialog dialog = new ImportAnalysisDialog(App.get().getAppController().getDialogPane(), run);
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.hasAnalysesInError()) {
                    DialogPane.Dialog<ButtonType> dialog2 = Message.confirm(App.getBundle().getString("importanalysesdialog.msg.conf.importAnalysisError"));
                    dialog2.getButton(ButtonType.YES).setOnAction(event -> importAnalyses(run, dialog));

                } else {
                    importAnalyses(run, dialog);
                }
            });
        }
    }


    private void importAnalyses(Run run, ImportAnalysisDialog dialog) {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("importanalysesdialog.lb.importingAnalyses"));
        wid.addTaskEndNotification(r -> {
            if (r == 0) {
                Message.hideDialog(dialog);
            }
            try {
                fillAnalysesTable();
            } catch (SQLException ex) {
                logger.error("Error when adding panel", ex);
                Message.error(ex.getMessage(), ex);
            }
        });
        wid.exec("LoadPanels", inputParam -> {
            List<RunFile> runFileList = dialog.getRunsFiles();
            ObservableList<AnalysisInputData> analysisInputData = dialog.getAnalysisInputData().filtered(d -> d.getState().equals(AnalysisInputData.AnalysisInputState.VALID));
            RunImporter runImporter = new RunImporter(run, runFileList, analysisInputData, wid);
            try {
                runImporter.importRun();
            } catch (Exception ex) {
                logger.error("Error when adding panel", ex);
                Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                return 1;
            }
            return 0;
        });
    }


    private void initSearchRunTextField() {
        closeSearchBtn.visibleProperty().bind(searchRunCtf.textProperty().isNotEmpty());
        searchRunCtf.setLeft(new FontIcon("mdmz-search"));
        searchRunCtf.setOnAction(e -> {
            try {
                initRunsPagination();
                loadRuns();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }


    private void initRunsPagination() throws SQLException {

        int runsCount = DAOController.getRunsDAO().getRunsCount(searchRunCtf.getText());
//        int maxItems = (int) (runsTable.getHeight() / runsTable.getFixedCellSize()) - 2;
//        this.runsByPage = Math.max(1, maxItems);
        int nbPage = (int) (Math.ceil(runsCount * 1.0 / runsByPage));
        runsPagination.setPageCount(nbPage);
        runsPagination.setCurrentPageIndex(0);

        runsPagination.currentPageIndexProperty().addListener((obs, oldV, newV) -> {
            try {
                loadRuns();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    @FXML
    private void clearRunSearch() {
        searchRunCtf.setText(null);
        searchRunCtf.fireEvent(new ActionEvent());
    }


    public void loadRuns() throws SQLException {
//        int maxItems = (int) (runsTable.getHeight() / runsTable.getFixedCellSize()) - 2;
//        this.runsByPage = Math.max(1, maxItems);
        // check if the name col or the date col is sorted
        runsPagination.setMaxPageIndicatorCount(10);
        RunOrder runOrder = RunOrder.DATE;
        Order order = Order.DESC;
        for (TableColumn<Run, ?> tc : runsTable.getSortOrder()) {
            if (tc.equals(runNameCol)) {
                runOrder = RunOrder.NAME;
                if (tc.getSortType().equals(TableColumn.SortType.ASCENDING)) {
                    order = Order.ASC;
                }
                break;
            }

            if (tc.equals(runDateCol)) {
                if (tc.getSortType().equals(TableColumn.SortType.ASCENDING)) {
                    order = Order.ASC;
                }
                break;
            }
        }

        String filterName = searchRunCtf.getText();

        runsTable.getItems().setAll(DAOController.getRunsDAO().getRuns(filterName, order, runOrder, runsPagination.getCurrentPageIndex() * runsByPage, runsByPage));


//        runsTable.getItems().setAll(DAOController.getRunsDAO().getRuns());
//        fillStatistics();
    }

    public void fillAnalysesTable() throws SQLException {
        Run selectedRun = runsTable.getSelectionModel().getSelectedItem();
        if (selectedRun != null) {
            runsTable.setDisable(true);
            analysesTable.setItems(null);
            analysesTable.setPlaceholder(progressIndicator);
            new Thread(() -> {
                try {
                    ObservableList<Analysis> analyses = DAOController.getAnalysisDAO().getAnalysis(selectedRun);
                    Platform.runLater(() -> {
                        analysesTable.setItems(analyses);
                        analysesTable.setPlaceholder(analysesTablePlaceholderLb);
                    });
                } catch (SQLException e) {
                    logger.error(e);
                    Platform.runLater(() -> Message.error(e.getMessage(), e));
                }
                Platform.runLater(() -> {
                    runsTable.setDisable(false);
                    runsTable.requestFocus();
                });
            }).start();


        }
        else {
            analysesTable.getItems().clear();
        }
    }


    private void fillStatistics() {
        if (statisticsThread != null && statisticsThread.isAlive()) {
            statisticsThread.interrupt();
        }
        Runnable task = () -> {
            try {
                RunsStatisticsDAO.RunsStatistics runsStatistics = DAOController.getRunsStatisticsDAO().getRunsStatistics();
                Platform.runLater(() -> {

                    Object[] runNb = {runsStatistics.getRunNb()};
                    statsRunsLb.setText(BundleFormatter.format("home.module.statistics.lb.runNb", runNb));

                    Object[] analysesNb = {runsStatistics.getAnalysisNb()};
                    statsAnalysesLb.setText(BundleFormatter.format("home.module.statistics.lb.analysesNb", analysesNb));

                    statsPanelsFp.getChildren().clear();
                    for (String panelName : runsStatistics.getAnalysisByPanelNb().keySet()) {
                        statsPanelsFp.getChildren().add(createPanelStatsPane(
                                panelName,
                                runsStatistics.getAnalysisByPanelNb().get(panelName).getFirst(),
                                runsStatistics.getAnalysisByPanelNb().get(panelName).getSecond()));
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        statisticsThread = new Thread(task);
        statisticsThread.start();

    }


    private VBox createPanelStatsPane(String panelName, Integer runsCount, Integer analysesCount) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.getStyleClass().addAll("module-box", "module-box-container");

        Label panelLb = new Label(panelName);
        panelLb.getStyleClass().add("font-medium");
        Label runCountLb = new Label();
        Label analysesCountLb = new Label();

        Object[] runNb = {runsCount};
        runCountLb.setText(BundleFormatter.format("home.module.statistics.lb.panelRunNb", runNb));

        Object[] analysesNb = {analysesCount};
        analysesCountLb.setText(BundleFormatter.format("home.module.statistics.lb.panelAnalysesNb", analysesNb));

        box.getChildren().addAll(panelLb, runCountLb, analysesCountLb);
        return box;

    }
}
