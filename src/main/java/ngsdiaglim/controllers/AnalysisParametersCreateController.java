package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.AnalysisParametersActionsCol;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageSet;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.modeles.variants.HotspotsSet;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class AnalysisParametersCreateController extends VBox {

    private static final Logger logger = LogManager.getLogger(AnalysisParametersCreateController.class);

    @FXML private TextField analysisNameTf;
    @FXML private Spinner<Integer> minDepthSpinner;
    @FXML private Spinner<Integer> warningDepthSpinner;
    @FXML private Spinner<Double> minVAFSpinner;
    @FXML private ComboBox<Genome> genomesCb;
    @FXML private ComboBox<Panel> panelsCb;
    @FXML private ComboBox<GeneSet> geneSetsCb;
    @FXML private ComboBox<SpecificCoverageSet> specificCoverageCb;
    @FXML private ComboBox<HotspotsSet> hotspotsCb;
    @FXML private ComboBox<TargetEnrichment> targetEnrichmentCb;

    @FXML private TableView<Gene> geneTranscriptTable;
    @FXML private TableColumn<Gene, String> genesCol;
    @FXML private TableColumn<Gene, HashMap<String, Transcript>> transcriptsCol;

    @FXML private TableView<AnalysisParameters> analysisParametersTable;
    @FXML private TableColumn<AnalysisParameters, String> analysisParametersNameCol;
    @FXML private TableColumn<AnalysisParameters, Genome> analysisParametersGenomeCol;
    @FXML private TableColumn<AnalysisParameters, Panel> analysisParametersPanelCol;
    @FXML private TableColumn<AnalysisParameters, GeneSet> analysisParametersGeneSetCol;
    @FXML private TableColumn<AnalysisParameters, SpecificCoverageSet> analysisParametersSpecificCoverageSetCol;
    @FXML private TableColumn<AnalysisParameters, HotspotsSet> analysisParametersHotspotsSetCol;
    @FXML private TableColumn<AnalysisParameters, TargetEnrichment> analysisParametersEnrichmentCol;
    @FXML private TableColumn<AnalysisParameters, Integer> analysisParametersMinDepthCol;
    @FXML private TableColumn<AnalysisParameters, Integer> analysisParametersWarningDepthCol;
    @FXML private TableColumn<AnalysisParameters, Float> analysisParametersMinVafCol;
    @FXML private TableColumn<AnalysisParameters, Boolean> analysisParametersActiveCol;
    @FXML private TableColumn<AnalysisParameters, Void> analysisParametersActionCol;

    private final CreateAnalysisParametersController createAnalysisParametersController;

    public AnalysisParametersCreateController(CreateAnalysisParametersController createAnalysisParametersController) {
        this.createAnalysisParametersController = createAnalysisParametersController;
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisParametersCreate.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        initView();
    }

    private void initView() {
        genomesCb.getItems().addAll(Genome.GRCh37, Genome.GRCh38);
        genomesCb.getSelectionModel().select(Genome.GRCh37);
        targetEnrichmentCb.getItems().addAll(TargetEnrichment.AMPLICON, TargetEnrichment.CAPTURE);
        targetEnrichmentCb.getSelectionModel().select(TargetEnrichment.CAPTURE);
        initMinDepthSpinner();
        initWarningDepthSpinner();
        initMinVAFSpinner();
        initPanelsCombobox();
        initGeneSetsCombobox();
        initSpecificCoverageCombobox();
        initHotspotsSetCombobox();
//        initCIQCombobox();
        initAnalysisParametersTable();

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
        panelsCb.setItems(createAnalysisParametersController
                .getAnalysisParametersPanelsController().getPanelsTable().getItems().filtered(Panel::isActive));
    }

    private void initGeneSetsCombobox() {
        geneSetsCb.setItems(createAnalysisParametersController
                .getAnalysisParametersGenesTrancriptsController().getGenesSetTable().getItems().filtered(GeneSet::isActive));
    }

    private void initSpecificCoverageCombobox() {
        specificCoverageCb.setItems(createAnalysisParametersController
                .getAnalysisParametersSpecificCoverageController().getSpecificCoverageSetTable().getItems().filtered(SpecificCoverageSet::isActive));
    }

    private void initHotspotsSetCombobox() {
        fillHotspotsSetCombobox();
        createAnalysisParametersController
                .getAnalysisParametersHotspotsController()
                .getHotspotsSetTable().getItems().addListener((ListChangeListener<HotspotsSet>) change -> fillHotspotsSetCombobox());
    }

    private void fillHotspotsSetCombobox() {
        hotspotsCb.getItems().clear();
        hotspotsCb.getItems().add(null);
        hotspotsCb.getItems().addAll(
                createAnalysisParametersController.getAnalysisParametersHotspotsController().getHotspotsSetTable().getItems().filtered(HotspotsSet::isActive));
    }

//    private void initCIQCombobox() {
//        fillCIQCombobox();
//        createAnalysisParametersController.getAnalysisParametersCIQController()
//                .getCiqModelTableView().getItems().addListener((ListChangeListener<CIQModel>) change -> fillCIQCombobox());
//    }
//
//    private void fillCIQCombobox() {
//        ciqCb.getItems().clear();
//        ciqCb.getItems().add(null);
//        System.out.println(createAnalysisParametersController);
//        System.out.println(createAnalysisParametersController.getAnalysisParametersCIQController());
//        ciqCb.getItems().addAll(
//                createAnalysisParametersController.getAnalysisParametersCIQController().getCiqModelTableView().getItems().filtered(CIQModel::isActive));
//    }

    private String checkPanelName(String name) {
        if (StringUtils.isBlank(analysisNameTf.getText())) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyname");
        } else {
            try {
                if (DAOController.getAnalysisParametersDAO().analysisParametersExists(analysisNameTf.getText())) {
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

        String genomeError = checkGenomeError();
        if (genomeError != null) return genomeError;

        String targetEnrichmentError = checkTargetEnrichmentError();
        if (targetEnrichmentError != null) return targetEnrichmentError;

        String panelError = checkPanelError();
        if (panelError != null) return panelError;

        return checkGeneTranscriptSetError();
    }


    private String checkAnalysisParametersIdUsed(long id) {
        try {
            if (DAOController.getAnalysisParametersDAO().isUsed(id)) {
                return App.getBundle().getString("createAnalasisParameters.msg.err.parametersUsed");
            }
        } catch (SQLException e) {
            logger.error("Error when checking analysis parameters use", e);
            return e.getMessage();
        }
        return null;
    }


    private String checkAnalysisParametersName(String name) {
        if (StringUtils.isBlank(name)) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyname");
        }
        else {
            try {
                if (DAOController.getAnalysisParametersDAO().analysisParametersExists(name)) {
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

    private String checkGenomeError() {
        if (genomesCb.getValue() == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyGenome");
        }
        return null;
    }

    private String checkTargetEnrichmentError() {
        if (targetEnrichmentCb.getValue() == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyTargetEnrichment");
        }
        return null;
    }

    private String checkPanelError() {
        if (panelsCb.getValue() == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyPanel");
        }
        return null;
    }

    private String checkGeneTranscriptSetError() {
        if (geneSetsCb.getValue() == null) {
            return App.getBundle().getString("createAnalasisParameters.msg.err.emptyGeneSet");
        }
        return null;
    }

    private void initAnalysisParametersTable() {
        analysisParametersTable.setEditable(false);
        analysisParametersNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnalysisName()));
        analysisParametersNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        analysisParametersNameCol.setOnEditStart(t -> {
//            String error = checkAnalysisParametersIdUsed(t.getRowValue().getId());
//            if (error != null) {
//                t.getTableView().edit(-1, null);
//                Message.error(error);
//            }
//        });
//        analysisParametersNameCol.setOnEditCommit(t -> {
//            AnalysisParameters analysisParameters = t.getRowValue();
//            try {
//                String error = checkAnalysisParametersName(t.getNewValue());
//                if (error != null) {
//                    analysisParameters.setAnalysisName(t.getOldValue());
//                    Message.error(error);
//                } else {
//                    DAOController.getAnalysisParametersDAO().updateAnalysisParameters(
//                            analysisParameters.getId(),
//                            t.getNewValue(),
//                            analysisParameters.getGenome(),
//                            analysisParameters.getMinDepth(),
//                            analysisParameters.getWarningDepth(),
//                            analysisParameters.getMinVAF(),
//                            analysisParameters.getPanel().getId(),
//                            analysisParameters.isActive());
//                    analysisParameters.setAnalysisName(t.getNewValue());
//                }
////                }
//            } catch (SQLException e) {
//                analysisParameters.setAnalysisName(t.getOldValue());
//                logger.error("Error when editing analysis parameters name", e);
//                Message.error(e.getMessage(), e);
//            }
//            panelsTable.refresh();
//
//        });

        analysisParametersGenomeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGenome()));
//        analysisParametersGenomeCol.setCellFactory(ComboBoxTableCell.forTableColumn(genomesCb.getItems()));
//        analysisParametersGenomeCol.setOnEditStart(t -> {
//            String error = checkAnalysisParametersIdUsed(t.getRowValue().getId());
//            if (error != null) {
//                t.getTableView().edit(-1, null);
//                Message.error(error);
//            }
//        });
//        analysisParametersGenomeCol.setOnEditCommit(t -> {
//            AnalysisParameters analysisParameters = t.getRowValue();
//            try {
//                String error = checkGenomeError(t.getNewValue());
//                if (error != null) {
//                    analysisParameters.setGenome(t.getOldValue());
//                    Message.error(error);
//                } else {
//                    DAOController.getAnalysisParametersDAO().updateAnalysisParameters(
//                            analysisParameters.getId(),
//                            analysisParameters.getAnalysisName(),
//                            t.getNewValue(),
//                            analysisParameters.getMinDepth(),
//                            analysisParameters.getWarningDepth(),
//                            analysisParameters.getMinVAF(),
//                            analysisParameters.getPanel().getId(),
//                            analysisParameters.isActive());
//                    analysisParameters.setGenome(t.getNewValue());
//                }
//            } catch (SQLException e) {
//                analysisParameters.setGenome(t.getOldValue());
//                logger.error("Error when editing analysis parameters genome", e);
//                Message.error(e.getMessage(), e);
//            }
//            panelsTable.refresh();
//        });

        analysisParametersPanelCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPanel()));
        analysisParametersGeneSetCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGeneSet()));
        analysisParametersSpecificCoverageSetCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSpecificCoverageSet()));
        analysisParametersHotspotsSetCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHotspotsSet()));
