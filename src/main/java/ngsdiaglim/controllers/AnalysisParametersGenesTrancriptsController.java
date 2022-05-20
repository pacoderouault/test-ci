package ngsdiaglim.controllers;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.AnalysisParametersGeneSetDeleteCell;
import ngsdiaglim.controllers.cells.GeneTranscriptsCell;
import ngsdiaglim.controllers.dialogs.AddGeneTranscriptSetDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.parsers.GeneSetParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class AnalysisParametersGenesTrancriptsController extends HBox {

    private static final Logger logger = LogManager.getLogger(AnalysisParametersGenesTrancriptsController.class);

    @FXML private TableView<GeneSet> genesSetTable;
    @FXML private TableColumn<GeneSet, String> geneSetNameCol;
    @FXML private TableColumn<GeneSet, Integer> geneSetSizeCol;
    @FXML private TableColumn<GeneSet, Boolean> geneSetActiveCol;
    @FXML private TableColumn<GeneSet, Void> geneSetDeleteCol;

    @FXML private TableView<Gene> geneTranscriptTable;
    @FXML private TableColumn<Gene, String> genesCol;
    @FXML private TableColumn<Gene, HashMap<String, Transcript>> transcriptsCol;

    public AnalysisParametersGenesTrancriptsController(CreateAnalysisParametersController createAnalysisParametersController) {
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisParametersGenesTrancripts.fxml"), App.getBundle());
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
        initGeneTranscriptSetTable();
        initGeneTranscriptTable();

        try {
            loadGeneSets();
        } catch (SQLException e) {
            logger.error("Error when loading panel", e);
            Message.error(e.getMessage(), e);
        }
    }




    private void initGeneTranscriptSetTable() {
        genesSetTable.setEditable(false);
        geneSetNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
//        geneSetNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        geneSetNameCol.setOnEditCommit(t -> {
//            if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
//                GeneSet geneSet = t.getRowValue();
//                try {
//                    String error = checkPanelName(t.getNewValue());
//                    if (error != null) {
//                        Message.error(error);
//                    } else {
//                        DAOController.getGeneSetDAO().updateGeneSet(geneSet, t.getNewValue(), geneSet.isActive());
//                        geneSet.setName(t.getNewValue());
//                    }
//                } catch (SQLException e) {
//                    geneSet.setName(t.getOldValue());
//                    logger.error("Error when editing user name", e);
//                    Message.error(e.getMessage(), e);
//                }
//                genesSetTable.refresh();
//            }
//        });

        geneSetSizeCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getGenesCount()).asObject());

        geneSetActiveCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isActive()));
        geneSetActiveCol.setCellFactory(p -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<GeneSet, Boolean> tableCell = new TableCell<>() {

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
            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> validateGeneTranscriptSetActivation(checkBox, tableCell.getTableRow().getItem(), event));
            return tableCell;
        });
        geneSetDeleteCol.setCellFactory(c -> new AnalysisParametersGeneSetDeleteCell());
    }


    private void initGeneTranscriptTable() {
        genesCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGeneName()));
        transcriptsCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTranscripts()));
        transcriptsCol.setCellFactory(data -> new GeneTranscriptsCell());
        genesSetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                geneTranscriptTable.getItems().setAll(newV.getGenes().values());
            }
        });
        // auto adjust the width of the column clonotypesNameCol
        transcriptsCol.prefWidthProperty().bind(
                geneTranscriptTable.widthProperty()
                        .subtract(genesCol.widthProperty())
                        .subtract(2));  // a border stroke?
    }


    private void loadGeneSets() throws SQLException {
        genesSetTable.getItems().setAll(DAOController.getGeneSetDAO().getGeneSets());
    }


    @FXML
    private void addGeneSetHandler() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS)) {
            AddGeneTranscriptSetDialog dialog = new AddGeneTranscriptSetDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(dialog);
            Button b = dialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (dialog.isValid() && dialog.getValue() != null) {
                    WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("addGeneTranscriptSetDialog.msg.loading"));
                    wid.addTaskEndNotification(r -> {
                        if (r == 0) {
                            try {
                                loadGeneSets();
                                Message.hideDialog(dialog);
                            } catch (SQLException ex) {
                                logger.error("Error when adding panel", ex);
                                Message.error(ex.getMessage(), ex);
                            }
                        }
                    });
                    wid.exec("LoadGeneSet", inputParam -> {
                        long geneSetId = -1;
                        try {
                            HashSet<Gene> genes = GeneSetParser.parseGeneSet(dialog.getValue().getFile());
                            geneSetId = DAOController.getGeneSetDAO().addGeneSet(dialog.getValue().getName());
                            for (Gene gene : genes) {
                                long geneId = DAOController.getGeneDAO().addGene(gene, geneSetId);
                                gene.setId(geneId);
                                for (Transcript transcript : gene.getTranscripts().values()) {
                                    long transcriptId = DAOController.getTranscriptsDAO().addTranscript(transcript.getName(), gene.getId());
                                    transcript.setId(transcriptId);
                                }
                                // if only one transcript for the gene, set it as "preferred transcript"
                                if (gene.getTranscripts().size() == 1) {
                                    Optional<Transcript> opt = gene.getTranscripts().values().stream().findAny();
                                    if(opt.isPresent()) {
                                        DAOController.getGeneDAO().setPreferredTranscript(gene.getId(), opt.get().getId());
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            logger.error("Error when adding genetranscriptset", ex);
                            Platform.runLater(() -> Message.error(ex.getMessage(), ex));
                            try {
                                DAOController.getGeneSetDAO().deleteGeneSet(geneSetId);
                            } catch (SQLException exc) {
                                logger.error("Error when deleting genetranscriptset", exc);
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


    private void validateGeneTranscriptSetActivation(CheckBox checkBox, GeneSet item, MouseEvent event) {
        event.consume();
        Object[] messageArguments = {item.getName()};
        String message;
        if (checkBox.isSelected()) {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.inactiveGeneSet", messageArguments);
        }
        else {
            message = BundleFormatter.format("createAnalasisParameters.msg.confirm.reactiveGeneSet", messageArguments);
        }
        DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
        d.getButton(ButtonType.YES).setOnAction(e -> {
            try {
                DAOController.getGeneSetDAO().updateGeneSet(item, item.getName(), !item.isActive());
                item.setActive(!checkBox.isSelected());
                checkBox.setSelected(!checkBox.isSelected());
                loadGeneSets();
            } catch (SQLException ex) {
                logger.error(ex);
                Message.error(ex.getMessage(), ex);
            }
            finally {
                Message.hideDialog(d);
            }
        });
    }

    public TableView<GeneSet> getGenesSetTable() {return genesSetTable;}
}
