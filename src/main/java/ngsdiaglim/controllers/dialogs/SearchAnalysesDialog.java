package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.cells.AnalysisActionsTableCell;
import ngsdiaglim.controllers.cells.AnalysisNameTableCell;
import ngsdiaglim.controllers.cells.AnalysisStateTableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;

public class SearchAnalysesDialog extends DialogPane.Dialog<Void> {

    private final static Logger logger = LogManager.getLogger(SearchAnalysesDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TableView<Analysis> analysesTable;
    @FXML private TableColumn<Analysis, Run> runCol;
    @FXML private TableColumn<Analysis, String> nameCol;
    @FXML private TableColumn<Analysis, String> barcodeCol;
    @FXML private TableColumn<Analysis, AnalysisParameters> paramsCol;
    @FXML private TableColumn<Analysis, AnalysisStatus> statusCol;
    @FXML private TableColumn<Analysis, Void> actionsCol;
    @FXML private CustomTextField searchTf;
    @FXML private Label resultCountLb;

    private final FontIcon searchIcon = new FontIcon("mdmz-search");

    public SearchAnalysesDialog() {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INFORMATION);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/SearchAnalysesDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setTitle(App.getBundle().getString("searchanalysesdialog.title"));
        setContent(dialogContainer);
        initView();
    }

    private void initView() {
        searchTf.setLeft(searchIcon);
//        searchTf.setOnAction(e -> {
//            search();
//            e.consume();
//        });
        initResultsTable();
    }

    private void initResultsTable() {
        runCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getRun()));
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        nameCol.setCellFactory(data -> new AnalysisNameTableCell());
        barcodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSampleName()));
        paramsCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAnalysisParameters()));
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        statusCol.setCellFactory(data -> new AnalysisStateTableCell());
        actionsCol.setCellFactory(data -> new AnalysisActionsTableCell(false));
    }

    @FXML
    private void search() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("searchanalysesdialog.msg.searching"));
        wid.addTaskEndNotification(r -> {
            resultCountLb.setVisible(r == 0);
            updateResultCount();
        });
        wid.exec("searchAnalyses", inputParams -> {
            analysesTable.getItems().clear();
            String query = searchTf.getText();
            if (query != null && !query.isEmpty()) {
                try {
                    ObservableList<Analysis> analyses = DAOController.getAnalysisDAO().searchAnalysis(query);
                    Platform.runLater(() -> analysesTable.setItems(analyses));
                } catch (SQLException e) {
                    logger.error(e);
                    Platform.runLater(() -> Message.error(e.getMessage(), e));
                }
            }
            return 0;
        });
    }


    private void updateResultCount() {
        Object[] arguments = {analysesTable.getItems().size()};
        resultCountLb.setText(BundleFormatter.format("searchanalysesdialog.lb.resultsCount", arguments));
    }
}
