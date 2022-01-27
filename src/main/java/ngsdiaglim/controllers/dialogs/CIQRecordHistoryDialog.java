package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.ciq.CIQRecordCommentCell;
import ngsdiaglim.controllers.cells.ciq.CIQRecordHistoryCommentCell;
import ngsdiaglim.enumerations.CIQRecordState;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import ngsdiaglim.utils.DateFormatterUtils;
import ngsdiaglim.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;

public class CIQRecordHistoryDialog  extends DialogPane.Dialog<CIQVariantRecord> {

    private static final Logger logger = LogManager.getLogger(CIQRecordHistoryDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TextField hotspotTf;
    @FXML private TextField runTf;
    @FXML private TextField analyseTf;
    @FXML private TextField vafTf;
    @FXML private TextField dpTf;
    @FXML private TextField aoTf;
    @FXML private TableView<CIQRecordHistory> historyTable;
    @FXML private TableColumn<CIQRecordHistory, String> validatorCol;
    @FXML private TableColumn<CIQRecordHistory, LocalDateTime> dateCol;
    @FXML private TableColumn<CIQRecordHistory, CIQRecordState> oldStateCol;
    @FXML private TableColumn<CIQRecordHistory, CIQRecordState> newStateCol;
    @FXML private TableColumn<CIQRecordHistory, String> commentCol;
    @FXML private TableColumn<CIQRecordHistory, Float> meanCol;
    @FXML private TableColumn<CIQRecordHistory, Float> sdCol;

    public CIQRecordHistoryDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);

        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CIQRecordHistoryDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (
                IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("ciqhistorydialog.title"));
        setContent(dialogContainer);

        initView();
        valueProperty().addListener((obs, oldV, newV) -> updateView());
    }

    private void initView() {
        validatorCol.setCellValueFactory(data -> data.getValue().usernameProperty());
        dateCol.setCellValueFactory(data -> data.getValue().dateTimeProperty());
        dateCol.setCellFactory(data -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setTitle(null);
                } else {
                    setText(DateFormatterUtils.formatLocalDateTime(item));
                }
            }
        });
        oldStateCol.setCellValueFactory(data -> data.getValue().oldStateProperty());
        newStateCol.setCellValueFactory(data -> data.getValue().newStateProperty());
        commentCol.setCellValueFactory(data -> data.getValue().commentProperty());
        commentCol.setCellFactory(data -> new CIQRecordHistoryCommentCell());
        meanCol.setCellValueFactory(data -> data.getValue().meanProperty().asObject());
        sdCol.setCellValueFactory(data -> data.getValue().sdProperty().asObject());
    }

    private void updateView() {
        if (getValue() != null) {
            hotspotTf.setText(getValue().getHotspot().getName());
            runTf.setText(getValue().getAnalysis().getRun().getName());
            analyseTf.setText(getValue().getAnalysis().getName());
            vafTf.setText(String.valueOf(NumberUtils.round(getValue().getVaf(), 3)));
            dpTf.setText(String.valueOf(getValue().getDp()));
            aoTf.setText(String.valueOf(getValue().getAo()));
            historyTable.getItems().setAll(getValue().getHistory());
        } else {
            clearView();
        }
    }

    private void clearView() {
        hotspotTf.setText(null);
        runTf.setText(null);
        analyseTf.setText(null);
        vafTf.setText(null);
        dpTf.setText(null);
        aoTf.setText(null);
        historyTable.getItems().clear();
    }
}
