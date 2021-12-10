package ngsdiaglim.controllers.analysisview;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.controllers.cells.AnnotationCommentaryListCell;
import ngsdiaglim.controllers.cells.VariantCommentaryListCell;
import ngsdiaglim.controllers.cells.variantsTableCells.TranscriptTableCell;
import ngsdiaglim.controllers.charts.PredictionChartItem;
import ngsdiaglim.controllers.charts.PredictionsChart;
import ngsdiaglim.controllers.charts.VAFChart;
import ngsdiaglim.controllers.charts.VafScatterChart;
import ngsdiaglim.controllers.dialogs.*;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.enumerations.HotspotType;
import ngsdiaglim.enumerations.PredictionTools;
import ngsdiaglim.enumerations.SangerState;
import ngsdiaglim.modeles.FastaSequenceGetter;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.ExternalVariation;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.*;
import ngsdiaglim.modeles.variants.predictions.PredictionToolsScore;
import ngsdiaglim.modules.ModuleManager;
import ngsdiaglim.utils.BrowserUtils;
import ngsdiaglim.utils.ExternalDatabasesUtils;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.VariantUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnalysisViewVariantDetailController extends ScrollPane {

    private final Logger logger = LogManager.getLogger(AnalysisViewVariantDetailController.class);

    @FXML private HBox test;
    @FXML private TextField cytobandTf;
    @FXML private TextField geneTf;
    @FXML private CustomTextField transcriptTf;
    @FXML private TextField hgvscTf;
    @FXML private TextField hgvspTf;
    @FXML private TextField positionTf;
    @FXML private TextField depthTf;
    @FXML private TextField alleleDepthTf;
    @FXML private Label clinvarLb;
    @FXML private TextField databaseFqTf;
    @FXML private TextField runFqTf;
    @FXML private TextField gnomadMaxTf;
    @FXML private TextField gnomadAfricanTf;
    @FXML private TextField gnomadJewishTf;
    @FXML private TextField gnomadEastAsianTf;
    @FXML private TextField gnomadEuropenan_nonFTf;
    @FXML private TextField gnomadEuropean_FTf;
    @FXML private TextField gnomadLatinoTf;
    @FXML private TextField gnomadSouthAsianTf;
    @FXML private TextField hotspotTf;
    @FXML private TextField hotspotPositionTf;
    @FXML private TextField pathogenicityTf;
    @FXML private TextField falsePositiveTf;
    @FXML private TextField sangerStateTf;
    @FXML private TextField consequencesTf;
    @FXML private TextField leftSeqTf;
    @FXML private TextField alt1SeqTf;
    @FXML private TextField alt2SeqTf;
    @FXML private TextField rightSeqTf;

    @FXML private ToggleSwitch addToReportTs;

    @FXML private AnchorPane alleleDepthChartAp;
    @FXML private AnchorPane predictionChartAp;
    @FXML private FlowPane externalVariationsContainer;
    @FXML private FlowPane pubmedIdsContainer;

    @FXML private Button showEnsemblConsequencesBtn;
    @FXML private Button editPathogenicityBtn;
    @FXML private Button editFalsePositiveBtn;
    @FXML private Button editSangerStateBtn;
    @FXML private Button showSameVariantsBtn;
    @FXML private Button addVariantCommentaryBtn;
    @FXML private Button addAnnotationCommentaryBtn;
    @FXML private Button igvLinkBtn;
    @FXML private Button gnomadLinkBtn;
    @FXML private Button clinvarLinkBtn;
    @FXML private Button dbsnpLinkBtn;
    @FXML private Button ensemblLinkBtn;
    @FXML private Button cosmicLinkBtn;
    @FXML private Button cosmicGeneViewerLinkBtn;
    @FXML private Button varsomeLinkBtn;
    @FXML private Button lovdLinkBtn;
    @FXML private Button oncoKbLinkBtn;
    @FXML private Button intogenLinkBtn;
    @FXML private Button alamutLinkBtn;

    @FXML private ListView<VariantCommentary> variantCommentaryLv;
    @FXML private ListView<AnnotationCommentary> annotationCommentaryLv;

    @FXML private TabPane commentariesTabpane;
    @FXML private Tab variantCommentTab;

    private final VafScatterChart vafScatterChart = new VafScatterChart(400, 250);

    private final static String false_positive_textfield = "false-positive-textfield";
    private final static String true_positive_textfield = "true-positive-textfield";
    private final static String pathogenic_textfield_class = "pathogenic-textfield";
    private final static String likely_pathogenic_textfield_class = "likely-pathogenic-textfield";
    private final static String uncertain_significance_textfield_class = "uncertain-significance-textfield";
    private final static String likely_benin_textfield_class = "likely-benin-textfield";
    private final static String benin_textfield_class = "benin-textfield";
    private final static String sanger_state_textfield_class = "sanger-state-textfield";

    private final FontIcon icon = new FontIcon("mdmz-menu");
    private final TranscriptTableCell.TranscriptPopOver popover = new TranscriptTableCell.TranscriptPopOver();

    private final VAFChart vafChart = new VAFChart();
    private final PredictionsChart predictionsChart = new PredictionsChart(1, 130);
    private final List<TextField> textFieldsFitToText = new ArrayList<>();

    private final PredictionsDetailDialog predictionsDetailDialog = new PredictionsDetailDialog();

    private final SimpleObjectProperty<Annotation> annotation = new SimpleObjectProperty<>();
    ObservableList<SearchVariantResult> searchVariantResults = null;

    private Thread getSequenceThread;
    private Thread getSangerStateThread;
    private FastaSequenceGetter fastaSequenceGetter;

    // Event
    private ChangeListener<Boolean> falsePositiveListener;
    private ChangeListener<Boolean> addToReportListener;
    private ChangeListener<ACMG> pathogenicityListener;


    public AnalysisViewVariantDetailController() {

        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewVariantDetail.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Platform.runLater(() -> Message.error(e.getMessage(), e));
        }

        try {
            fastaSequenceGetter = new FastaSequenceGetter(new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.REFERENCE_HG19.name())));
        } catch (Exception e) {
            logger.error("Error when getting fasta sequence", e);
        }
    }

    @FXML
    public void initialize() {
        initalizeTextfields();
        initTranscriptTextfield();
        initListeners();
        alleleDepthChartAp.getChildren().setAll(vafChart);
        predictionChartAp.getChildren().setAll(predictionsChart);

        annotation.addListener((obs, oldV, newV) -> {
            searchVariantResults = null;
            if (oldV != null) {
                oldV.getVariant().falsePositiveProperty().removeListener(falsePositiveListener);
                oldV.getVariant().acmgProperty().removeListener(pathogenicityListener);
            }
            if (newV != null) {
                fillAnnotationRelatedFields();
                annotation.get().transcriptConsequenceProperty().addListener((obs2, oldV2, newV2) -> {
                    if (newV2 != null) {
                        fillTranscriptRelatedFields();
                    }
                });
                annotation.get().getVariant().acmgProperty().addListener(((observableValue, acmg, t1) -> fillAnnotationRelatedFields()));
            }
        });

        variantCommentaryLv.setCellFactory(data -> new VariantCommentaryListCell());
        annotationCommentaryLv.setCellFactory(data -> new AnnotationCommentaryListCell());
        annotationCommentaryLv.setSelectionModel(null);

        commentariesTabpane.getSelectionModel().select(variantCommentTab);

        hotspotTf.textProperty().addListener(initHotspotStyleListener());
    }

    private void initalizeTextfields() {
        // adjust some textfields to te text width
        textFieldsFitToText.add(cytobandTf);
        textFieldsFitToText.add(geneTf);
        textFieldsFitToText.add(transcriptTf);
        textFieldsFitToText.add(hgvscTf);
        textFieldsFitToText.add(hgvspTf);
        textFieldsFitToText.add(positionTf);
        textFieldsFitToText.add(alleleDepthTf);
        textFieldsFitToText.add(consequencesTf);
        textFieldsFitToText.add(leftSeqTf);
        textFieldsFitToText.add(alt1SeqTf);
        textFieldsFitToText.add(alt2SeqTf);
        textFieldsFitToText.add(rightSeqTf);
        textFieldsFitToText.add(databaseFqTf);
        textFieldsFitToText.add(runFqTf);
        textFieldsFitToText.add(gnomadAfricanTf);
        textFieldsFitToText.add(gnomadEastAsianTf);
        textFieldsFitToText.add(gnomadEuropean_FTf);
        textFieldsFitToText.add(gnomadJewishTf);
        textFieldsFitToText.add(gnomadEuropenan_nonFTf);
        textFieldsFitToText.add(gnomadLatinoTf);
        textFieldsFitToText.add(gnomadMaxTf);
        textFieldsFitToText.add(gnomadSouthAsianTf);
        textFieldsFitToText.add(alleleDepthTf);
        textFieldsFitToText.add(depthTf);
        textFieldsFitToText.add(hotspotTf);
        textFieldsFitToText.add(hotspotPositionTf);

        for (TextField tf : textFieldsFitToText) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
                Text text = new Text(newValue);
                text.setFont(tf.getFont()); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                        + tf.getPadding().getLeft() + tf.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                if (tf instanceof CustomTextField) {
                    CustomTextField ctf = (CustomTextField) tf;
                    if (ctf.getLeft() != null) {
                        width += ctf.getLeft().layoutBoundsProperty().get().getWidth() + 5d;
                    }
                    if (ctf.getRight() != null) {
                        width += ctf.getRight().layoutBoundsProperty().get().getWidth() + 5d;
                    }
                }
                tf.setMinWidth(width); // Set the width
                tf.setPrefWidth(width); // Set the width
                tf.positionCaret(tf.getCaretPosition()); // If you remove this line, it flashes a little bit
            }));
        }

        sangerStateTf.getStyleClass().add(sanger_state_textfield_class);
    }


    private void initListeners() {
        falsePositiveListener = (obs, oldV, newV) -> setFalsePositiveStyle();
        pathogenicityListener = (obs, oldV, newV) -> setFalsePathogenicityStyle();
        addToReportListener = (obs, oldV, newV) -> {
            if (newV != null && annotation.get() != null) {
                annotation.get().setReported(newV);
            }
        };
    }


    private void initTranscriptTextfield() {
        transcriptTf.setLeft(icon);


        popover.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
        popover.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        icon.setOnMouseClicked(e -> popover.show(icon));

        popover.selectedTranscriptProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                if (popover.setForAllVariants()) {
                    ModuleManager.getAnalysisViewController().getVariantsViewController().setVisibleTranscript(newV.getTranscript());
                }
                else {
                    Annotation annotation = annotationProperty().get();
                    if (annotation != null) {
                        annotation.setTranscriptConsequence(newV);
                        ModuleManager.getAnalysisViewController().getVariantsViewController().refreshTable();
                    }
                }
                User loggedUser = App.get().getLoggedUser();
                loggedUser.setPreference(DefaultPreferencesEnum.SELECT_TRANSCRIPT_FOR_ALL_VARIANTS, popover.setForAllVariants());
                try {
                    DAOController.get().getUsersDAO().updatePreferences(loggedUser);
                } catch (SQLException e) {
                    logger.error(e);
                }
                popover.hide();
            }
        });
    }


    private void initTranscriptPopOver() {
        popover.clear();
        if (annotation.get() != null) {
            for (String transcript : annotation.get().getTranscriptConsequences().keySet()) {
                popover.addTranscript(annotation.get().getTranscriptConsequences().get(transcript));
            }
        }
    }


    private void fillAnnotationRelatedFields() {
        Annotation a = annotation.get();

        a.getVariant().falsePositiveProperty().addListener(falsePositiveListener);
        a.getVariant().acmgProperty().addListener(pathogenicityListener);

        setFalsePositiveStyle();
        setFalsePathogenicityStyle();
        try {
            cytobandTf.setText(a.getVariant().getCytoband() != null ? a.getVariant().getCytoband().getName() : null);
        } catch (IOException e) {
            logger.error(e);
        }
        positionTf.setText(VariantUtils.getHashVariant(a.getVariant()));
        depthTf.setText(String.valueOf(a.getDepth()));
        alleleDepthTf.setText(a.getAllelesDepth());

        databaseFqTf.setText(String.valueOf(a.getVariant().getOccurrence()));
        runFqTf.setText(String.valueOf(a.getVariant().getOccurrenceInRun()));
        gnomadMaxTf.setText(a.getGnomADFrequencies().getMax() == null ? null : a.getGnomADFrequencies().getMax().toString());
        gnomadAfricanTf.setText(a.getGnomADFrequencies().getAfr() == null ? null : a.getGnomADFrequencies().getAfr().toString());
        gnomadJewishTf.setText(a.getGnomADFrequencies().getAsj() == null ? null : a.getGnomADFrequencies().getAsj().toString());
        gnomadEastAsianTf.setText(a.getGnomADFrequencies().getEas() == null ? null : a.getGnomADFrequencies().getEas().toString());
        gnomadSouthAsianTf.setText(a.getGnomADFrequencies().getSas() == null ? null : a.getGnomADFrequencies().getSas().toString());
        gnomadEuropenan_nonFTf.setText(a.getGnomADFrequencies().getNfe() == null ? null : a.getGnomADFrequencies().getNfe().toString());
        gnomadEuropean_FTf.setText(a.getGnomADFrequencies().getFin() == null ? null : a.getGnomADFrequencies().getFin().toString());
        gnomadLatinoTf.setText(a.getGnomADFrequencies().getAmr() == null ? null : a.getGnomADFrequencies().getAmr().toString());
        pathogenicityTf.setText(a.getVariant().getAcmg().getName());

        if (annotation.get().getVariant().isHotspot()) {
            Hotspot hotspot = annotation.get().getVariant().getHotspot();
            hotspotTf.setText(hotspot.getHotspotId());
            if (hotspot.getType().equals(HotspotType.POINT_MUTATION)) {
                hotspotPositionTf.setText(hotspot.getContig() + ":" + hotspot.getStart() + ":" + hotspot.getRef() + ">" + hotspot.getAlt());
            } else {
                hotspotPositionTf.setText(hotspot.getContig() + ":" + hotspot.getStart() + "-" + hotspot.getEnd());
            }
        } else {
            hotspotTf.setText(null);
            hotspotPositionTf.setText(null);
        }

        vafChart.vafValueProperty().bind(a.vafProperty());

//        addToReportTs.selectedProperty().bindBidirectional(a.reportedProperty());
        addToReportTs.selectedProperty().removeListener(addToReportListener);
        addToReportTs.setSelected(a.isReported());
        addToReportTs.selectedProperty().addListener(addToReportListener);
        fillTranscriptRelatedFields();
        initTranscriptPopOver();
        loadVariantCommentaries();
        loadAnnotationCommentaries();
        getSequence();
        getSangerState();
        setExternalLinksButtons();
    }

    private void fillTranscriptRelatedFields() {
        TranscriptConsequence t = annotation.get().getTranscriptConsequence();
        if (t != null) {
            geneTf.setText(t.getGeneName());
            transcriptTf.setText(t.getTranscript().getName());

            hgvscTf.setText(t.getHgvsc());
            hgvspTf.setText(t.getHgvsp());
            consequencesTf.setText(t.getConsequence().getName());
            if (t.getClinvarSign() != null) {
                clinvarLb.setText(t.getClinvarSign().replaceAll(";", "\n"));
            } else {
                clinvarLb.setText(null);
            }
        }
        drawPathogenicChart();
        fillExternalVariations();
        fillPubmedIds();
    }

    private void drawPathogenicChart() {

        List<PredictionChartItem> items = new ArrayList<>();

        // polyphen2 hdiv
        float polyphen_hdiv_score = -1;
        String polyphen_hdiv_subtitle = "N/A";
        Color polyphen_hdiv_color = null;
        if (annotation.get().getTranscriptConsequence().getPolyphen2HdivPred() != null) {
            polyphen_hdiv_score = PredictionToolsScore.scalePolyphenScore(annotation.get().getTranscriptConsequence().getPolyphen2HdivPred().getScore().floatValue());
            polyphen_hdiv_subtitle = String.valueOf(annotation.get().getTranscriptConsequence().getPolyphen2HdivPred().getScore());
            polyphen_hdiv_color = PredictionToolsScore.getPolyphenScoreColor(annotation.get().getTranscriptConsequence().getPolyphen2HdivPred().getScore().floatValue());
        }
        PredictionChartItem polyphen2HdivItem = new PredictionChartItem(PredictionTools.POLYPHEN2_HDIV.getName(),
                polyphen_hdiv_subtitle,
                PredictionTools.POLYPHEN2_HDIV.getDesc(),
                polyphen_hdiv_score,
                polyphen_hdiv_color);
        items.add(polyphen2HdivItem);

        // polyphen2 hdiv
        float polyphen_hvar_score = -1;
        String polyphen_hvar_subtitle = "N/A";
        Color polyphen_hvar_color = null;
        if (annotation.get().getTranscriptConsequence().getPolyphen2HvarPred() != null) {
            polyphen_hvar_score = PredictionToolsScore.scalePolyphenScore(annotation.get().getTranscriptConsequence().getPolyphen2HvarPred().getScore().floatValue());
            polyphen_hvar_subtitle = String.valueOf(annotation.get().getTranscriptConsequence().getPolyphen2HvarPred().getScore());
            polyphen_hvar_color = PredictionToolsScore.getPolyphenScoreColor(annotation.get().getTranscriptConsequence().getPolyphen2HvarPred().getScore().floatValue());
        }
        PredictionChartItem polyphen2HvarItem = new PredictionChartItem(PredictionTools.POLYPHEN2_HVAR.getName(),
                polyphen_hvar_subtitle,
                PredictionTools.POLYPHEN2_HVAR.getDesc(),
                polyphen_hvar_score,
                polyphen_hvar_color);
        items.add(polyphen2HvarItem);

        // sift
        float sift_score = -1;
        String sift_subtitle = "N/A";
        Color sift_color = null;
        if (annotation.get().getTranscriptConsequence().getSiftPred() != null) {
            float sift_raw_score = annotation.get().getTranscriptConsequence().getSiftPred().getScore().floatValue();
            sift_score = PredictionToolsScore.scaleSiftScore(sift_raw_score);
            sift_subtitle = String.valueOf(sift_raw_score);
            sift_color = PredictionToolsScore.getSiftScoreColor(sift_raw_score);
        }
        PredictionChartItem siftItem = new PredictionChartItem(PredictionTools.SIFT.getName(),
                sift_subtitle,
                PredictionTools.SIFT.getDesc(),
                sift_score,
                sift_color);
        items.add(siftItem);

        // cadd
        float cadd_score = -1;
        String cadd_subtitle = "N/A";
        Color cadd_color = null;
        if (annotation.get().getTranscriptConsequence().getCaddPhredPred() != null) {
            float cadd_raw_score = annotation.get().getTranscriptConsequence().getCaddPhredPred().getScore().floatValue();
            cadd_score = PredictionToolsScore.scaleCaddPhredScore(cadd_raw_score);
            cadd_subtitle = String.valueOf(cadd_raw_score);
            cadd_color = PredictionToolsScore.getCaddPhredScoreColor(cadd_raw_score);
        }
        PredictionChartItem caddItem = new PredictionChartItem(PredictionTools.CADD_PHRED.getName(),
                cadd_subtitle,
                PredictionTools.CADD_PHRED.getDesc(),
                cadd_score,
                cadd_color);
        items.add(caddItem);

        // revel
        float revel_score = -1;
        String revel_subtitle = "N/A";
        Color revel_color = null;
        if (annotation.get().getTranscriptConsequence().getRevelPred() != null) {
            float revel_raw_score = PredictionToolsScore.scaleRevelScore(annotation.get().getTranscriptConsequence().getRevelPred().getScore().floatValue());
            revel_score = PredictionToolsScore.scaleRevelScore(revel_raw_score);
            revel_subtitle = String.valueOf(revel_raw_score);
            revel_color = PredictionToolsScore.getRevelScoreColor(revel_raw_score);
        }
        PredictionChartItem revelItem = new PredictionChartItem(PredictionTools.REVEL.getName(),
                revel_subtitle,
                PredictionTools.REVEL.getDesc(),
                revel_score,
                revel_color);
        items.add(revelItem);


        // gerp++_rs
        float gerp_score = -1;
        String gerp_subtitle = "N/A";
        Color gerp_color = null;
        if (annotation.get().getTranscriptConsequence().getGerpPred() != null) {
            float gerp_raw_score = annotation.get().getTranscriptConsequence().getGerpPred().getScore().floatValue();
            gerp_score = PredictionToolsScore.scaleGerpScore(gerp_raw_score);
            gerp_subtitle = String.valueOf(gerp_raw_score);
            gerp_color = PredictionToolsScore.getGerpColor(gerp_raw_score);
        }
        PredictionChartItem gerpItem = new PredictionChartItem(PredictionTools.GERP.getName(),
                gerp_subtitle,
                PredictionTools.GERP.getDesc(),
                gerp_score,
                gerp_color);
        items.add(gerpItem);

        // gnomad
        float gnomad_score = 1;
        String gnomad_subtitle = "N/A";
        Color gnomad_color = PredictionToolsScore.getGnomadFreqColor(0f);
        if (annotation.get().getGnomADFrequencies().maxProperty() != null) {
            float gnomad_freq = annotation.get().getGnomADFrequencies().maxProperty().get().getAf();
            gnomad_score = PredictionToolsScore.scalePopulationFrequencie(gnomad_freq);
            gnomad_subtitle = String.valueOf(NumberUtils.round(gnomad_freq, 4));
            gnomad_color = PredictionToolsScore.getGnomadFreqColor(gnomad_freq);
        }
        PredictionChartItem gnomadItem = new PredictionChartItem("Gnomad",
                gnomad_subtitle,
                "Probably patho : freq < 0.01",
                gnomad_score,
                gnomad_color);
        items.add(gnomadItem);


        // SpliceAI
        float spliceAI_score = -1;
        String spliceAI_name = PredictionTools.SPLICE_AI.getName();
        String spliceAI_subtitle = "N/A";
        Color spliceAI_color = null;
        if (annotation.get().getTranscriptConsequence().getSpliceAIPreds() != null) {
            float spliceAI_raw_score = (float) annotation.get().getTranscriptConsequence().getSpliceAIPreds().getMostSeverePred().getScore();
            spliceAI_score = PredictionToolsScore.scaleSpliceAIScore(spliceAI_raw_score);
            spliceAI_subtitle = String.valueOf(spliceAI_score);
            spliceAI_color = PredictionToolsScore.getSpliceAIColor(spliceAI_score);
            spliceAI_name = spliceAI_name + "\n(" + annotation.get().getTranscriptConsequence().getSpliceAIPreds().getMostSeverePred().getSite().getSite() + ")";
        }
        PredictionChartItem spliceAIItem = new PredictionChartItem(spliceAI_name,
                spliceAI_subtitle,
                PredictionTools.SPLICE_AI.getDesc(),
                spliceAI_score,
                spliceAI_color);
        items.add(spliceAIItem);


        // MVP
        float mvp_score = -1;
        String mvp_subtitle = "N/A";
        Color mvp_color = null;
        if (annotation.get().getTranscriptConsequence().getMvpPred() != null) {
            float mvp_raw_score = annotation.get().getTranscriptConsequence().getMvpPred().getScore().floatValue();
            mvp_score = (float) NumberUtils.round(PredictionToolsScore.scaleMVPScore(mvp_raw_score), 3);
            mvp_subtitle = String.valueOf(NumberUtils.round(mvp_raw_score, 4));
            mvp_color = PredictionToolsScore.getMVPColor(mvp_raw_score);
        }
        PredictionChartItem mvpItem = new PredictionChartItem(PredictionTools.MVP.getName(),
                mvp_subtitle,
                PredictionTools.MVP.getDesc(),
                mvp_score,
                mvp_color);
        items.add(mvpItem);

        predictionsChart.setChartItems(items);

    }


    @FXML
    private void editPathogenicity() {
        EditVariantPathogenicityDialog editVariantPathogenicityDialog = new EditVariantPathogenicityDialog(App.get().getAppController().getDialogPane());
        editVariantPathogenicityDialog.setValue(getAnnotation().getVariant());
        Message.showDialog(editVariantPathogenicityDialog);
    }

    @FXML
    private void editFalsePositive() {
        EditVariantFalsePositiveDialog editVariantFalsePositiveDialog = new EditVariantFalsePositiveDialog(App.get().getAppController().getDialogPane());
        editVariantFalsePositiveDialog.setValue(getAnnotation().getVariant());
        Message.showDialog(editVariantFalsePositiveDialog);
    }


    @FXML
    private void editSangerState() {
        EditSangerStateDialog editSangerStateDialog = new EditSangerStateDialog(App.get().getAppController().getDialogPane());
        editSangerStateDialog.setValue(getAnnotation());
        Message.showDialog(editSangerStateDialog);
    }


    public Annotation getAnnotation() {
        return annotation.get();
    }

    public SimpleObjectProperty<Annotation> annotationProperty() {
        return annotation;
    }


    private void getSequence() {
        if (getSequenceThread != null && getSequenceThread.isAlive()) {
            getSequenceThread.interrupt();
        }

        leftSeqTf.setText(null);
        alt1SeqTf.setText(annotation.get().getVariant().getRef());
        alt2SeqTf.setText(annotation.get().getVariant().getAlt());
        rightSeqTf.setText(null);

        int margin = 8;
        int start = annotation.get().getVariant().getStart() - margin;
        int end = annotation.get().getVariant().getStart() + annotation.get().getVariant().getRef().length() + margin - 1;

        Runnable task = () -> {
            String seq;
            try {
                seq = fastaSequenceGetter.getSequence(annotation.get().getVariant().getContig(), start, end);
                if (!Thread.currentThread().isInterrupted() && seq != null) {
                    Platform.runLater(() -> {
                        int lenVariant = annotation.get().getVariant().getRef().length() + margin;
                        String t_left = seq.substring(0, margin);
                        String t_right = seq.substring(lenVariant);
                        leftSeqTf.setText(t_left);
                        rightSeqTf.setText(t_right);
                    });
                }
            } catch (IOException e) {
                logger.warn(e);
            }
        };

        getSequenceThread = new Thread(task);
        getSequenceThread.start();
    }

    public void getSangerState() {
        if (getSangerStateThread != null && getSangerStateThread.isAlive()) {
            getSangerStateThread.interrupt();
        }

        if (annotation.get().getSangerState() != null) {
            if (annotation.get().getSangerState().getLastState() != null) {
                sangerStateTf.setText(annotation.get().getSangerState().getLastState().getState().getName());
            } else {
                sangerStateTf.setText(SangerState.NONE.getName());
            }
        } else {
            Runnable task = () -> {
                try {
                    annotation.get().setSangerState(DAOController.get().getSangerStateDAO().getSangerChecks(
                            annotation.get(),
                            ModuleManager.getAnalysisViewController().getAnalysis().getId()));
                    //                        if (annotation.get().getSangerState().getLastState() != null) {
                    //                            sangerStateTf.setText(annotation.get().getSangerState().getLastState().getState().getName());
                    //                        } else {
                    //                            sangerStateTf.setText(SangerState.NONE.getName());
                    //                        }
                    Platform.runLater(this::getSangerState);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            };
            getSangerStateThread = new Thread(task);
            getSangerStateThread.start();
        }
    }


    public void loadVariantCommentaries() {
        try {
            variantCommentaryLv.setItems(DAOController.get().getVariantCommentaryDAO().getVariantCommentaries(annotation.get().getVariant().getId()));
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    public void loadAnnotationCommentaries() {
        try {
            annotationCommentaryLv.setItems(DAOController.get().getAnnotationCommentaryDAO().getAnnotationCommentaries(
                    annotation.get().getVariant().getId(),
                    ModuleManager.getAnalysisViewController().getAnalysis().getId()));
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    private void setFalsePositiveStyle() {
        if (annotation.get() != null && annotation.get().getVariant().isFalsePositive()) {
            falsePositiveTf.getStyleClass().add(false_positive_textfield);
            falsePositiveTf.getStyleClass().remove(true_positive_textfield);
            falsePositiveTf.setDisable(false);
        } else {
            falsePositiveTf.getStyleClass().remove(false_positive_textfield);
            falsePositiveTf.getStyleClass().add(true_positive_textfield);
            falsePositiveTf.setDisable(true);
        }
    }


    private void setFalsePathogenicityStyle() {
        pathogenicityTf.getStyleClass().removeAll(pathogenic_textfield_class, likely_pathogenic_textfield_class, uncertain_significance_textfield_class, likely_benin_textfield_class, benin_textfield_class);
        if (annotation.get() != null) {
            switch (annotation.get().getVariant().getAcmg()) {
                case PATHOGENIC:
                    pathogenicityTf.getStyleClass().add(pathogenic_textfield_class);
                    break;
                case LIKELY_PATHOGENIC:
                    pathogenicityTf.getStyleClass().add(likely_pathogenic_textfield_class);
                    break;
                case LIKELY_BENIN:
                    pathogenicityTf.getStyleClass().add(likely_benin_textfield_class);
                    break;
                case BENIN:
                    pathogenicityTf.getStyleClass().add(benin_textfield_class);
                    break;
                default:
                    pathogenicityTf.getStyleClass().add(uncertain_significance_textfield_class);
            }
        }
    }


    private ChangeListener<String> initHotspotStyleListener() {
        return (obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                hotspotTf.getStyleClass().remove("pathogenic-textfield");
            } else {
                hotspotTf.getStyleClass().add("pathogenic-textfield");
            }
        };
    }


    @FXML
    private void addVariantCommentary() {
        AddVariantCommentaryDialog addVariantCommentaryDialog = new AddVariantCommentaryDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(addVariantCommentaryDialog);
        Button b = addVariantCommentaryDialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (addVariantCommentaryDialog.isValid() && addVariantCommentaryDialog.getValue() != null) {
                String comment = addVariantCommentaryDialog.getValue().getCommentary();
                try {
                    DAOController.get().getVariantCommentaryDAO().addVariantCommentary(annotation.get().getVariant().getId(), comment);
                    loadVariantCommentaries();
                    Message.hideDialog(addVariantCommentaryDialog);
                } catch (SQLException ex) {
                    logger.error(ex);
                    Message.error(ex.getMessage(), ex);
                }

            }
        });
    }

    @FXML
    private void addAnnotationCommentary() {
        AddVariantCommentaryDialog addVariantCommentaryDialog = new AddVariantCommentaryDialog(App.get().getAppController().getDialogPane());
        Message.showDialog(addVariantCommentaryDialog);
        Button b = addVariantCommentaryDialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            if (addVariantCommentaryDialog.isValid() && addVariantCommentaryDialog.getValue() != null) {
                String comment = addVariantCommentaryDialog.getValue().getCommentary();
                try {
                    DAOController.get().getAnnotationCommentaryDAO().addAnnotationCommentary(
                            annotation.get().getVariant().getId(),
                            ModuleManager.getAnalysisViewController().getAnalysis().getId(),
                            comment);
                    loadAnnotationCommentaries();
                    Message.hideDialog(addVariantCommentaryDialog);
                } catch (SQLException ex) {
                    logger.error(ex);
                    Message.error(ex.getMessage(), ex);
                }

            }
        });
    }


    private void fillExternalVariations() {
        externalVariationsContainer.getChildren().clear();
        if (annotation.get().getTranscriptConsequence() != null && annotation.get().getTranscriptConsequence().getExternalVariations() != null) {
            for (ExternalVariation externalVariation : annotation.get().getTranscriptConsequence().getExternalVariations()) {
                Label l = new Label(externalVariation.getId());
                l.getStyleClass().add("hyperlink-label");
                l.setOnMouseClicked(e -> {
                    String url = externalVariation.getURL();
                    if (url != null) {
                        App.get().getHostServices().showDocument(url);
                    }
                });
                externalVariationsContainer.getChildren().add(l);
            }
        }
    }


    private void fillPubmedIds() {
        System.out.println(annotation.get().getTranscriptConsequence().getPubmedIds());
        pubmedIdsContainer.getChildren().clear();
        if (annotation.get().getTranscriptConsequence() != null && annotation.get().getTranscriptConsequence().getPubmedIds() != null) {
            for (String pubmedId : annotation.get().getTranscriptConsequence().getPubmedIds()) {
                Label l = new Label(pubmedId);
                l.getStyleClass().add("hyperlink-label");
                l.setOnMouseClicked(e -> {
                    String url = ExternalDatabasesUtils.getPubmedLink(pubmedId);
                    if (url != null) {
                        App.get().getHostServices().showDocument(url);
                    }
                });
                pubmedIdsContainer.getChildren().add(l);
            }
        }
    }


    private void setExternalLinksButtons() {

        igvLinkBtn.setOnAction(e -> {
            try {
                App.get().getIgvHandler().goTo(ModuleManager.getAnalysisViewController().getAnalysis(), annotation.get().getVariant().getContig(), annotation.get().getVariant().getStart());
            } catch (IOException ex) {
                Message.error(ex.getMessage());
            }
        });

        String gnomadLink = ExternalDatabasesUtils.getGnomADLink(annotation.get());
        gnomadLinkBtn.setDisable(false);
        gnomadLinkBtn.setOnAction(e -> BrowserUtils.openURL(gnomadLink));

        String clinVarLink = ExternalDatabasesUtils.getClinVarLink(annotation.get());
        clinvarLinkBtn.setDisable(clinVarLink == null);
        clinvarLinkBtn.setOnAction(e -> BrowserUtils.openURL(clinVarLink));

        String dbsnpLink = ExternalDatabasesUtils.getdbSnpLink(annotation.get());
        dbsnpLinkBtn.setDisable(dbsnpLink == null);
        dbsnpLinkBtn.setOnAction(e -> BrowserUtils.openURL(dbsnpLink));

        String ensemblLink = ExternalDatabasesUtils.getdbSnpLink(annotation.get());
        ensemblLinkBtn.setDisable(ensemblLink == null);
        ensemblLinkBtn.setOnAction(e -> BrowserUtils.openURL(ensemblLink));

        String cosmicLink = ExternalDatabasesUtils.getCosmicLink(annotation.get());
        cosmicLinkBtn.setDisable(cosmicLink == null);
        cosmicLinkBtn.setOnAction(e -> BrowserUtils.openURL(cosmicLink));

        String cosmicGeneViewerLink = ExternalDatabasesUtils.getCosmicGeneViewLink(annotation.get());
        cosmicGeneViewerLinkBtn.setDisable(cosmicGeneViewerLink == null);
        cosmicGeneViewerLinkBtn.setOnAction(e -> BrowserUtils.openURL(cosmicGeneViewerLink));

        String varsomeLink = ExternalDatabasesUtils.getVarsomeLink(annotation.get());
        varsomeLinkBtn.setDisable(varsomeLink == null);
        varsomeLinkBtn.setOnAction(e -> BrowserUtils.openURL(varsomeLink));

        String lovdLink = ExternalDatabasesUtils.getLovdLink(annotation.get());
        lovdLinkBtn.setDisable(lovdLink == null);
        lovdLinkBtn.setOnAction(e -> BrowserUtils.openURL(lovdLink));

        String oncoKbLink = ExternalDatabasesUtils.getOncoKBLink(annotation.get());
        oncoKbLinkBtn.setDisable(oncoKbLink == null);
        oncoKbLinkBtn.setOnAction(e -> BrowserUtils.openURL(oncoKbLink));

        String intogenLink = ExternalDatabasesUtils.getIntogenLink(annotation.get());
        intogenLinkBtn.setDisable(intogenLink == null);
        intogenLinkBtn.setOnAction(e -> BrowserUtils.openURL(intogenLink));

        String alamutLink = ExternalDatabasesUtils.getAlamutQuery(annotation.get());
        alamutLinkBtn.setDisable(alamutLink == null);
        alamutLinkBtn.setOnAction(e -> {
            if (alamutLink != null) {
                try {
                    URL url = new URL(alamutLink);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.getInputStream();
                } catch (IOException ex) {
                    logger.error(ex);
                    Message.error(ex.getMessage());
                }
            }
        });
    }

    @FXML
    private void showEnsemblConsequencesDialog() {
        Image img = new Image(String.valueOf(getClass().getResource("/images/ensembl_consequences.jpg")));
        ImageView imageView = new ImageView(img);
        DialogPane.Dialog<Object> dialog = App.get().getAppController().getDialogPane()
                .showNode(DialogPane.Type.INFORMATION, "Ensembl Consequences", imageView);
        dialog.setShowCloseButton(false);
    }

    @FXML
    private void showSharingVariantDialog() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("sharingvariantdialog.msg.loadingAnalyses"));
        wid.addTaskEndNotification(r -> {
            if (r == 0) {
                Analysis analysis = ModuleManager.getAnalysisViewController().getAnalysis();
                AnalysesSharingVariantDialog analysesSharingVariantDialog = new AnalysesSharingVariantDialog(analysis, annotation.get(), searchVariantResults);
                Message.showDialog(analysesSharingVariantDialog);
            }
        });
        wid.exec("loadingAnalyses", inputParams -> {
            try {
                if (searchVariantResults == null) {
                    searchVariantResults = DAOController.get().getVariantAnalysisDAO().getVariants(annotation.get().getVariant());
                }
            } catch (Exception e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
                return 1;
            }
            return 0;
        });
    }


    @FXML
    private void showVafScatterPlot() {
        WorkIndicatorDialog<String> wid = new WorkIndicatorDialog<>(App.getPrimaryStage(), App.getBundle().getString("sharingvariantdialog.msg.loadingAnalyses"));
        wid.addTaskEndNotification(r -> {
            if (r == 0) {
                vafScatterChart.setVariant(annotation.get(), searchVariantResults);
                List<ButtonType> buttonTypes = new ArrayList<>();
                buttonTypes.add(ButtonType.OK);
                DialogPane.Dialog<Object> dialog = App.get().getAppController().getDialogPane().showNode(
                        DialogPane.Type.INFORMATION, App.getBundle().getString("vafscatterplot.title"), vafScatterChart, buttonTypes);
                dialog.setShowCloseButton(false);
            }
        });
        wid.exec("loadingAnalyses", inputParams -> {
            try {
                if (searchVariantResults == null) {
                    searchVariantResults = DAOController.get().getVariantAnalysisDAO().getVariants(annotation.get().getVariant());
                }
            } catch (Exception e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
                return 1;
            }
            return 0;
        });
    }

    @FXML
    private void showPredictionsDetalDialog() {
        predictionsDetailDialog.setValue(annotation.get());
        Message.showDialog(predictionsDetailDialog);
    }

    public HBox getTest() {return test;}
}
