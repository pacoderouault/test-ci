package ngsdiaglim.controllers.analysisview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.RunFileListCell;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.RunFile;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AnalysisViewRunInfoController extends HBox {

    private final Logger logger = LogManager.getLogger(AnalysisViewRunInfoController.class);

    @FXML private TextField runNameTf;
    @FXML private TextField runDateTf;
    @FXML private TextField runCreationDateTf;
    @FXML private TextField analysesNbTf;
    @FXML private TextField runCreationUserTf;
    @FXML private ListView<RunFile> runFilesLv;

    private final Analysis analysis;

    public AnalysisViewRunInfoController(Analysis analysis) {
        this.analysis = analysis;
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewRunInfo.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        initView();
    }

    private void initView() {
        runFilesLv.setCellFactory(data -> new RunFileListCell());
        if (analysis != null) {
            analysis.getRun().loadRunFiles();
            runNameTf.setText(analysis.getRun().getName());
            runDateTf.setText(DateFormatterUtils.formatLocalDate(analysis.getRun().getDate()));
            runCreationDateTf.setText(DateFormatterUtils.formatLocalDate(analysis.getRun().getCreationDate()));
            runCreationUserTf.setText(analysis.getRun().getCreationUser());
            try {
                analysesNbTf.setText(String.valueOf(analysis.getRun().getAnalyses().size()));
            } catch (SQLException e) {
                analysesNbTf.setText("NA");
                logger.error(e);
            }
            runFilesLv.getItems().setAll(analysis.getRun().getRunFiles());
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        runNameTf.setText(null);
        runDateTf.setText(null);
        runCreationDateTf.setText(null);
        analysesNbTf.setText(null);
        runFilesLv.getItems().clear();
    }
}
