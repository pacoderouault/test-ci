package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.dialogs.AnalysisInfoDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.exceptions.NotBiallelicVariant;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AnalysisActionsTableCell extends TableCell<Analysis, Void> {

    private final static Logger logger = LogManager.getLogger(ngsdiaglim.controllers.cells.RunActionTableCell.class);

    private final HBox box = new HBox();
    private final Button openAnalysisBtn = new Button("", new FontIcon("mdmz-open_in_new"));
    private final Button deleteAnalysisBtn = new Button("", new FontIcon("mdal-delete_forever"));
    private final Button infoAnalysisBtn = new Button("", new FontIcon("mdal-info"));
    private static final Tooltip openAnalysisTp = new Tooltip(App.getBundle().getString("home.module.analyseslist.tp.openAnalysis"));
    private static final Tooltip analysisDetailTp = new Tooltip(App.getBundle().getString("home.module.analyseslist.tp.analysisInfo"));
    private static final Tooltip deleteAnalysisTp = new Tooltip(App.getBundle().getString("home.module.analyseslist.tp.deleteAnalysis"));

    private final boolean canDelete;

    public AnalysisActionsTableCell() {
        this(true);
    }

    public AnalysisActionsTableCell(boolean canDelete) {
        this.canDelete = canDelete;
        box.getStyleClass().add("box-action-cell");
        deleteAnalysisBtn.getStyleClass().add("button-action-cell");
        infoAnalysisBtn.getStyleClass().add("button-action-cell");

        box.getChildren().addAll(openAnalysisBtn, infoAnalysisBtn);
        if (canDelete) {
            box.getChildren().add(deleteAnalysisBtn);
        }

        openAnalysisTp.setShowDelay(Duration.ZERO);
        analysisDetailTp.setShowDelay(Duration.ZERO);
        deleteAnalysisTp.setShowDelay(Duration.ZERO);
        openAnalysisBtn.setTooltip(openAnalysisTp);
        infoAnalysisBtn.setTooltip(analysisDetailTp);
        deleteAnalysisBtn.setTooltip(deleteAnalysisTp);

        openAnalysisBtn.setOnAction(e -> openAnalysisHandler());
        deleteAnalysisBtn.setOnAction(e -> deleteAnalysisHandler());
        infoAnalysisBtn.setOnAction(e -> showAnalysisInfoDialog());

    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        deleteAnalysisBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.REMOVE_ANALYSE));
        if(empty) {
            setGraphic(null);
        }
        else {
            setGraphic(box);
        }
    }

    private void openAnalysisHandler() {
        Analysis analysis = getTableRow().getItem();
        App.get().getAppController().openAnalysis(analysis);
//        if (analysis != null) {
//            Object[] messageArguments = {analysis.getName()};
//            String message = BundleFormatter.format("home.module.analyseslist.msg.openingAnalysis", messageArguments);
//            WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), message);
//            wid.addTaskEndNotification(r -> {
//                if (r == 0) {
//                    App.get().getAppController().showAnalysisView(analysis);
//                }
//            });
//            wid.exec("LoadPanels", inputParam -> {
//                try {
//                    AnalysisParameters params = DAOController.get().getAnalysisParametersDAO().getAnalysisParameters(analysis.getAnalysisParameters().getId());
//                    analysis.setAnalysisParameters(params);
//                    VCFParser vcfParser = new VCFParser(analysis.getVcfFile(), analysis.getAnalysisParameters(), analysis.getRun());
//                    vcfParser.parseVCF(true);
//                    analysis.setAnnotations(vcfParser.getAnnotations());
//                    analysis.loadCoverage();
//                } catch (Exception e) {
//                    logger.error(e);
//                    Platform.runLater(() -> Message.error(e.getMessage(), e));
//                    return 1;
//                }
//                return 0;
//            });
//        }
    }

    private void showAnalysisInfoDialog() {
        Analysis analysis = getTableRow().getItem();
        if (analysis != null) {
            AnalysisInfoDialog dialog = new AnalysisInfoDialog(App.get().getAppController().getDialogPane());
            dialog.setValue(analysis);
            Message.showDialog(dialog);
        }
    }

    private void deleteAnalysisHandler() {
        Analysis analysis = getTableRow().getItem();
        Object[] arguments = {analysis.getName()};
        DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("home.module.analyseslist.msg.conf.deleteAnalysis", arguments));
        dialog.getButton(ButtonType.YES).setOnAction(event -> {
            try {
                DAOController.get().getAnalysisDAO().deleteAnalysis(analysis.getId());
                FileUtils.deleteDirectory(new File(analysis.getDirectoryPath()));
                getTableView().refresh();
            } catch (SQLException | IOException e) {
                logger.error("Error when deleting analysis", e);
                Message.error(e.getMessage(), e);
            }
            Message.hideDialog(dialog);
            try {
                ModuleManager.getHomeController().fillAnalysesTable();
            } catch (SQLException e) {
                logger.error("Error when getting analysis from db", e);
            }
        });

    }
}
