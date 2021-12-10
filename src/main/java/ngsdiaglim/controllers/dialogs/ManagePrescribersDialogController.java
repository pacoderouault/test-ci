package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.PrescriberStateCell;
import ngsdiaglim.controllers.cells.TextAreaTableCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.Prescriber;
import ngsdiaglim.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;

public class ManagePrescribersDialogController extends DialogPane.Dialog<Void> {

    private final static Logger logger = LogManager.getLogger(ManagePrescribersDialogController.class);

    @FXML private VBox dialogContainer;
    @FXML private HBox headerBtnContainer;
    @FXML private TableView<Prescriber> prescribersTable;
    @FXML private TableColumn<Prescriber, Prescriber.PrescriberState> stateCol;
    @FXML private TableColumn<Prescriber, String> statusCol;
    @FXML private TableColumn<Prescriber, String> firstNameCol;
    @FXML private TableColumn<Prescriber, String> lastNameCol;
    @FXML private TableColumn<Prescriber, String> addressCol;
    @FXML private TableColumn<Prescriber, Void> actionsCol;
    private final CustomTextField searchTf = new CustomTextField();
    private final FontIcon clearSearchFi = new FontIcon("mdi-close-circle");
    private final ObservableList<Prescriber> prescribersList = FXCollections.observableArrayList();
    private final FilteredList<Prescriber> viewvablePrescribers = new FilteredList<>(prescribersList);
//    private Prescriber p = null;

    public ManagePrescribersDialogController() {

        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INFORMATION);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ManagePrescribersDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setContent(dialogContainer);
        initView();
    }

    private void initView() {
        initSearchTf();
        initTable();

    }

    private void initSearchTf() {
        searchTf.setLeft(new FontIcon("mdi-magnify"));
        searchTf.setRight(clearSearchFi);
        clearSearchFi.visibleProperty().bind(searchTf.textProperty().isEmpty().not());
        clearSearchFi.setCursor(Cursor.HAND);
        clearSearchFi.setOnMouseClicked(e -> searchTf.setText(""));
        searchTf.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable();
        });
    }

    private void initTable() {
        prescribersTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        prescribersTable.getSelectionModel().setCellSelectionEnabled(true);
        prescribersTable.setEditable(true);

        stateCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getState()));
        stateCol.setCellFactory(c -> new PrescriberStateCell<>());

        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        statusCol.setCellFactory(TextFieldTableCell.forTableColumn());
        statusCol.setOnEditCommit(t -> {
            Prescriber prescriber = t.getRowValue();
            if (prescriber != null) {
                prescriber.setStatus(t.getNewValue());
                editPrescriber(prescriber);
                prescribersTable.refresh();
            }
        });

        firstNameCol.setCellValueFactory(data -> data.getValue().firstNameProperty());
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(t -> {
            Prescriber prescriber = t.getRowValue();
            if (prescriber != null) {
                prescriber.setFirstName(t.getNewValue());
                editPrescriber(prescriber);
                prescribersTable.refresh();
            }
        });

        lastNameCol.setCellValueFactory(data -> data.getValue().lastNameProperty());
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(t -> {
            Prescriber prescriber = t.getRowValue();
            if (prescriber != null) {
                prescriber.setLastName(t.getNewValue());
                editPrescriber(prescriber);
                prescribersTable.refresh();
            }
        });

        addressCol.setCellValueFactory(data -> data.getValue().addressProperty());
        addressCol.setCellFactory(TextAreaTableCell.forTableColumn());
        addressCol.setOnEditCommit(t -> {
            Prescriber prescriber = t.getRowValue();
            if (prescriber != null) {
                prescriber.setAddress(t.getNewValue());
                editPrescriber(prescriber);
                prescribersTable.refresh();
            }
        });

        actionsCol.setCellFactory((tableColumn) -> new TableCell<>() {
            private final Button btn = new Button("", new FontIcon("mdi-delete-circle"));
            {
                btn.getStyleClass().add("btn-icon");
                btn.setOnAction(e -> {
                    DialogPane.Dialog<ButtonType> dialog = Message.confirm(App.getBundle().getString("manageprescriberdialog.msg.conf.deletePrescriber"));
                    dialog.getButton(ButtonType.YES).setOnAction(event -> {
                        Prescriber prescriber = getTableRow().getItem();
                        if (prescriber != null) {
                            try {
                                DAOController.getPrescriberDAO().deletePrescriber(prescriber.getId());
                                prescribersList.remove(prescriber);
                                Message.hideDialog(dialog);
                            } catch (SQLException ex) {
                                logger.error(ex);
                                Message.error(ex.getMessage(), ex);
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(btn);
                }
            }
        });


        try {
            prescribersTable.setItems(viewvablePrescribers);
            prescribersList.setAll(DAOController.getPrescriberDAO().getPrescribers());
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }


    private void filterTable() {
        String query = searchTf.getText();
        if (query == null || query.isEmpty()) {
            viewvablePrescribers.setPredicate(null);
        }
        else {
            viewvablePrescribers.setPredicate(p -> StringUtils.containsIgnoreCase(p.getLastName(),query) || StringUtils.containsIgnoreCase(p.getFirstName(),query));
        }
    }

    private void editPrescriber(Prescriber p) {
        if (!p.getState().getState().equals(Prescriber.PrescriberState.State.ERROR)) {
            try {
                if (p.getId() > 0) {
                    DAOController.getPrescriberDAO().updatePrescriber(p);
                } else {
                    long id = DAOController.getPrescriberDAO().addPrescriber(p);
                    p.setId(id);
                }
            } catch (SQLException e) {
                logger.error("Error when adding/editing prescriber", e);
                Message.error(e.getMessage());
            }
        }
    }


    @FXML
    private void addPrescriber() {
        Prescriber p = new Prescriber(
                "Dr",
                "prénom",
                "nom",
                "adresse"
        );
        prescribersList.add(0, p);
    }
}
