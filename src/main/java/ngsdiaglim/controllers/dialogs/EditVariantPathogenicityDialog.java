package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.ValidateVariantPathogenicityTableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.VariantPathogenicity;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class EditVariantPathogenicityDialog extends DialogPane.Dialog<Variant> {

    private final Logger logger = LogManager.getLogger(EditVariantPathogenicityDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private ListView<ACMG> acmgLv;
    @FXML private TextArea commentaryTa;
    @FXML private TableView<VariantPathogenicity> historyTable;
    @FXML private TableColumn<VariantPathogenicity, ACMG> pathogenicityCol;
    @FXML private TableColumn<VariantPathogenicity, String> userCol;
    @FXML private TableColumn<VariantPathogenicity, LocalDateTime> dateCol;
    @FXML private TableColumn<VariantPathogenicity, String> verifiedUserCol;
    @FXML private TableColumn<VariantPathogenicity, LocalDateTime> verifiedDateCol;
    @FXML private TableColumn<VariantPathogenicity, String> commentaryCol;
    @FXML private TableColumn<VariantPathogenicity, Void> actionsCol;

    public EditVariantPathogenicityDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/EditVariantPathogenicityDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setTitle(App.getBundle().getString("editpathogenicitydialog.title"));
        setContent(dialogContainer);
    }

    @FXML
    public void initialize() {
        initACMGListView();
        initPathogenicityHistoryTable();

        valueProperty().addListener(((observable, oldValue, newValue) -> {
            fillPathogenicityHistoryTable();
            selectACMG();
            commentaryTa.setText(null);
        }));
    }

    private void initACMGListView() {
        acmgLv.getItems().addAll(
                ACMG.PATHOGENIC,
                ACMG.LIKELY_PATHOGENIC,
                ACMG.UNCERTAIN_SIGNIGICANCE,
                ACMG.LIKELY_BENIN,
                ACMG.BENIN
        );
    }

    private void initPathogenicityHistoryTable() {
        pathogenicityCol.setCellValueFactory(data -> data.getValue().acmgProperty());
        userCol.setCellValueFactory(data -> data.getValue().userNameProperty());
        dateCol.setCellValueFactory(data -> data.getValue().dateTimeProperty());
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

        verifiedUserCol.setCellValueFactory(data -> data.getValue().verifiedUsernameProperty());
        verifiedDateCol.setCellValueFactory(data -> data.getValue().verifiedDateTimeProperty());
        verifiedDateCol.setCellFactory(column -> new TableCell<>() {
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
        commentaryCol.setCellValueFactory(data -> data.getValue().commentaryProperty());

        actionsCol.setCellFactory(data -> new ValidateVariantPathogenicityTableCell());
    }

    private void fillPathogenicityHistoryTable() {
        try {
            if (getValue() != null && getValue().getPathogenicityHistory() != null) {
                historyTable.setItems(getValue().getPathogenicityHistory().getHistory());
            }
            else {
                historyTable.setItems(null);
            }
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    private void selectACMG() {
        acmgLv.getSelectionModel().select(getValue() != null ? getValue().getAcmg() : null);
    }

    @FXML
    private void editPathogenicity() {
        String error = checkError();
        if (error != null) {
            Message.error(error);
        } else {
            User user = App.get().getLoggedUser();
            long validateUserId = -1;
            String validateUserName = null;
            LocalDateTime validateDate = null;
            LocalDateTime currentDateTime = LocalDateTime.now();
            ACMG acmg = acmgLv.getSelectionModel().getSelectedItem();
            if (user.isPermitted(PermissionsEnum.VALIDATE_VARIANT_PATHOGENICITY)) {
                validateUserId = user.getId();
                validateUserName = user.getUsername();
                validateDate = currentDateTime;
            }
            VariantPathogenicity vp = new VariantPathogenicity(
                    getValue().getId(),
                    acmg,
                    user.getId(),
                    user.getUsername(),
                    currentDateTime,
                    validateUserId,
                    validateUserName,
                    validateDate,
                    commentaryTa.getText()
            );
            try {
                DAOController.getVariantPathogenicityDAO().addVariantPathogenicity(vp);
                getValue().setAcmg(acmg);
                getValue().setPathogenicityConfirmed(user.isPermitted(PermissionsEnum.VALIDATE_VARIANT_PATHOGENICITY));
                DAOController.getVariantsDAO().updateVariant(getValue());
                getValue().loadPathogenicityHistory();
                historyTable.refresh();

                // refresh analysis variant table
                ModuleManager.getAnalysisViewController().getVariantsViewController().refreshTable();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
                Message.error(e.getMessage(), e);
            }
        }
    }

    private String checkError() {
        if (acmgLv.getSelectionModel().getSelectedItem() == null) {
            return App.getBundle().getString("editpathogenicitydialog.msg.err.emptyACMG");
        } else if(StringUtils.isBlank(commentaryTa.getText())) {
            return App.getBundle().getString("editpathogenicitydialog.msg.err.emptyComment");
        } else if (acmgLv.getSelectionModel().getSelectedItem().equals(getValue().getAcmg())) {
            return App.getBundle().getString("editpathogenicitydialog.msg.err.sameACMG");
        }
        else {
            return null;
        }
    }

}
