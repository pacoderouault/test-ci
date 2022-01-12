package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.report.GeneCommentActionTableCell;
import ngsdiaglim.controllers.cells.report.MutationCommentActionTableCell;
import ngsdiaglim.controllers.cells.report.OtherCommentActionTableCell;
import ngsdiaglim.controllers.cells.report.SelectedCommentActionTableCell;
import ngsdiaglim.controllers.dialogs.EditGeneReportCommentDialog;
import ngsdiaglim.controllers.dialogs.EditMutationReportCommentDialog;
import ngsdiaglim.controllers.dialogs.EditReportCommentDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ReportType;
import ngsdiaglim.modeles.reports.ReportGeneCommentary;
import ngsdiaglim.modeles.reports.ReportMutationCommentary;
import ngsdiaglim.modeles.reports.bgm.ReportCommentary;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.variants.Annotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ReportComments extends ReportPane {

    private static final Logger logger = LogManager.getLogger(ReportComments.class);

    @FXML private TableView<ReportGeneCommentary> reportGeneCommentaryTable;
    @FXML private TableColumn<ReportGeneCommentary, String> reportGeneCommentaryGeneCol;
    @FXML private TableColumn<ReportGeneCommentary, String> reportGeneCommentaryTitleCol;
    @FXML private TableColumn<ReportGeneCommentary, Void> reportGeneCommentaryActionsCol;
    @FXML private TableView<ReportMutationCommentary> reportMutCommentaryTable;
    @FXML private TableColumn<ReportMutationCommentary, String> reportMutCommentaryMutCol;
    @FXML private TableColumn<ReportMutationCommentary, String> reportMutCommentaryTitleCol;
    @FXML private TableColumn<ReportMutationCommentary, Void> reportMutCommentaryActionsCol;
    @FXML private TableView<ReportCommentary> reportOtherCommentaryTable;
    @FXML private TableColumn<ReportCommentary, String> reportOtherCommentaryTitleCol;
    @FXML private TableColumn<ReportCommentary, Void> reportOtherCommentaryActionsCol;
    @FXML private TableView<ReportCommentary> selectedCommentaryTable;
    @FXML private TableColumn<ReportCommentary, String> selectedCommentaryTitleCol;
    @FXML private TableColumn<ReportCommentary, Void> selectedCommentaryActionsCol;
    @FXML private TextArea previewTa;

    public ReportComments(AnalysisViewReportBGMController analysisViewReportBGMController) {
        super(analysisViewReportBGMController);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ReportComments.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        init();

        reportController.getReportSelectVariants().getReportedVariants().addListener((ListChangeListener<Annotation>) change -> {
            try {
                fillCommentsTables();
            } catch (SQLException e) {
                logger.error(e);
                Message.error(e.getMessage(), e);
            }
        });
    }

    @Override
    String checkForm() {
        return null;
    }

    private void init() {
        initGeneCommentsTable();
        initMutationCommentsTable();
        initOtherCommentsTable();
        initSelectedCommentsTable();

        try {
            fillCommentsTables();
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    private void initGeneCommentsTable() {
        reportGeneCommentaryGeneCol.setCellValueFactory(data -> data.getValue().geneNameProperty());
        reportGeneCommentaryTitleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        reportGeneCommentaryActionsCol.setCellFactory(data -> new GeneCommentActionTableCell());
    }

    private void initMutationCommentsTable() {
        reportMutCommentaryMutCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnnotation().toString()));
        reportMutCommentaryTitleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        reportMutCommentaryActionsCol.setCellFactory(data -> new MutationCommentActionTableCell());
    }

    private void initOtherCommentsTable() {
        reportOtherCommentaryTitleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        reportOtherCommentaryActionsCol.setCellFactory(data -> new OtherCommentActionTableCell());
    }

    private void initSelectedCommentsTable() {
        selectedCommentaryTitleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        selectedCommentaryActionsCol.setCellFactory(data -> new SelectedCommentActionTableCell());
        selectedCommentaryTable.getItems().addListener((ListChangeListener<ReportCommentary>) change -> fillPreviewTextfield());
    }

    public void fillCommentsTables() throws SQLException {
        fillGeneCommentsTable();
        fillMutationCommentsTable();
        fillOtherCommentsTable();
    }

    private void fillGeneCommentsTable() throws SQLException {
        // get genes of reported Variants
        final Set<String> genes = new HashSet<>();
        for (Annotation reportedVariant : reportController.getReportSelectVariants().getReportedVariants()) {
            if (reportedVariant.getTranscriptConsequence() != null
                    && reportedVariant.getTranscriptConsequence().getGeneName() != null
                    && !reportedVariant.getTranscriptConsequence().getGeneName().isEmpty()) {
                genes.add(reportedVariant.getTranscriptConsequence().getGeneName());
            }
            if (reportedVariant.getGene() != null) {
                genes.add(reportedVariant.getGene().getGeneName());
            }
        }
        reportGeneCommentaryTable.getItems().setAll(
                DAOController.getReportGeneCommentaryDAO().getReportGeneCommentary(
                        ReportType.BGM,
                        genes));
    }


    private void fillMutationCommentsTable() throws SQLException {
        final Map<Long, Annotation> variants = new HashMap<>();
        for (Annotation reportedVariant : reportController.getReportSelectVariants().getReportedVariants()) {
            variants.put(reportedVariant.getVariant().getId(), reportedVariant);
        }
        reportMutCommentaryTable.getItems().setAll(
                DAOController.getReportMutationCommentaryDAO().getReportMutationCommentary(ReportType.BGM, variants));
    }


    private void fillOtherCommentsTable() throws SQLException {
        reportOtherCommentaryTable.getItems().setAll(
                DAOController.getReportCommentaryDAO().getReportCommentary(ReportType.BGM));
    }


    private Set<String> getTargetGenes() {
        // get genes of reported Variants
        final Set<String> genes = new HashSet<>();
        for (Annotation reportedVariant : reportController.getReportSelectVariants().getReportedVariants()) {
            if (reportedVariant.getTranscriptConsequence() != null
                    && reportedVariant.getTranscriptConsequence().getGeneName() != null
                    && !reportedVariant.getTranscriptConsequence().getGeneName().isEmpty()) {
                genes.add(reportedVariant.getTranscriptConsequence().getGeneName());
            }
            if (reportedVariant.getGene() != null) {
                genes.add(reportedVariant.getGene().getGeneName());
            }
        }
        return genes;
    }


    private List<Annotation> getTargetVariants() {
        return new ArrayList<>(reportController.getReportSelectVariants().getReportedVariants());
    }

    @FXML
    private void addGeneComment() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.CREATE_REPORT_COMMENT)) {
            EditGeneReportCommentDialog dialog = new EditGeneReportCommentDialog(getTargetGenes());
            dialog.setEditable(true);
            Message.showDialog(dialog);
            dialog.getButton(ButtonType.OK).setOnAction(event -> {
                ReportGeneCommentary commentary = new ReportGeneCommentary(
                        ReportType.BGM,
                        dialog.getValue().getGeneName(),
                        dialog.getValue().getTitle(),
                        dialog.getValue().getComment()
                );
                try {
                    long id = DAOController.getReportGeneCommentaryDAO().insertReportGeneCommentary(commentary);
                    commentary.setId(id);
                    Message.hideDialog(dialog);
                    fillGeneCommentsTable();
                } catch (SQLException e) {
                    logger.error(e);
                    Message.error(e.getMessage(), e);
                }
            });
        }
    }


    @FXML
    private void addMutationComment() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.CREATE_REPORT_COMMENT)) {
            EditMutationReportCommentDialog dialog = new EditMutationReportCommentDialog(getTargetVariants());
            dialog.setEditable(true);
            Message.showDialog(dialog);
            dialog.getButton(ButtonType.OK).setOnAction(event -> {
                ReportMutationCommentary commentary = new ReportMutationCommentary(
                        ReportType.BGM,
                        dialog.getValue().getTitle(),
                        dialog.getValue().getComment(),
                        dialog.getValue().getVariant()
                );
                try {
                    long id = DAOController.getReportMutationCommentaryDAO().insertReportMutationCommentary(commentary);
                    commentary.setId(id);
                    Message.hideDialog(dialog);
                    fillMutationCommentsTable();
                } catch (SQLException e) {
                    logger.error(e);
                    Message.error(e.getMessage(), e);
                }
            });
        }
    }

    @FXML
    private void addOtherComment() {
        if (App.get().getLoggedUser().isPermitted(PermissionsEnum.CREATE_REPORT_COMMENT)) {
            EditReportCommentDialog dialog = new EditReportCommentDialog();
            dialog.setEditable(true);
            Message.showDialog(dialog);
            dialog.getButton(ButtonType.OK).setOnAction(event -> {
                ReportCommentary commentary = new ReportCommentary(
                        ReportType.BGM,
                        dialog.getValue().getTitle(),
                        dialog.getValue().getComment()
                );
                try {
                    long id = DAOController.getReportCommentaryDAO().insertReportCommentary(commentary);
                    commentary.setId(id);
                    Message.hideDialog(dialog);
                    fillOtherCommentsTable();
                } catch (SQLException e) {
                    logger.error(e);
                    Message.error(e.getMessage(), e);
                }
            });
        }
    }

    public void addCommentToReport(ReportCommentary reportCommentary) {
        if (!selectedCommentaryTable.getItems().contains(reportCommentary)) {
            selectedCommentaryTable.getItems().add(reportCommentary);
        }
    }

    private void fillPreviewTextfield() {
        StringBuilder sb = new StringBuilder();
        for (ReportCommentary comment : selectedCommentaryTable.getItems()) {
            sb.append(comment.getComment()).append("\n");
        }
        previewTa.setText(sb.toString());
    }

    public List<ReportCommentary> getCommentaries() {
        return selectedCommentaryTable.getItems();
    }
}
