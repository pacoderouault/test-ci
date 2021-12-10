package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.ValidateVariantPathogenicityTableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.SangerState;
import ngsdiaglim.modeles.analyse.SangerCheck;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.VariantPathogenicity;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EditSangerStateDialog extends DialogPane.Dialog<Annotation> {

    private final Logger logger = LogManager.getLogger(EditSangerStateDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private ListView<SangerState> sangerStateLv;
    @FXML private TextArea commentaryTa;
    @FXML private TableView<SangerCheck> historyTable;
    @FXML private TableColumn<SangerCheck, SangerState> stateCol;
    @FXML private TableColumn<SangerCheck, String> userCol;
    @FXML private TableColumn<SangerCheck, LocalDateTime> dateCol;
    @FXML private TableColumn<SangerCheck, String> commentaryCol;

    public EditSangerStateDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/EditSangerStateDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setTitle(App.getBundle().getString("ditsangerstatedialog.title"));
        setContent(dialogContainer);
    }

    @FXML
    public void initialize() {
        initSangerStateListView();
        initHistoryTable();

        valueProperty().addListener(((observable, oldValue, newValue) -> {
            fillHistoryTable();
            selectState();
            commentaryTa.setText(null);
        }));
    }

    private void initSangerStateListView() {
        sangerStateLv.getItems().addAll(
                SangerState.NONE,
                SangerState.ON_DEMAND,
                SangerState.ON_PROGRESS,
                SangerState.COMPLETE_NEGATIVE,
                SangerState.COMPLETE_POSITIVE
        );
    }

    private void initHistoryTable() {
        stateCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getState()));
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserName()));
        dateCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDateTime()));
        dateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(DateFormatterUtils.formatLocalDateTime(item));
                }
            }
        });
        commentaryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getComment()));
    }

    private void fillHistoryTable() {
        if (getValue() != null && getValue().getSangerState() != null) {
            historyTable.setItems(getValue().getSangerState().getSangerChecks());
        }
        else {
            historyTable.setItems(null);
        }
    }

    private void selectState() {
        sangerStateLv.getSelectionModel().select(getValue() != null && getValue().getSangerState() != null && getValue().getSangerState().getLastState() != null? getValue().getSangerState().getLastState().getState() : null);
    }


    @FXML
    private void editSangerState() {
        String error = checkError();
        if (error != null) {
            Message.error(error);
        } else {
            User user = App.get().getLoggedUser();
            if (user.isPermitted(PermissionsEnum.ADD_SANGER_CHECK)) {
                try {
                    System.out.println(getValue());
                    System.out.println(getValue().getVariant());
                    System.out.println(getValue().getVariant().getId());
                    DAOController.get().getSangerStateDAO().addSangerState(
                            getValue().getVariant().getId(),
                            ModuleManager.getAnalysisViewController().getAnalysis().getId(),
                            sangerStateLv.getSelectionModel().getSelectedItem(),
                            commentaryCol.getText()
                    );
                    getValue().setSangerState(DAOController.get().getSangerStateDAO().getSangerChecks(
                            getValue(),
                            ModuleManager.getAnalysisViewController().getAnalysis().getId()));
                    fillHistoryTable();
                } catch (SQLException e) {
                    logger.error(e);
                    Message.error(e.getMessage(), e);
                }
            }
        }
    }


    private String checkError() {
        if (sangerStateLv.getSelectionModel().getSelectedItem() == null) {
            return App.getBundle().getString("ditsangerstatedialog.msg.err.emptyState");
        } else if(StringUtils.isBlank(commentaryTa.getText())) {
            return App.getBundle().getString("ditsangerstatedialog.msg.err.emptyComment");
        }
        else {
            return null;
        }
    }
}


