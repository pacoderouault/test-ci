package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.ValidateVariantFalsePositiveTableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.FalsePositiveVariantEnum;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.modeles.variants.VariantFalsePositive;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EditVariantFalsePositiveDialog  extends DialogPane.Dialog<Variant> {

    private final static Logger logger = LogManager.getLogger(EditVariantPathogenicityDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private ComboBox<FalsePositiveVariantEnum> falsePositiveCb;
    @FXML private TextArea commentaryTa;
    @FXML private Button editFalsePositiveBtn;
    @FXML private TableView<VariantFalsePositive> historyTable;
    @FXML private TableColumn<VariantFalsePositive, Boolean> falsePositiveCol;
    @FXML private TableColumn<VariantFalsePositive, String> userCol;
    @FXML private TableColumn<VariantFalsePositive, LocalDateTime> dateCol;
    @FXML private TableColumn<VariantFalsePositive, String> verifiedUserCol;
    @FXML private TableColumn<VariantFalsePositive, LocalDateTime> verifiedDateCol;
    @FXML private TableColumn<VariantFalsePositive, String> commentaryCol;
    @FXML private TableColumn<VariantFalsePositive, Void> actionsCol;

    public EditVariantFalsePositiveDialog(DialogPane pane) {

        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/EditVariantFalsePositive.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setTitle(App.getBundle().getString("editfalsepositivedialog.title"));
        setContent(dialogContainer);
    }

    @FXML
    public void initialize() {
        initFalsePositiveCb();
        initFalsePositiveHistoryTable();

        valueProperty().addListener(((observable, oldValue, newValue) -> {
            fillFalsePositiveHistoryTable();
            selectFalsePositive();
            commentaryTa.setText(null);
        }));

        editFalsePositiveBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.EDIT_VARIANT_PATHOGENICITY));
    }


    private void initFalsePositiveCb() {
        falsePositiveCb.getItems().addAll(FalsePositiveVariantEnum.VP, FalsePositiveVariantEnum.FP);
        Callback<ListView<FalsePositiveVariantEnum>, ListCell<FalsePositiveVariantEnum>> cellFactory = new Callback<>() {
            @Override
            public ListCell<FalsePositiveVariantEnum> call(ListView<FalsePositiveVariantEnum> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(FalsePositiveVariantEnum item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(null);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getValueName());
                        }
                    }
                };
            }
        };
        falsePositiveCb.setCellFactory(cellFactory);
    }


    private void initFalsePositiveHistoryTable() {
        falsePositiveCol.setCellValueFactory(data -> data.getValue().falsePositiveProperty());
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
        commentaryCol.setCellFactory(tc -> {
            TableCell<VariantFalsePositive, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(commentaryCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        commentaryCol.setCellValueFactory(data -> data.getValue().commentaryProperty());
        actionsCol.setCellFactory(data -> new ValidateVariantFalsePositiveTableCell());
    }


    private void fillFalsePositiveHistoryTable() {
        try {
            if (getValue() != null && getValue().getFalsePositiveHistory() != null) {
                historyTable.setItems(getValue().getFalsePositiveHistory().getHistory());
            }
            else {
                historyTable.setItems(null);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage(), e);
        }
    }

    private void selectFalsePositive() {
        if (getValue() != null) {
            if (getValue().isFalsePositive()) {
                falsePositiveCb.getSelectionModel().select(FalsePositiveVariantEnum.FP);
            } else {
                falsePositiveCb.getSelectionModel().select(FalsePositiveVariantEnum.VP);
            }
        }
    }

    @FXML
    private void editVariant() {
        String error = checkError();
        if (error != null) {
            Message.error(error);
        } else {
            User user = App.get().getLoggedUser();
            long validateUserId = -1;
            String validateUserName = null;
            LocalDateTime validateDate = null;
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (user.isPermitted(PermissionsEnum.VALIDATE_VARIANT_FALSE_POSITIVE)) {
                validateUserId = user.getId();
                validateUserName = user.getUsername();
                validateDate = currentDateTime;
            }
            boolean falsePositive = falsePositiveCb.getValue().getValue();
            VariantFalsePositive vfp = new VariantFalsePositive(
                    getValue().getId(),
                    falsePositive,
                    user.getId(),
                    user.getUsername(),
                    currentDateTime,
                    validateUserId,
                    validateUserName,
                    validateDate,
                    commentaryTa.getText()
            );
            try {
                DAOController.getVariantFalsePositiveDAO().addVariantFalsePositive(vfp);
                getValue().setFalsePositive(falsePositive);
                getValue().setFalsePositiveConfirmed(user.isPermitted(PermissionsEnum.VALIDATE_VARIANT_FALSE_POSITIVE));
                DAOController.getVariantsDAO().updateVariant(getValue());
                getValue().loadFalsePositiveHistory();
                historyTable.refresh();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
                Message.error(e.getMessage(), e);
            }
        }
    }

    private String checkError() {
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.EDIT_VARIANT_PATHOGENICITY)) {
            return App.getBundle().getString("app.msg.err.nopermit");
        }
        else if (falsePositiveCb.getSelectionModel().getSelectedItem() == null) {
            return App.getBundle().getString("editfalsepositivedialog.msg.err.emptyFp");
        } else if(StringUtils.isBlank(commentaryTa.getText())) {
            return App.getBundle().getString("editfalsepositivedialog.msg.err.emptyComment");
        } else if (falsePositiveCb.getSelectionModel().getSelectedItem().getValue() == getValue().isFalsePositive()) {
            return App.getBundle().getString("editfalsepositivedialog.msg.err.sameFalsePositive");
        }
        else {
            return null;
        }
    }
}
