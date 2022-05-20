package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.controllers.charts.PredictionGauge3;
import ngsdiaglim.controllers.charts.PredictionGaugeLabel;
import ngsdiaglim.enumerations.PredictionTools;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.TranscriptConsequence;
import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PredictionsDetailDialog extends DialogPane.Dialog<Annotation> {

    private final static Logger logger = LogManager.getLogger(PredictionsDetailDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private GridPane conservationGridPane;
    @FXML private GridPane functionalGridPane;
    @FXML private GridPane globalGridPane;
    @FXML private GridPane spliceAIGridPane;
    @FXML private GridPane dbscSNVGridPane;

    @FXML private FontIcon gerpHelp;
    @FXML private FontIcon phastCons100WayHelp;
    @FXML private FontIcon phastCons30WayHelp;
    @FXML private FontIcon phylop100WayHelp;
    @FXML private FontIcon phylop30WayHelp;
    @FXML private FontIcon siphyHelp;
    @FXML private FontIcon fathmmHelp;
    @FXML private FontIcon siftHelp;
    @FXML private FontIcon polyphen2HvarHelp;
    @FXML private FontIcon polyphen2HdivHelp;
    @FXML private FontIcon vest4Help;
    @FXML private FontIcon mvpHelp;
    @FXML private FontIcon caddRawHelp;
    @FXML private FontIcon caddPhredHelp;
    @FXML private FontIcon mcapHelp;
    @FXML private FontIcon metaLRHelp;
    @FXML private FontIcon metaSVMHelp;
    @FXML private FontIcon revelHelp;
    @FXML private FontIcon spliceAIHelp;
    @FXML private FontIcon dbscSNVADAHelp;
    @FXML private FontIcon dbscSNVRFHelp;

    @FXML private TextField gerpScoreTf;
    @FXML private TextField phastCons100WayScoreTf;
    @FXML private TextField phastCons30WayScoreTf;
    @FXML private TextField phylop100WayScoreTf;
    @FXML private TextField phylop30WayScoreTf;
    @FXML private TextField siphyScoreTf;
    @FXML private TextField fathmmScoreTf;
    @FXML private TextField siftScoreTf;
    @FXML private TextField polyphen2HvarScoreTf;
    @FXML private TextField polyphen2HdivScoreTf;
    @FXML private TextField vest4ScoreTf;
    @FXML private TextField mvpScoreTf;
    @FXML private TextField caddRawScoreTf;
    @FXML private TextField caddPhredScoreTf;
    @FXML private TextField mcapScoreTf;
    @FXML private TextField metaLRScoreTf;
    @FXML private TextField metaSVMScoreTf;
    @FXML private TextField revelScoreTf;

    @FXML private TextField spliceAIDGPosTf;
    @FXML private TextField spliceAIDGScoreTf;
    @FXML private TextField spliceAIDLPosTf;
    @FXML private TextField spliceAIDLScoreTf;
    @FXML private TextField spliceAIAGPosTf;
    @FXML private TextField spliceAIAGScoreTf;
    @FXML private TextField spliceAIALPosTf;
    @FXML private TextField spliceAIALScoreTf;
    @FXML private TextField dbscSNVAdaScoreTf;
    @FXML private TextField dbscSNVRfScoreTf;

    @FXML private TextField variantTf;

    private PredictionGauge3 gerpGauge;
    private PredictionGauge3 phastCons100WayGauge;
    private PredictionGauge3 phastCons30WayGauge;
    private PredictionGauge3 phylop100WayGauge;
    private PredictionGauge3 phylop30WayGauge;
    private PredictionGauge3 siphyGauge;
    private PredictionGauge3 fathmmGauge;
    private PredictionGauge3 polyphen2HvarGauge;
    private PredictionGauge3 polyphen2HdivGauge;
    private PredictionGauge3 siftGauge;
    private PredictionGauge3 vest4Gauge;
    private PredictionGauge3 mvpGauge;
    private PredictionGauge3 caddRawGauge;
    private PredictionGauge3 caddPhredGauge;
    private PredictionGauge3 mcapGauge;
    private PredictionGauge3 metaLRGauge;
    private PredictionGauge3 metaSVMGauge;
    private PredictionGauge3 revelGauge;
    private PredictionGauge3 spliceAIDGGauge;
    private PredictionGauge3 spliceAIDLGauge;
    private PredictionGauge3 spliceAIAGGauge;
    private PredictionGauge3 spliceAIALGauge;
    private PredictionGauge3 dbscSNVADAGauge;
    private PredictionGauge3 dbscSNVRFGauge;

    private final List<TextField> textfields = new ArrayList<>();
    private final List<PredictionGauge3> predictionGauges = new ArrayList<>();
    private final ChangeListener<TranscriptConsequence> transcriptListener;

    public PredictionsDetailDialog() {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INFORMATION);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/PredictionsDetailDialog2.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (Exception e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setTitle(App.getBundle().getString("predictionsdetaildialog.title"));
        setContent(dialogContainer);
        textfields.add(siftScoreTf);
        textfields.add(gerpScoreTf);
        textfields.add(phastCons100WayScoreTf);
        textfields.add(phastCons30WayScoreTf);
        textfields.add(phylop100WayScoreTf);
        textfields.add(phylop30WayScoreTf);
        textfields.add(siphyScoreTf);
        textfields.add(fathmmScoreTf);
        textfields.add(polyphen2HvarScoreTf);
        textfields.add(polyphen2HdivScoreTf);
        textfields.add(siftScoreTf);
        textfields.add(vest4ScoreTf);
        textfields.add(mvpScoreTf);
        textfields.add(caddRawScoreTf);
        textfields.add(caddPhredScoreTf);
        textfields.add(mcapScoreTf);
        textfields.add(metaLRScoreTf);
        textfields.add(metaSVMScoreTf);
        textfields.add(revelScoreTf);
        textfields.add(spliceAIDGPosTf);
        textfields.add(spliceAIDGScoreTf);
        textfields.add(spliceAIDLPosTf);
        textfields.add(spliceAIDLScoreTf);
        textfields.add(spliceAIAGPosTf);
        textfields.add(spliceAIAGScoreTf);
        textfields.add(spliceAIALPosTf);
        textfields.add(spliceAIALScoreTf);
        textfields.add(dbscSNVAdaScoreTf);
        textfields.add(dbscSNVRfScoreTf);
        textfields.add(variantTf);
        textfields.forEach(t -> t.visibleProperty().bind(t.textProperty().isNotNull()));
        initHelpTooltips();
        initGauges();
        initGridPanes();

        transcriptListener = (obs, oldV, newV) -> fillFields();

        valueProperty().addListener((obs, oldV, newV) -> {
            if (oldV != null) {
                oldV.transcriptConsequenceProperty().removeListener(transcriptListener);
            }
            if (newV != null) {
                newV.transcriptConsequenceProperty().addListener(transcriptListener);
            }
            fillFields();
        });



    }

    private void initHelpTooltips() {
        Tooltip gerpTp = new Tooltip(PredictionTools.GERP.getDesc());
        gerpTp.setShowDelay(Duration.ZERO);
        Tooltip.install(gerpHelp, gerpTp);
        Tooltip phastCons100WayTp = new Tooltip(PredictionTools.PHASTCONS100WAY_VERTEBRATE.getDesc());
        phastCons100WayTp.setShowDelay(Duration.ZERO);
        Tooltip.install(phastCons100WayHelp, phastCons100WayTp);
        Tooltip phastCons30WayTp = new Tooltip(PredictionTools.PHASTCONS30WAY_MAMMALIAN.getDesc());
        phastCons30WayTp.setShowDelay(Duration.ZERO);
        Tooltip.install(phastCons30WayHelp, phastCons30WayTp);
        Tooltip phylop100WayTp = new Tooltip(PredictionTools.PHYLOP100WAY_VERTEBRATE.getDesc());
        phylop100WayTp.setShowDelay(Duration.ZERO);
        Tooltip.install(phylop100WayHelp, phylop100WayTp);
        Tooltip phylop30WayTp = new Tooltip(PredictionTools.PHYLOP30WAY_MAMMALIAN.getDesc());
        phylop30WayTp.setShowDelay(Duration.ZERO);
        Tooltip.install(phylop30WayHelp, phylop30WayTp);
        Tooltip siphyTp = new Tooltip(PredictionTools.SIPHY.getDesc());
        siphyTp.setShowDelay(Duration.ZERO);
        Tooltip.install(siphyHelp, siphyTp);

        Tooltip fathmmTp = new Tooltip(PredictionTools.FATHMM.getDesc());
        fathmmTp.setShowDelay(Duration.ZERO);
        Tooltip.install(fathmmHelp, fathmmTp);
        Tooltip siftTp = new Tooltip(PredictionTools.SIFT.getDesc());
        siftTp.setShowDelay(Duration.ZERO);
        Tooltip.install(siftHelp, siftTp);
        Tooltip polyphen2HvarTp = new Tooltip(PredictionTools.POLYPHEN2_HVAR.getDesc());
        polyphen2HvarTp.setShowDelay(Duration.ZERO);
        Tooltip.install(polyphen2HvarHelp, polyphen2HvarTp);
        Tooltip polyphen2HdivTp = new Tooltip(PredictionTools.POLYPHEN2_HDIV.getDesc());
        polyphen2HdivTp.setShowDelay(Duration.ZERO);
        Tooltip.install(polyphen2HdivHelp, polyphen2HdivTp);
        Tooltip vest4Tp = new Tooltip(PredictionTools.VEST4.getDesc());
        vest4Tp.setShowDelay(Duration.ZERO);
        Tooltip.install(vest4Help, vest4Tp);
        Tooltip mvpTp = new Tooltip(PredictionTools.MVP.getDesc());
        mvpTp.setShowDelay(Duration.ZERO);
        Tooltip.install(mvpHelp, mvpTp);

        Tooltip caddRawTp = new Tooltip(PredictionTools.CADD_RAW.getDesc());
        caddRawTp.setShowDelay(Duration.ZERO);
        Tooltip.install(caddRawHelp, caddRawTp);
        Tooltip caddPhredTp = new Tooltip(PredictionTools.CADD_PHRED.getDesc());
        caddPhredTp.setShowDelay(Duration.ZERO);
        Tooltip.install(caddPhredHelp, caddPhredTp);
        Tooltip mcapTp = new Tooltip(PredictionTools.MCAP.getDesc());
        mcapTp.setShowDelay(Duration.ZERO);
        Tooltip.install(mcapHelp, mcapTp);
        Tooltip metaLRTp = new Tooltip(PredictionTools.MetaLR.getDesc());
        metaLRTp.setShowDelay(Duration.ZERO);
        Tooltip.install(metaLRHelp, metaLRTp);
        Tooltip metaSVMTp = new Tooltip(PredictionTools.MetaSVM.getDesc());
        metaSVMTp.setShowDelay(Duration.ZERO);
        Tooltip.install(metaSVMHelp, metaSVMTp);
        Tooltip revelTp = new Tooltip(PredictionTools.REVEL.getDesc());
        revelTp.setShowDelay(Duration.ZERO);
        Tooltip.install(revelHelp, revelTp);

        Tooltip spliceAiTp = new Tooltip(PredictionTools.SPLICE_AI.getDesc());
        spliceAiTp.setShowDelay(Duration.ZERO);
        Tooltip.install(spliceAIHelp, spliceAiTp);
        Tooltip dbscSVNAdaTp = new Tooltip(PredictionTools.dbscSNV_ADA.getDesc());
        dbscSVNAdaTp.setShowDelay(Duration.ZERO);
        Tooltip.install(dbscSNVADAHelp, dbscSVNAdaTp);
        Tooltip dbscSVNRfTp = new Tooltip(PredictionTools.dbscSNV_RF.getDesc());
        dbscSVNRfTp.setShowDelay(Duration.ZERO);
        Tooltip.install(dbscSNVRFHelp, dbscSVNRfTp);
    }


    private void initGauges() {

        gerpGauge = new PredictionGauge3(-12.3, 6.17, null, null);
        phastCons100WayGauge = new PredictionGauge3(0, 1, null, null);
        phastCons30WayGauge = new PredictionGauge3(0, 1, null, null);
        phylop100WayGauge = new PredictionGauge3(-20, 10.003, null, null);
        phylop30WayGauge = new PredictionGauge3(-20, 1.312, null, null);
        siphyGauge = new PredictionGauge3(0, 37.9718, null, null);

        List<PredictionGaugeLabel> fathmmLabels = new ArrayList<>();
        fathmmLabels.add(new PredictionGaugeLabel(-1.5f, "D"));
        List<Double> fathmmStops = new ArrayList<>();
        fathmmStops.add(-2d);
        fathmmStops.add(-1d);
        fathmmStops.add(0d);
        fathmmGauge = new PredictionGauge3(-16.13, 10.64, true, fathmmStops, fathmmLabels);

        List<PredictionGaugeLabel> polyphen2HvarLabels = new ArrayList<>();
        polyphen2HvarLabels.add(new PredictionGaugeLabel(0.909f, "D"));
        polyphen2HvarLabels.add(new PredictionGaugeLabel(0.447f, "P"));
        polyphen2HvarGauge = new PredictionGauge3(0, 1, null, polyphen2HvarLabels);

        List<PredictionGaugeLabel> polyphen2HdivLabels = new ArrayList<>();
        polyphen2HdivLabels.add(new PredictionGaugeLabel(0.957f, "D"));
        polyphen2HdivLabels.add(new PredictionGaugeLabel(0.452f, "P"));
        polyphen2HdivGauge = new PredictionGauge3(0, 1, null, polyphen2HdivLabels);

        List<PredictionGaugeLabel> siftLabels = new ArrayList<>();
        siftLabels.add(new PredictionGaugeLabel(0.05f, "D"));
        List<Double> siftStops = new ArrayList<>();
        siftStops.add(0.05d);
        siftStops.add(0.1d);
        siftStops.add(1d);
        siftGauge = new PredictionGauge3(0, 1, true, siftStops, siftLabels);

        vest4Gauge = new PredictionGauge3(0, 1, null, null);

        List<PredictionGaugeLabel> mvpLabels = new ArrayList<>();
        mvpLabels.add(new PredictionGaugeLabel(0.7f, "D"));
        List<Double> mvpStops = new ArrayList<>();
        mvpStops.add(0.0d);
        mvpStops.add(0.65d);
        mvpStops.add(0.75d);
        mvpGauge = new PredictionGauge3(0, 1, mvpStops, mvpLabels);

        caddRawGauge = new PredictionGauge3(-6.458163, 18.301497, null, null);
        caddPhredGauge = new PredictionGauge3(0, 50, null, null);

        List<PredictionGaugeLabel> mcapLabels = new ArrayList<>();
        mcapLabels.add(new PredictionGaugeLabel(0.025f, "D"));
        List<Double> mcapStops = new ArrayList<>();
        mcapStops.add(0d);
        mcapStops.add(0.024d);
        mcapStops.add(0.025d);
        mcapGauge = new PredictionGauge3(0, 1, mcapStops, mcapLabels);

        List<PredictionGaugeLabel> metaLRLabels = new ArrayList<>();
        metaLRLabels.add(new PredictionGaugeLabel(0.5f, "D"));
        List<Double> metaLRStops = new ArrayList<>();
        metaLRStops.add(0d);
        metaLRStops.add(0.45d);
        metaLRStops.add(0.50d);
        metaLRGauge = new PredictionGauge3(0, 1, metaLRStops, metaLRLabels);

        List<PredictionGaugeLabel> metaSVMLabels = new ArrayList<>();
        metaSVMLabels.add(new PredictionGaugeLabel(0f, "D"));
        List<Double> metaSVMStops = new ArrayList<>();
        metaSVMStops.add(-2d);
        metaSVMStops.add(-0.1d);
        metaSVMStops.add(0d);
        metaSVMGauge = new PredictionGauge3(-2, 3, metaSVMStops, metaSVMLabels);

        revelGauge = new PredictionGauge3(0, 1, null, null);

        List<PredictionGaugeLabel> spliceAILabels = new ArrayList<>();
        spliceAILabels.add(new PredictionGaugeLabel(0.8f, "D"));
        List<Double> spliceAIStops = new ArrayList<>();
        spliceAIStops.add(0d);
        spliceAIStops.add(0.4d);
        spliceAIStops.add(0.8d);
        spliceAIDGGauge = new PredictionGauge3(0, 1, spliceAIStops, spliceAILabels);
        spliceAIDLGauge = new PredictionGauge3(0, 1, spliceAIStops, spliceAILabels);
        spliceAIAGGauge = new PredictionGauge3(0, 1, spliceAIStops, spliceAILabels);
        spliceAIALGauge = new PredictionGauge3(0, 1, spliceAIStops, spliceAILabels);

        List<PredictionGaugeLabel> dbscSNVLabels = new ArrayList<>();
        dbscSNVLabels.add(new PredictionGaugeLabel(0.6f, "D"));
        List<Double> dbscSNVStops = new ArrayList<>();
        dbscSNVStops.add(0d);
        dbscSNVStops.add(0.58d);
        dbscSNVStops.add(0.6d);
        dbscSNVADAGauge = new PredictionGauge3(0, 1, dbscSNVStops, dbscSNVLabels);
        dbscSNVRFGauge = new PredictionGauge3(0, 1, dbscSNVStops, dbscSNVLabels);

        predictionGauges.add(gerpGauge);
        predictionGauges.add(phastCons100WayGauge);
        predictionGauges.add(phastCons30WayGauge);
        predictionGauges.add(phylop100WayGauge);
        predictionGauges.add(phylop30WayGauge);
        predictionGauges.add(siphyGauge);
        predictionGauges.add(fathmmGauge);
        predictionGauges.add(polyphen2HdivGauge);
        predictionGauges.add(polyphen2HvarGauge);
        predictionGauges.add(siftGauge);
        predictionGauges.add(vest4Gauge);
        predictionGauges.add(mvpGauge);
        predictionGauges.add(caddRawGauge);
        predictionGauges.add(caddPhredGauge);
        predictionGauges.add(mcapGauge);
        predictionGauges.add(metaLRGauge);
        predictionGauges.add(metaSVMGauge);
        predictionGauges.add(revelGauge);
        predictionGauges.add(spliceAIDGGauge);
        predictionGauges.add(spliceAIDLGauge);
        predictionGauges.add(spliceAIAGGauge);
        predictionGauges.add(spliceAIALGauge);
        predictionGauges.add(dbscSNVADAGauge);
        predictionGauges.add(dbscSNVRFGauge);
    }

    private void initGridPanes() {
        // conservation
        int rowIdx = 1;
        int colIdx = 1;
        conservationGridPane.add(gerpGauge, colIdx, rowIdx++);
        conservationGridPane.add(phastCons100WayGauge, colIdx, rowIdx++);
        conservationGridPane.add(phastCons30WayGauge, colIdx, rowIdx++);
        conservationGridPane.add(phylop100WayGauge, colIdx, rowIdx++);
        conservationGridPane.add(phylop30WayGauge, colIdx, rowIdx++);
        conservationGridPane.add(siphyGauge, colIdx, rowIdx);

        // functional
        rowIdx = 1;
        functionalGridPane.add(fathmmGauge, colIdx, rowIdx++);
        functionalGridPane.add(polyphen2HdivGauge, colIdx, rowIdx++);
        functionalGridPane.add(polyphen2HvarGauge, colIdx, rowIdx++);
        functionalGridPane.add(siftGauge, colIdx, rowIdx++);
        functionalGridPane.add(vest4Gauge, colIdx, rowIdx++);
        functionalGridPane.add(mvpGauge, colIdx, rowIdx);

        // global
        rowIdx = 1;
        globalGridPane.add(caddRawGauge, colIdx, rowIdx++);
        globalGridPane.add(caddPhredGauge, colIdx, rowIdx++);
        globalGridPane.add(mcapGauge, colIdx, rowIdx++);
        globalGridPane.add(metaLRGauge, colIdx, rowIdx++);
        globalGridPane.add(metaSVMGauge, colIdx, rowIdx++);
        globalGridPane.add(revelGauge, colIdx, rowIdx);

        // SpliceAI
        rowIdx = 1;
        colIdx = 2;
        spliceAIGridPane.add(spliceAIDGGauge, colIdx, rowIdx++);
        spliceAIGridPane.add(spliceAIDLGauge, colIdx, rowIdx++);
        spliceAIGridPane.add(spliceAIAGGauge, colIdx, rowIdx++);
        spliceAIGridPane.add(spliceAIALGauge, colIdx, rowIdx);

        // dbscSNV
        rowIdx = 1;
        dbscSNVGridPane.add(dbscSNVADAGauge, colIdx, rowIdx++);
        dbscSNVGridPane.add(dbscSNVRFGauge, colIdx, rowIdx);
    }

    private void fillFields() {
        clearFields();
        variantTf.setText(getValue().toString());
        if (getValue().getTranscriptConsequence() != null) {
            TranscriptConsequence tc = getValue().getTranscriptConsequence();
            if (tc.getGerpPred() != null) {
                gerpScoreTf.setText(getScoreAndPred(tc.getGerpPred()));
                gerpGauge.setScore(tc.getGerpPred().getScore().floatValue());
            }
            if (tc.getPhastCons100WayPred() != null) {
                phastCons100WayScoreTf.setText(getScoreAndPred(tc.getPhastCons100WayPred()));
                phastCons100WayGauge.setScore(tc.getPhastCons100WayPred().getScore().floatValue());
            }
            if (tc.getPhastCons30WayPred() != null) {
                phastCons30WayScoreTf.setText(getScoreAndPred(tc.getPhastCons30WayPred()));
                phastCons30WayGauge.setScore(tc.getPhastCons30WayPred().getScore().floatValue());
            }
            if (tc.getPhylop100WayPred() != null) {
                phylop100WayScoreTf.setText(getScoreAndPred(tc.getPhylop100WayPred()));
                phylop100WayGauge.setScore(tc.getPhylop100WayPred().getScore().floatValue());
            }
            if (tc.getPhylop30WayPred() != null) {
                phylop30WayScoreTf.setText(getScoreAndPred(tc.getPhylop30WayPred()));
                phylop30WayGauge.setScore(tc.getPhylop30WayPred().getScore().floatValue());
            }
            if (tc.getSiphyPred() != null) {
                siphyScoreTf.setText(getScoreAndPred(tc.getSiphyPred()));
                siphyGauge.setScore(tc.getSiphyPred().getScore().floatValue());
            }
            if (tc.getFathmmPred() != null) {
                fathmmScoreTf.setText(getScoreAndPred(tc.getFathmmPred()));
                fathmmGauge.setScore(tc.getFathmmPred().getScore().floatValue());
            }
            if (tc.getSiftPred() != null) {
                siftScoreTf.setText(getScoreAndPred(tc.getSiftPred()));
                siftGauge.setScore(tc.getSiftPred().getScore().floatValue());
            }
            if (tc.getPolyphen2HvarPred() != null) {
                polyphen2HvarScoreTf.setText(getScoreAndPred(tc.getPolyphen2HvarPred()));
                polyphen2HvarGauge.setScore(tc.getPolyphen2HvarPred().getScore().floatValue());
            }
            if (tc.getPolyphen2HdivPred() != null) {
                polyphen2HdivScoreTf.setText(getScoreAndPred(tc.getPolyphen2HdivPred()));
                polyphen2HdivGauge.setScore(tc.getPolyphen2HdivPred().getScore().floatValue());
            }
            if (tc.getVest4Pred() != null) {
                vest4ScoreTf.setText(getScoreAndPred(tc.getVest4Pred()));
                vest4Gauge.setScore(tc.getVest4Pred().getScore().floatValue());
            }
            if (tc.getMvpPred() != null) {
                mvpScoreTf.setText(getScoreAndPred(tc.getMvpPred()));
                mvpGauge.setScore(tc.getMvpPred().getScore().floatValue());
            }
            if (tc.getCaddRawPred() != null) {
                caddRawScoreTf.setText(getScoreAndPred(tc.getCaddRawPred()));
                caddRawGauge.setScore(tc.getCaddRawPred().getScore().floatValue());
            }
            if (tc.getCaddPhredPred() != null) {
                caddPhredScoreTf.setText(getScoreAndPred(tc.getCaddPhredPred()));
                caddPhredGauge.setScore(tc.getCaddPhredPred().getScore().floatValue());
            }
            if (tc.getMcapPred() != null) {
                mcapScoreTf.setText(getScoreAndPred(tc.getMcapPred()));
                mcapGauge.setScore(tc.getMcapPred().getScore().floatValue());
            }
            if (tc.getMetaLRPred() != null) {
                metaLRScoreTf.setText(getScoreAndPred(tc.getMetaLRPred()));
                metaLRGauge.setScore(tc.getMetaLRPred().getScore().floatValue());
            }
            if (tc.getMetaSVMPred() != null) {
                metaSVMScoreTf.setText(getScoreAndPred(tc.getMetaSVMPred()));
                metaSVMGauge.setScore(tc.getMetaSVMPred().getScore().floatValue());
            }
            if (tc.getRevelPred() != null) {
                revelScoreTf.setText(getScoreAndPred(tc.getRevelPred()));
                revelGauge.setScore(tc.getRevelPred().getScore().floatValue());
            }

            if (tc.getSpliceAIPreds() != null) {

                spliceAIDGPosTf.setText(tc.getSpliceAIPreds().getDonnorGainPred().getPosition() + "pb");
                spliceAIDGScoreTf.setText(tc.getSpliceAIPreds().getDonnorGainPred().getScore() < 0 ? null : tc.getSpliceAIPreds().getDonnorGainPred().getPrintableScore());
                spliceAIDGGauge.setScore((float)tc.getSpliceAIPreds().getDonnorGainPred().getScore());

                spliceAIDLPosTf.setText(tc.getSpliceAIPreds().getDonnorLossPred().getPosition() + "pb");
                spliceAIDLScoreTf.setText(tc.getSpliceAIPreds().getDonnorLossPred().getScore() < 0 ? null : tc.getSpliceAIPreds().getDonnorLossPred().getPrintableScore());
                spliceAIDLGauge.setScore((float)tc.getSpliceAIPreds().getDonnorLossPred().getScore());

                spliceAIAGPosTf.setText(tc.getSpliceAIPreds().getAcceptorGainPred().getPosition() + "pb");
                spliceAIAGScoreTf.setText(tc.getSpliceAIPreds().getAcceptorGainPred().getScore() < 0 ? null : tc.getSpliceAIPreds().getAcceptorGainPred().getPrintableScore());
                spliceAIAGGauge.setScore((float)tc.getSpliceAIPreds().getAcceptorGainPred().getScore());

                spliceAIALPosTf.setText(tc.getSpliceAIPreds().getAcceptorLossPred().getPosition() + "pb");
                spliceAIALScoreTf.setText(tc.getSpliceAIPreds().getAcceptorLossPred().getScore() < 0 ? null : tc.getSpliceAIPreds().getAcceptorLossPred().getPrintableScore());
                spliceAIALGauge.setScore((float)tc.getSpliceAIPreds().getAcceptorLossPred().getScore());
            }

            if (tc.getDbscSNVPreds() != null) {
                if (tc.getDbscSNVPreds().getAdaScore() != null) {
                    dbscSNVAdaScoreTf.setText(String.valueOf(tc.getDbscSNVPreds().getAdaScore()));
                    dbscSNVADAGauge.setScore(tc.getDbscSNVPreds().getAdaScore());
                }
                if (tc.getDbscSNVPreds().getRfScore() != null) {
                    dbscSNVRfScoreTf.setText(String.valueOf(tc.getDbscSNVPreds().getRfScore()));
                    dbscSNVRFGauge.setScore(tc.getDbscSNVPreds().getRfScore());
                }
            }
        }
    }

    private String getSpliceAIGenomicPosition(SpliceAIPredictions.SpliceAIPrediction spliceAIPrediction) {
        if (spliceAIPrediction == null) {
            return null;
        }
        int genomicPos = getValue().getGenomicVariant().getStart() + spliceAIPrediction.getPosition();
        return getValue().getGenomicVariant().getContig() + ":" + genomicPos;
    }

    private void clearFields() {
        textfields.forEach(t -> t.setText(null));
        predictionGauges.stream().filter(Objects::nonNull).forEach(g -> g.setScore(null));
    }


    private String getScoreAndPred(VariantPrediction vp) {
        if (vp.getScore() == null && vp.getPrediction() == null) {
            return null;
        } else {
            String scoreAndPred = "";
            if (vp.getScore() != null) {
                scoreAndPred += String.valueOf(vp.getScore());
            }
            if (vp.getPrediction() != null) {
                if (vp.getScore() != null) {
                    scoreAndPred += " ";
                }
                scoreAndPred += "(" + vp.getPrediction() + ")";
            }
            return scoreAndPred;
        }
    }

    public VBox getDialogContainer() {return dialogContainer;}
}