//        analysisParametersPanelCol.setCellFactory(ComboBoxTableCell.forTableColumn(panelsCb.getItems()));
//        analysisParametersPanelCol.setOnEditStart(t -> {
//            String error = checkAnalysisParametersIdUsed(t.getRowValue().getId());
//            if (error != null) {
//                t.getTableView().edit(-1, null);
//                Message.error(error);
//            }
//        });
//        analysisParametersPanelCol.setOnEditCommit(t -> {
//            AnalysisParameters analysisParameters = t.getRowValue();
//            try {
//                String error = checkPanelError(t.getNewValue());
//                if (error != null) {
//                    Message.error(error);
//                } else {
//                    DAOController.getAnalysisParametersDAO().updateAnalysisParameters(
//                            analysisParameters.getId(),
//                            analysisParameters.getAnalysisName(),
//                            analysisParameters.getGenome(),
//                            analysisParameters.getMinDepth(),
//                            analysisParameters.getWarningDepth(),
//                            analysisParameters.getMinVAF(),
//                            t.getNewValue().getId(),
//                            analysisParameters.isActive());
//                    analysisParameters.setPanel(t.getNewValue());
//                }
//            } catch (SQLException e) {
//                analysisParameters.setPanel(t.getOldValue());
//                logger.error("Error when editing analysis parameters panel", e);
//                Message.error(e.getMessage(), e);
//            }
//            panelsTable.refresh();
//        });
        analysisParametersEnrichmentCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTargetEnrichment()));
        analysisParametersMinDepthCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMinDepth()).asObject());
