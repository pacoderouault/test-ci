package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.SearchVariantResult;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.DateFormatterUtils;
import ngsdiaglim.utils.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AnalysesSharingVariantDialog extends DialogPane.Dialog<Void> {

    private final static Logger logger = LogManager.getLogger(AnalysesSharingVariantDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private Label variantLb;
    @FXML private TableView<SearchVariantResult> variantsTable;
    @FXML private TableColumn<SearchVariantResult, String> runCol;
    @FXML private TableColumn<SearchVariantResult, String> analysisCol;
    @FXML private TableColumn<SearchVariantResult, String> sampleCol;
    @FXML private TableColumn<SearchVariantResult, String> dateCol;
    @FXML private TableColumn<SearchVariantResult, Integer> depthCol;
    @FXML private TableColumn<SearchVariantResult, Float> vafCol;
    @FXML private TableColumn<SearchVariantResult, Void> actionsCol;
    private final Analysis analysis;
    private final Annotation annotation;
    private final ObservableList<SearchVariantResult> searchVariantResults;
    private final DialogPane.Dialog<Void> instance;

    public AnalysesSharingVariantDialog(Analysis analysis, Annotation annotation, ObservableList<SearchVariantResult> searchVariantResults) {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INFORMATION);
        this.analysis = analysis;
        this.annotation = annotation;
        this.searchVariantResults = searchVariantResults;
        this.instance = this;
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysesSharingVariantDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        initView();
        setTitle(App.getBundle().getString("sharingvariantdialog.title"));
        setContent(dialogContainer);
    }

    private void initView() {

        variantLb.setText(
                annotation.getGenomicVariant().getContig() + ":" + annotation.getGenomicVariant().getStart() + annotation.getGenomicVariant().getRef() + ">" + annotation.getGenomicVariant().getAlt()
        );
        initTable();

    }

    private void initTable() {
        runCol.setCellValueFactory(data -> data.getValue().runNameProperty());
        analysisCol.setCellValueFactory(data -> data.getValue().analysisNameProperty());
        sampleCol.setCellValueFactory(data -> data.getValue().sampleNameProperty());
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(DateFormatterUtils.formatLocalDateTime(data.getValue().getAnalysisDate(), "dd/MM/yyyy - MM:ss")));
        depthCol.setCellValueFactory(data -> data.getValue().depthProperty().asObject());
        vafCol.setCellValueFactory(data -> data.getValue().vafProperty().asObject());
        actionsCol.setCellFactory(data -> new ActionsCell());
        variantsTable.setItems(searchVariantResults);
    }

    private class ActionsCell extends TableCell<SearchVariantResult, Void> {

        private final HBox box = new HBox();
        private final Button openAnalysisBtn = new Button(App.getBundle().getString("sharingvariantdialog.btn.open"));

        public ActionsCell() {
            box.getStyleClass().add("box-action-cell");
            openAnalysisBtn.getStyleClass().add("button-action-cell");
            box.getChildren().add(openAnalysisBtn);
            openAnalysisBtn.setOnAction(e -> openAnalysis());
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                setGraphic(null);
            } else {
                SearchVariantResult r = getTableRow().getItem();
                if (r != null && r.getAnalysisId() == analysis.getId()) {
                    openAnalysisBtn.setDisable(true);
                    setText(App.getBundle().getString("sharingvariantdialog.btn.currentAnalysis"));
                } else {
                    openAnalysisBtn.setDisable(false);
                }
                setGraphic(box);
            }
        }

        private void openAnalysis() {
            SearchVariantResult r = getTableRow().getItem();
            if (r != null) {
                try {
                    Run run = DAOController.getRunsDAO().getRun(r.getRunId());
                    Optional<Analysis> toLoad = run.getAnalyses().stream().filter(a -> a.getId() == r.getAnalysisId()).findAny();
                    toLoad.ifPresent(value -> {
                        App.get().getAppController().openAnalysis(value);
                        Message.hideDialog(instance);
                    });

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    Platform.runLater(() -> Message.error(e.getMessage(), e));
                }
//                Object[] messageArguments = {r.getAnalysisName()};
//                String message = BundleFormatter.format("sharingvariantdialog.msg.openingAnalysis", messageArguments);
//                WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), message);
//                wid.addTaskEndNotification(result -> {
//                    if (result == 0) {
//                        Message.hideDialog(instance);
//                    }
//                });
//                wid.exec("loadAnalysis", inputParam -> {
//                    try {
//                        Run run = DAOController.getRunsDAO().getRun(r.getRunId());
//                        Optional<Analysis> toLoad = run.getAnalyses().stream().filter(a -> a.getId() == r.getAnalysisId()).findAny();
//                        if (toLoad.isPresent()) {
//                            AnalysisParameters params = DAOController.getAnalysisParametersDAO().getAnalysisParameters(toLoad.get().getAnalysisParameters().getId());
//                            toLoad.get().setAnalysisParameters(params);
//                            VCFParser vcfParser = new VCFParser(toLoad.get().getVcfFile(), toLoad.get().getAnalysisParameters(), toLoad.get().getRun());
//                            vcfParser.parseVCF(true);
//                            toLoad.get().setAnnotations(vcfParser.getAnnotations());
//                            PlatformUtils.runAndWait(() -> {
//                                App.get().getAppController().showAnalysisView(toLoad.get());
////                                ModuleManager.getAnalysisViewController().getVariantsViewController().gotToVariant(annotation.getVariant());
//                            });
//                            PlatformUtils.runAndWait(() -> {
////                                App.get().getAppController().showAnalysisView(toLoad.get());
//                                ModuleManager.getAnalysisViewController().getVariantsViewController().gotToVariant(annotation.getVariant());
//                            });
////                            Platform.runLater(() -> App.get().getAppController().showAnalysisView(toLoad.get()));
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.getMessage(), e);
//                        Platform.runLater(() -> Message.error(e.getMessage(), e));
//                        return 1;
//                    }
//                    return 0;
//                });
            }
        }
    }
}
