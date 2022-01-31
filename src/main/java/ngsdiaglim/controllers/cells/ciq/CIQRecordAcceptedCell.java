package ngsdiaglim.controllers.cells.ciq;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.CIQRecordHistoryDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.CIQRecordState;
import ngsdiaglim.modeles.ciq.CIQRecordHistory;
import ngsdiaglim.modeles.ciq.CIQVariantDataSet;
import ngsdiaglim.modeles.ciq.CIQVariantRecord;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.PlatformUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class CIQRecordAcceptedCell extends TableCell<CIQVariantRecord, CIQRecordHistory> {

    private final static Logger logger = LogManager.getLogger(CIQRecordAcceptedCell.class);
    private final HBox box = new HBox();
    private final ComboBox<CIQRecordState> ciqRecordSateCb = new ComboBox<>();
    private final ChangeListener<CIQRecordState> acceptedListener;
    private final static String[] cssClasses = new String[]{"ciq-record-cell-accepted", "ciq-record-cell-notaccepted"};
    private static final Tooltip showHistoryTp = new Tooltip(App.getBundle().getString("ciq.msg.showHistory"));

    public CIQRecordAcceptedCell() {
        box.getStyleClass().add("box-action-cell");

        Button viewHistoryBtn = new Button("", new FontIcon("mdral-history"));
        viewHistoryBtn.setOnAction(e -> viewHistory());
        showHistoryTp.setShowDelay(Duration.ZERO);
        viewHistoryBtn.setTooltip(showHistoryTp);

        ciqRecordSateCb.getItems().setAll(CIQRecordState.ACCEPTED, CIQRecordState.NOT_ACCEPTED);
        acceptedListener = (obs, oldV, newV) -> {
            if (newV != null) {
                PlatformUtils.runAndWait(() -> {
                    setAccepted(oldV, newV);
                    setStyles(newV);
                });

                ModuleManager.getAnalysisViewController().getCiqViewController().getShowedDataset().computeStats();
                ModuleManager.getAnalysisViewController().getCiqViewController().fillHotspotsStatsFields(
                        ModuleManager.getAnalysisViewController().getCiqViewController().getShowedDataset()
                );
                ModuleManager.getAnalysisViewController().getCiqViewController().getChart().drawChart();
            }
        };
        ciqRecordSateCb.getSelectionModel().selectedItemProperty().addListener(acceptedListener);

        box.getChildren().addAll(ciqRecordSateCb, viewHistoryBtn);
    }

    @Override
    protected void updateItem(CIQRecordHistory item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll(cssClasses);
        setText(null);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(box);
            removeCbListener();
            if (item == null || item.getNewState().equals(CIQRecordState.UNKNOWN)) {
                ciqRecordSateCb.getSelectionModel().select(null);
            } else {
                ciqRecordSateCb.getSelectionModel().select(item.getNewState());
                setStyles(item.getNewState());
            }
            addCbListener();

            if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.VALIDATE_CIQ)) {
                ciqRecordSateCb.setDisable(true);
            }
        }
    }


    private void removeCbListener() {
        ciqRecordSateCb.getSelectionModel().selectedItemProperty().removeListener(acceptedListener);
    }

    private void addCbListener() {
        ciqRecordSateCb.getSelectionModel().selectedItemProperty().addListener(acceptedListener);
    }

    private void setAccepted(CIQRecordState oldState, CIQRecordState newState) {
        CIQVariantRecord record = getTableRow().getItem();
        if (record != null) {
            try {

                CIQVariantDataSet dataset = ModuleManager.getAnalysisViewController().getCiqViewController().getShowedDataset();
                long recordHistoryId = DAOController.getCiqRecordHistoryDAO().addCIQRecordHistory(record, oldState, newState, (float)dataset.getMean(), (float)dataset.getSd());
                if (recordHistoryId >= 0) {
                    DialogPane.Dialog<String> dialog = App.get().getAppController().getDialogPane().showTextInput(App.getBundle().getString("ciq.msg.addComment"), null, true);
                    dialog.getButton(ButtonType.OK).setOnAction(e -> {
                        try {
                            addComment(recordHistoryId, dialog.getValue());
                            record.loadHistory();
                            getTableView().refresh();
                            Message.hideDialog(dialog);
                        } catch (SQLException ex) {
                            logger.error(e);
                            Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                        }
                    });
                }
            } catch (SQLException e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
            }

            try {
                record.loadHistory();
            } catch (SQLException e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
            }
            getTableView().refresh();
        }
    }

    private void setStyles(CIQRecordState accepted) {
        if (accepted != null) {
            if (accepted.equals(CIQRecordState.ACCEPTED)) {
                getStyleClass().add(cssClasses[0]);
            } else if (accepted.equals(CIQRecordState.NOT_ACCEPTED)) {
                getStyleClass().add(cssClasses[1]);
            }
        }
    }

    private void addComment(long recordHistoryId, String comment) throws SQLException {
        if (comment != null && !StringUtils.isBlank(comment)) {
            DAOController.getCiqRecordHistoryDAO().addComment(recordHistoryId, comment);
        }
    }

    private void viewHistory() {
        CIQVariantRecord ciqRecord = getTableRow().getItem();
        if (ciqRecord != null) {
            CIQRecordHistoryDialog dialog = new CIQRecordHistoryDialog(App.get().getAppController().getDialogPane());
            dialog.setValue(ciqRecord);
            Message.showDialog(dialog);
        }
    }
}
