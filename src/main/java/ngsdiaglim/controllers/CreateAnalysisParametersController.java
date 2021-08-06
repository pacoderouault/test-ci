package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddPanelDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.parsers.PanelParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MaskerPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateAnalysisParametersController extends Module {

    private final Logger logger = LogManager.getLogger(CreateAnalysisParametersController.class);

    @FXML private TextField analysisNameTf;
    @FXML private Spinner<Integer> minDepthSpinner;
    @FXML private Spinner<Integer> warningDepthSpinner;
    @FXML private Spinner<Double> minVAFSpinner;
    @FXML private ComboBox<Genome> genomesCb;
    @FXML private ComboBox<Panel> panelsCb;

    @FXML private TableView<Panel> panelsTable;
    @FXML private TableColumn<Panel, String> panelsNameCol;
    @FXML private TableColumn<Panel, Integer> panelsSizeCol;
    @FXML private TableColumn<Panel, Boolean> panelsActiveCol;

    @FXML private TableView<PanelRegion> regionsTable;
    @FXML private TableColumn<PanelRegion, String> regionsContigCol;
    @FXML private TableColumn<PanelRegion, Integer> regionsStartCol;
    @FXML private TableColumn<PanelRegion, Integer> regionsEndCol;
    @FXML private TableColumn<PanelRegion, String> regionsNameCol;

    @FXML private TableView<AnalysisParameters> analysisParametersTable;
    @FXML private TableColumn<AnalysisParameters, String> analysisParametersNameCol;
    @FXML private TableColumn<AnalysisParameters, Genome> analysisParametersGenomeCol;
    @FXML private TableColumn<AnalysisParameters, Panel> analysisParametersPanelCol;
    @FXML private TableColumn<AnalysisParameters, Integer> analysisParametersMinDepthCol;
    @FXML private TableColumn<AnalysisParameters, Integer> analysisParametersWarningDepthCol;
    @FXML private TableColumn<AnalysisParameters, Float> analysisParametersMinVafCol;
    @FXML private TableColumn<AnalysisParameters, Boolean> analysisParametersActiveCol;
    private final MaskerPane progressPane = new MaskerPane();
    private final Tooltip inactivePanelTooltip = new Tooltip(App.getBundle().getString("createAnalasisParameters.tooltip.inactivePanel"));

    public CreateAnalysisParametersController() {
        super(App.getBundle().getString("createAnalasisParameters.title"));
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CreateAnalysisParameters.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
            logger.error("Problem when loading the create analysis panel", e);
        }
    }


    @FXML
    private void initialize() {
        genomesCb.getItems().addAll(Genome.GRCh37, Genome.GRCh38);
        genomesCb.getSelectionModel().select(Genome.GRCh37);
        initMinDepthSpinner();
        initWarningDepthSpinner();
        initMinVAFSpinner();
        initPanelsCombobox();
        initPanelsTable();
        initRegionsTable();
        initAnalysisParametersTable();
        try {
            loadPanels();
        } catch (SQLException e) {
            logger.error("Error when loading panel", e);
            Message.error(e.getMessage(), e);
        }

        try {
            loadAnalysisParameters();
        } catch (SQLException e) {
            logger.error("Error when loading analysis parameters", e);
            Message.error(e.getMessage(), e);
        }


    }


    private void initMinDepthSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 30);
        minDepthSpinner.setValueFactory(valueFactory);
    }


    private void initWarningDepthSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 50);
        warningDepthSpinner.setValueFactory(valueFactory);
    }


    private void initMinVAFSpinner() {
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0d, 1d, 0.1, 0.05);
        minVAFSpinner.setValueFactory(valueFactory);
    }

    private void initPanelsCombobox() {
        panelsCb.setItems(panelsTable.getItems().filtered(Panel::isActive));
//        panelsTable.getItems().addListener((ListChangeListener<Panel>) c -> {
//
//        });
    }

    private String checkPanelName(String name) {
        if (StringUtils.isBlank(analysisNameTf.getText())) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyname");
        } else {
            try {
                if (DAOController.get().getAnalysisParametersDAO().analysisParametersExists(analysisNameTf.getText())) {
                    return App.getBundle().getString("createAnalasisParameters.msg.err.analysisnameExists");
                }
            } catch (SQLException e) {
                logger.error("Error when checking analysis parameters name", e);
                return e.getMessage();
            }
        }
        return null;
    }

    private String checkError() {

        String errorName = checkAnalysisParametersName(analysisNameTf.getText());
        if (errorName != null) return errorName;

        String minDepthError = checkMinDepthError(minDepthSpinner.getValue());
        if (minDepthError != null) return minDepthError;

        String warningDepthError = checkWarningDepthError(warningDepthSpinner.getValue());
        if (warningDepthError != null) return warningDepthError;

        String minVafError = checkMinVafError(minVAFSpinner.getValue());
        if (minVafError != null) return minVafError;

        String genomeError = checkGenomeError(genomesCb.getValue());
        if (genomeError != null) return genomeError;

        return checkPanelError(panelsCb.getValue());
    }


    private String checkAnalysisParametersName(String name) {
        if (StringUtils.isBlank(name)) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyname");
        }
        else {
            try {
                if (DAOController.get().getAnalysisParametersDAO().analysisParametersExists(name)) {
                    return App.getBundle().getString("createAnalasisParameters.msg.err.analysisnameExists");
                }
            } catch (SQLException e) {
                logger.error("Error when checking analysis parameters name", e);
                return e.getMessage();
            }
        }
        return null;
    }


    private String checkMinDepthError(Object value) {
        if (value == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyMinDepth");
        }
        else if (!NumberUtils.isInt(value)) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.notIntMinDepth");
        }
        return null;
    }

    private String checkWarningDepthError(Object value) {
        if (value == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptywarningDepth");
        }
        else if (!NumberUtils.isInt(value)) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.notIntWarningDepth");
        }
        return null;
    }

    private String checkMinVafError(Object value) {
        if (value == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyVAF");
        }
        else if (!NumberUtils.isFloat(value)) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.notFloatVAF");
        }
        return null;
    }

    private String checkGenomeError(Genome genome) {
        if (genomesCb.getValue() == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyGenome");
        }
        return null;
    }

    private String checkPanelError(Panel panel) {
        if (panelsCb.getValue() == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyPanel");
        }
        return null;
    }

    private void initPanelsTable() {
        panelsTable.setEditable(true);
        panelsNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        panelsNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        panelsNameCol.setOnEditCommit(t -> {
            if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
                Panel panel = t.getRowValue();
                try {
                    String error = checkPanelName(t.getNewValue());
                    if (error != null) {
                        Message.error(error);
                    } else {
                        DAOController.get().getPanelDAO().updatePanel(panel, t.getNewValue(), panel.isActive());
                        panel.setName(t.getNewValue());
                    }
                } catch (SQLException e) {
                    panel.setName(t.getOldValue());
                    logger.error("Error when editing user name", e);
                    Message.error(e.getMessage(), e);
                }
                panelsTable.refresh();
            }
        });

        panelsSizeCol.setCellValueFactory(data -> {
            try {
                return new SimpleIntegerProperty(data.getValue().getSize()).asObject();
            } catch (SQLException e) {
                logger.error("Error when getting panel size");
                return new SimpleIntegerProperty(-1).asObject();
            }

        });

        panelsActiveCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActive()));
        panelsActiveCol.setCellFactory(p -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<Panel, Boolean> tableCell = new TableCell<>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (empty || item == null)
                        setGraphic(null);
                    else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                    }
                }
            };
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validatePanelActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
    }

    private void validatePanelActivation(CheckBox checkBox, Panel item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getName()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.inactivePanel", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.reactivePanel", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.get().getPanelDAO().updatePanel(item, item.getName(), !item.isActive());
                item.setActive(!checkBox.isSelected());
                checkBox.setSelected(!checkBox.isSelected());
                loadPanels();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }


    private void initRegionsTable() {
        regionsContigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        regionsStartCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        regionsEndCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        regionsNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        panelsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                try {
                    regionsTable.getItems().setAll(newV.getRegions());
                } catch (SQLException e) {
                    logger.error("Error when load panel regions", e);
                    Message.error(e.getMessage(), e);
                }
            }
        });

    }


    private void loadPanels() throws SQLException {
        panelsTable.getItems().setAll(DAOController.get().getPanelDAO().getPanels());
    }

    @FXML
    private void addPanelHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
            AddPanelDialog dialog = new AddPanelDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.isValid() && dialog.getValue() != null) {
                    WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addpaneldialog.msg.loading"));
                    wid.addTaskEndNotification(r -> {
                        if (r == 0) {
                            try {
                                loadPanels();
                                Message.hideDialog(dialog);
                            } catch (SQLException ex) {
                                logger.error("Error when adding panel", ex);
                                Message.error(ex.getMessage(), ex);
                            }
                        }
                    });
                    wid.exec("LoadPanels", inputParam -> {
                        long panelId = -1;
                        try {
                            List<PanelRegion> regions = PanelParser.parsePanel(dialog.getValue().getBedFile());
                            panelId = DAOController.get().getPanelDAO().addPanel(dialog.getValue().getName());
                            for (PanelRegion region : regions) {
                                DAOController.get().getPanelRegionDAO().addRegion(region, panelId);
                            }
                            Thread.sleep(1000);
                        } catch (Exception ex) {
                            logger.error("Error when adding panel", ex);
                            Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                            try {
                                DAOController.get().getPanelDAO().deletePanel(panelId);
                            } catch (SQLException exc) {
                                logger.error("Error when deleting panel", exc);
                                Platform.runLater(() -> Message.error(exc.getMessage(), exc));
                            }
                            return 1;
                        }
                        return 0;
                    });
                }
            });
        }
    }


    private void initAnalysisParametersTable() {
        analysisParametersTable.setEditable(App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
        analysisParametersNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnalysisName()));
        analysisParametersNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        analysisParametersNameCol.setOnEditCommit(t -> {
            AnalysisParameters analysisParameters = t.getRowValue();
            try {
                String error = checkAnalysisParametersName(t.getNewValue());
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(
                            analysisParameters.getId(),
                            t.getNewValue(),
                            analysisParameters.getGenome(),
                            analysisParameters.getMinDepth(),
                            analysisParameters.getWarningDepth(),
                            analysisParameters.getMinVAF(),
                            analysisParameters.getPanel().getId(),
                            analysisParameters.isActive());
                    analysisParameters.setAnalysisName(t.getNewValue());
                }
            } catch (SQLException e) {
                analysisParameters.setAnalysisName(t.getOldValue());
                logger.error("Error when editing analysis parameters name", e);
                Message.error(e.getMessage(), e);
            }
            panelsTable.refresh();

        });

        analysisParametersGenomeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGenome()));
        analysisParametersGenomeCol.setCellFactory(ComboBoxTableCell.forTableColumn(genomesCb.getItems()));
        analysisParametersGenomeCol.setOnEditCommit(t -> {
            AnalysisParameters analysisParameters = t.getRowValue();
            try {
                String error = checkGenomeError(t.getNewValue());
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(
                            analysisParameters.getId(),
                            analysisParameters.getAnalysisName(),
                            t.getNewValue(),
                            analysisParameters.getMinDepth(),
                            analysisParameters.getWarningDepth(),
                            analysisParameters.getMinVAF(),
                            analysisParameters.getPanel().getId(),
                            analysisParameters.isActive());
                    analysisParameters.setGenome(t.getNewValue());
                }
            } catch (SQLException e) {
                analysisParameters.setGenome(t.getOldValue());
                logger.error("Error when editing analysis parameters genome", e);
                Message.error(e.getMessage(), e);
            }
            panelsTable.refresh();
        });

        analysisParametersPanelCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPanel()));
        analysisParametersPanelCol.setCellFactory(ComboBoxTableCell.forTableColumn(panelsCb.getItems()));
        analysisParametersPanelCol.setOnEditCommit(t -> {
            AnalysisParameters analysisParameters = t.getRowValue();
            try {
                String error = checkPanelError(t.getNewValue());
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(
                            analysisParameters.getId(),
                            analysisParameters.getAnalysisName(),
                            analysisParameters.getGenome(),
                            analysisParameters.getMinDepth(),
                            analysisParameters.getWarningDepth(),
                            analysisParameters.getMinVAF(),
                            t.getNewValue().getId(),
                            analysisParameters.isActive());
                    analysisParameters.setPanel(t.getNewValue());
                }
            } catch (SQLException e) {
                analysisParameters.setPanel(t.getOldValue());
                logger.error("Error when editing analysis parameters panel", e);
                Message.error(e.getMessage(), e);
            }
            panelsTable.refresh();
        });

        analysisParametersMinDepthCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMinDepth()).asObject());
        analysisParametersMinDepthCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        analysisParametersMinDepthCol.setOnEditCommit(t -> {
            AnalysisParameters analysisParameters = t.getRowValue();
            try {
                String error = checkMinDepthError(t.getNewValue());
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(
                            analysisParameters.getId(),
                            analysisParameters.getAnalysisName(),
                            analysisParameters.getGenome(),
                            t.getNewValue(),
                            analysisParameters.getWarningDepth(),
                            analysisParameters.getMinVAF(),
                            analysisParameters.getPanel().getId(),
                            analysisParameters.isActive());
                    analysisParameters.setMinDepth(t.getNewValue());
                }
            } catch (SQLException e) {
                analysisParameters.setMinDepth(t.getOldValue());
                logger.error("Error when editing analysis parameters min depth", e);
                Message.error(e.getMessage(), e);
            }
            panelsTable.refresh();
        });


        analysisParametersWarningDepthCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getWarningDepth()).asObject());
        analysisParametersWarningDepthCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        analysisParametersWarningDepthCol.setOnEditCommit(t -> {
            AnalysisParameters analysisParameters = t.getRowValue();
            try {
                String error = checkWarningDepthError(t.getNewValue());
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(
                            analysisParameters.getId(),
                            analysisParameters.getAnalysisName(),
                            analysisParameters.getGenome(),
                            analysisParameters.getMinDepth(),
                            t.getNewValue(),
                            analysisParameters.getMinVAF(),
                            analysisParameters.getPanel().getId(),
                            analysisParameters.isActive());
                    analysisParameters.setWarningDepth(t.getNewValue());
                }
            } catch (SQLException e) {
                analysisParameters.setWarningDepth(t.getOldValue());
                logger.error("Error when editing analysis parameters min depth", e);
                Message.error(e.getMessage(), e);
            }
            panelsTable.refresh();
        });



        analysisParametersMinVafCol.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getMinVAF()).asObject());
        analysisParametersMinVafCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        analysisParametersMinVafCol.setOnEditCommit(t -> {

            AnalysisParameters analysisParameters = t.getRowValue();
            try {
                String error = checkMinVafError(t.getNewValue());
                if (error != null) {
                    Message.error(error);
                } else {
                    DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(
                            analysisParameters.getId(),
                            analysisParameters.getAnalysisName(),
                            analysisParameters.getGenome(),
                            analysisParameters.getMinDepth(),
                            analysisParameters.getWarningDepth(),
                            t.getNewValue(),
                            analysisParameters.getPanel().getId(),
                            analysisParameters.isActive());
                    analysisParameters.setMinVAF(t.getNewValue());
                }
            } catch (SQLException e) {
                analysisParameters.setMinVAF(t.getOldValue());
                logger.error("Error when editing analysis parameters min depth", e);
                Message.error(e.getMessage(), e);
            }
            panelsTable.refresh();
        });


        analysisParametersActiveCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActive()));
        analysisParametersActiveCol.setCellFactory(p -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<AnalysisParameters, Boolean> tableCell = new TableCell<>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (empty || item == null)
                        setGraphic(null);
                    else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                    }
                }
            };
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validateAnalysisParametersActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
    }

    private void validateAnalysisParametersActivation(CheckBox checkBox, AnalysisParameters item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getAnalysisName()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.inactiveAnalysisParameters", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.reactiveAnalysisParameters", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.get().getAnalysisParametersDAO().updateAnalysisParameters(item.getId(),
                        item.getAnalysisName(),
                        item.getGenome(),
                        item.getMinDepth(),
                        item.getWarningDepth(),
                        item.getMinVAF(),
                        item.getPanel().getId(),
                        !item.isActive());
                item.setActive(!item.isActive());
                checkBox.setSelected(!checkBox.isSelected());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }


    private void loadAnalysisParameters() throws SQLException {
        analysisParametersTable.getItems().setAll(DAOController.get().getAnalysisParametersDAO().getAnalysisParameters());
    }


    @FXML
    private void createAnalysisParametersHandler() {
        String error = checkError();
        if(error == null) {
            try {
                DAOController.get().getAnalysisParametersDAO().addAnalysisParameters(
                        analysisNameTf.getText(),
                        genomesCb.getValue(),
                        minDepthSpinner.getValue(),
                        warningDepthSpinner.getValue(),
                        minVAFSpinner.getValue(),
                        panelsCb.getValue().getId()
                );
                loadAnalysisParameters();
                clearAddAnalysisParametersForm();
            } catch (SQLException e) {
                Message.error(e.getMessage(), e);
            }
        }
        else {
            Message.error(error);
        }
    }

    private void clearAddAnalysisParametersForm() {
        analysisNameTf.setText(null);
        panelsCb.getSelectionModel().select(null);
    }
}
