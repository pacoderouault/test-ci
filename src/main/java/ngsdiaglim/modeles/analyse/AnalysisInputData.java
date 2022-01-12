package ngsdiaglim.modeles.analyse;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.parsers.SamtoolsDepthParser;
import ngsdiaglim.utils.BamUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class AnalysisInputData {

    private final static Logger logger = LogManager.getLogger(AnalysisInputData.class);

    private final SimpleStringProperty analysisName = new SimpleStringProperty();
    private final SimpleStringProperty sampleName = new SimpleStringProperty();
    private final SimpleObjectProperty<File> vcfFile = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<File> bamFile = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<File> depthFile = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Run> run = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<AnalysisParameters> analysisParameters = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<AnalysisInputState> state = new SimpleObjectProperty<>();

    public AnalysisInputData(Run run, String analysisName, String sampleName) {
        this.sampleName.setValue(sampleName);
        this.run.setValue(run);
        this.analysisName.setValue(analysisName);

        this.analysisName.addListener((obs, oldV, newV) -> computeState());
        this.sampleName.addListener((obs, oldV, newV) -> computeState());
        this.vcfFile.addListener((obs, oldV, newV) -> computeState());
        this.analysisParameters.addListener((obs, oldV, newV) -> computeState());
        this.run.addListener((obs, oldV, newV) -> computeState());
    }

    public Run getRun() {
        return run.get();
    }

    public SimpleObjectProperty<Run> runProperty() {
        return run;
    }

    public void setRun(Run run) {
        this.run.set(run);
    }

    public AnalysisParameters getAnalysisParameters() {
        return analysisParameters.get();
    }

    public SimpleObjectProperty<AnalysisParameters> analysisParametersProperty() {
        return analysisParameters;
    }

    public void setAnalysisParameters(AnalysisParameters analysisParameters) {
        this.analysisParameters.set(analysisParameters);
    }

    public String getAnalysisName() {
        return analysisName.get();
    }

    public SimpleStringProperty analysisNameProperty() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName.set(analysisName);
    }

    public String getSampleName() {
        return sampleName.get();
    }

    public SimpleStringProperty sampleNameProperty() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName.set(sampleName);
    }

    public File getVcfFile() {
        return vcfFile.get();
    }

    public SimpleObjectProperty<File> vcfFileProperty() {
        return vcfFile;
    }

    public void setVcfFile(File vcfFile) {
        this.vcfFile.set(vcfFile);
    }

    public File getBamFile() {
        return bamFile.get();
    }

    public SimpleObjectProperty<File> bamFileProperty() {
        return bamFile;
    }

    public void setBamFile(File bamFile) {
        this.bamFile.set(bamFile);
    }

    public File getDepthFile() {
        return depthFile.get();
    }

    public SimpleObjectProperty<File> depthFileProperty() {
        return depthFile;
    }

    public void setDepthFile(File depthFile) {
        this.depthFile.set(depthFile);
    }

    public AnalysisInputState getState() {
        return state.get();
    }

    public SimpleObjectProperty<AnalysisInputState> stateProperty() {
        return state;
    }

    public void setState(AnalysisInputState state) {
        this.state.set(state);
    }

    private boolean invalidCharacterInAnalysisName() {
//        String[] invalidCharacters = new String[]{"/", "\\", ":", ">", "<", "\"", "|", "?", "*"};
        return !Pattern.matches("[^.][\\dA-Za-z_\\-.]+[^.]", getAnalysisName());
//        return ngsdiaglim.utils.StringUtils.stringContainsItemFromList(getAnalysisName(), invalidCharacters);
    }

    public void computeState() {
        if (state.getValue() != null && state.getValue().equals(AnalysisInputState.DUPLICATE_ANALYSIS)) {
            return;
        }
        if (getAnalysisParameters() == null) state.setValue(AnalysisInputState.PARAMETERS_NULL);
        else if (getRun() == null) state.setValue(AnalysisInputState.RUN_NULL);
        else if (StringUtils.isBlank(getAnalysisName())) state.setValue(AnalysisInputState.NAME_NULL);
        else if (invalidCharacterInAnalysisName()) state.setValue(AnalysisInputState.INVALID_CHARACTER);
        else if (run.get().hasAnalysis(getAnalysisName())) state.setValue(AnalysisInputState.DUPLICATE_ANALYSIS);
        else if (StringUtils.isBlank(getSampleName())) state.setValue(AnalysisInputState.SAMPLENAME_NULL);
        else if (getVcfFile() == null || !getVcfFile().exists()) state.setValue(AnalysisInputState.VCF_NULL);
        else if (!VCFUtils.isVCFReadable(getVcfFile())) state.setValue(AnalysisInputState.VCF_INVALID);
        else if (getBamFile() != null && !BamUtils.isBamFile(getBamFile()))
            state.setValue(AnalysisInputState.BAM_INVALID);
        else {
            try {
                if (getDepthFile() != null && !SamtoolsDepthParser.isDepthFile(getDepthFile()))
                    state.setValue(AnalysisInputState.DEPTH_INVALID);
                else state.setValue(AnalysisInputState.VALID);
            } catch (IOException e) {
                logger.error("Error when check samtools depth", e);
                Message.error(e.getMessage(), e);
                state.setValue(AnalysisInputState.INVALID);
            }
        }

    }

    public enum AnalysisInputState {
        RUN_NULL(App.getBundle().getString("importanalysesdialog.msg.state.runNull")),
        DUPLICATE_ANALYSIS(App.getBundle().getString("importanalysesdialog.msg.state.duplicateAnalysisName")),
        VCF_NULL(App.getBundle().getString("importanalysesdialog.msg.state.vcfNull")),
        BAM_INVALID(App.getBundle().getString("importanalysesdialog.msg.state.bamInvalid")),
        VCF_INVALID(App.getBundle().getString("importanalysesdialog.msg.state.vcfInvalid")),
        DEPTH_INVALID(App.getBundle().getString("importanalysesdialog.msg.state.samdepthInvalid")),
        NAME_NULL(App.getBundle().getString("importanalysesdialog.msg.state.analysisnameNull")),
        INVALID_CHARACTER(App.getBundle().getString("importanalysesdialog.msg.state.invalidCharacters")),
        SAMPLENAME_NULL(App.getBundle().getString("importanalysesdialog.msg.state.nameNull")),
        PARAMETERS_NULL(App.getBundle().getString("importanalysesdialog.msg.state.panelNull")),
        INVALID("NA"),
        VALID("");

        private final String message;

        AnalysisInputState(String message) {
            this.message = message;
        }

        public String getMessage() {return message;}
    }
}
