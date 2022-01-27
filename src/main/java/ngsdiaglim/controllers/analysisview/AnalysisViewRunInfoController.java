package ngsdiaglim.controllers.analysisview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.RunFileListCell;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.RunFile;
import ngsdiaglim.utils.DateFormatterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();

    public AnalysisViewRunInfoController() {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewRunInfo.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        analysis.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                initView();
            } else {
                clearFields();
            }
        });
    }

    public Analysis getAnalysis() {
        return analysis.get();
    }

    public SimpleObjectProperty<Analysis> analysisProperty() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis.set(analysis);
    }

    private void initView() {
        runFilesLv.setCellFactory(data -> new RunFileListCell());
        if (analysis.get() != null) {
            analysis.get().getRun().loadRunFiles();
            runNameTf.setText(analysis.get().getRun().getName());
            runDateTf.setText(DateFormatterUtils.formatLocalDate(analysis.get().getRun().getDate()));
            runCreationDateTf.setText(DateFormatterUtils.formatLocalDate(analysis.get().getRun().getCreationDate()));
            runCreationUserTf.setText(analysis.get().getRun().getCreationUser());
            try {
                analysesNbTf.setText(String.valueOf(analysis.get().getRun().getAnalyses().size()));
            } catch (SQLException e) {
                analysesNbTf.setText("NA");
                logger.error(e);
            }
            runFilesLv.getItems().setAll(analysis.get().getRun().getRunFiles());
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