//        analysisParametersMinDepthCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        analysisParametersMinDepthCol.setOnEditStart(t -> {
//            String error = checkAnalysisParametersIdUsed(t.getRowValue().getId());
//            if (error != null) {
//                t.getTableView().edit(-1, null);
//                Message.error(error);
//            }
//        });
//        analysisParametersMinDepthCol.setOnEditCommit(t -> {
//            AnalysisParameters analysisParameters = t.getRowValue();
//            try {
//                String error = checkMinDepthError(t.getNewValue());
//                if (error != null) {
//                    Message.error(error);
//                } else {
//                    DAOController.getAnalysisParametersDAO().updateAnalysisParameters(
//                            analysisParameters.getId(),
//                            analysisParameters.getAnalysisName(),
//                            analysisParameters.getGenome(),
//                            t.getNewValue(),
//                            analysisParameters.getWarningDepth(),
//                            analysisParameters.getMinVAF(),
//                            analysisParameters.getPanel().getId(),
//                            analysisParameters.isActive());
//                    analysisParameters.setMinDepth(t.getNewValue());
//                }
//            } catch (SQLException e) {
//                analysisParameters.setMinDepth(t.getOldValue());
//                logger.error("Error when editing analysis parameters min depth", e);
//                Message.error(e.getMessage(), e);
//            }
//            panelsTable.refresh();
//        });


        analysisParametersWarningDepthCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getWarningDepth()).asObject());
//        analysisParametersWarningDepthCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        analysisParametersWarningDepthCol.setOnEditStart(t -> {
//            String error = checkAnalysisParametersIdUsed(t.getRowValue().getId());
//            if (error != null) {
//                t.getTableView().edit(-1, null);
//                Message.error(error);
//            }
//        });
//        analysisParametersWarningDepthCol.setOnEditCommit(t -> {
//            AnalysisParameters analysisParameters = t.getRowValue();
//            try {
//                String error = checkWarningDepthError(t.getNewValue());
//                if (error != null) {
//                    Message.error(error);
//                } else {
//                    DAOController.getAnalysisParametersDAO().updateAnalysisParameters(
//                            analysisParameters.getId(),
//                            analysisParameters.getAnalysisName(),
//                            analysisParameters.getGenome(),
//                            analysisParameters.getMinDepth(),
//                            t.getNewValue(),
//                            analysisParameters.getMinVAF(),
//                            analysisParameters.getPanel().getId(),
//                            analysisParameters.isActive());
//                    analysisParameters.setWarningDepth(t.getNewValue());
//                }
//            } catch (SQLException e) {
//                analysisParameters.setWarningDepth(t.getOldValue());
//                logger.error("Error when editing analysis parameters min depth", e);
//                Message.error(e.getMessage(), e);
//            }
//            panelsTable.refresh();
//        });



        analysisParametersMinVafCol.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getMinVAF()).asObject());
//        analysisParametersMinVafCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
//        analysisParametersMinVafCol.setOnEditStart(t -> {
//            String error = checkAnalysisParametersIdUsed(t.getRowValue().getId());
//            if (error != null) {
//                t.getTableView().edit(-1, null);
//                Message.error(error);
//            }
//        });
//        analysisParametersMinVafCol.setOnEditCommit(t -> {
//
//            AnalysisParameters analysisParameters = t.getRowValue();
//            try {
//                String error = checkMinVafError(t.getNewValue());
//                if (error != null) {
//                    Message.error(error);
//                } else {
//                    DAOController.getAnalysisParametersDAO().updateAnalysisParameters(
//                            analysisParameters.getId(),
//                            analysisParameters.getAnalysisName(),
//                            analysisParameters.getGenome(),
//                            analysisParameters.getMinDepth(),
//                            analysisParameters.getWarningDepth(),
//                            t.getNewValue(),
//                            analysisParameters.getPanel().getId(),
//                            analysisParameters.isActive());
//                    analysisParameters.setMinVAF(t.getNewValue());
//                }
//            } catch (SQLException e) {
//                analysisParameters.setMinVAF(t.getOldValue());
//                logger.error("Error when editing analysis parameters min depth", e);
//                Message.error(e.getMessage(), e);
//            }
//            panelsTable.refresh();
//        });


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

        analysisParametersActionCol.setCellFactory(data -> new AnalysisParametersActionsCol());
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
                DAOController.getAnalysisParametersDAO().updateAnalysisParameters(item.getId(),
                        item.getAnalysisName(),
                        item.getGenome(),
                        item.getMinDepth(),
                        item.getWarningDepth(),
                        item.getMinVAF(),
                        item.getPanel().getId(),
                        item.getGeneSet().getId(),
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
        analysisParametersTable.getItems().setAll(DAOController.getAnalysisParametersDAO().getAnalysisParameters());
    }


    @FXML
    private void createAnalysisParametersHandler() {
        String error = checkError();
        if(error == null) {
            try {
                Long specificCoverageSetId = specificCoverageCb.getValue() == null ? null : specificCoverageCb.getValue().getId();
                Long hotspotsSetId = hotspotsCb.getValue() == null ? null : hotspotsCb.getValue().getId();
                DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                        analysisNameTf.getText(),
                        genomesCb.getValue(),
                        minDepthSpinner.getValue(),
                        warningDepthSpinner.getValue(),
                        minVAFSpinner.getValue(),
                        panelsCb.getValue().getId(),
                        geneSetsCb.getValue().getId(),
                        specificCoverageSetId,
                        hotspotsSetId,
                        targetEnrichmentCb.getValue()
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
