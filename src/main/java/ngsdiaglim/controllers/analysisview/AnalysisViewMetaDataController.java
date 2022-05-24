package ngsdiaglim.controllers.analysisview;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFFileReader;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.exceptions.MalformedSearchQuery;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.utils.DateFormatterUtils;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;

public class AnalysisViewMetaDataController  extends VBox {

    private static final Logger logger = LogManager.getLogger(AnalysisViewMetaDataController.class);
    @FXML private TextField analysisNameTf;
    @FXML private TextField analysisSampleNameTf;
    @FXML private TextField analysisDateTf;
    @FXML private TextField analysisUserTf;
    @FXML private TextField analysisRunTf;
    @FXML private TextField analysisPathTf;
    @FXML private TextField analysisVCFTf;
    @FXML private TextField analysisBAMTf;
    @FXML private TextField analysisDepthTf;
    @FXML private TextField paramsNameTf;
    @FXML private TextField paramsGenomeTf;
    @FXML private TextField paramsPanelTf;
    @FXML private TextField paramsHotspotsTf;
    @FXML private TextField paramsGenesTf;
    @FXML private TextField paramsMinDepthTf;
    @FXML private TextField paramsWarningDepthTf;
    @FXML private TextField paramsSpecificDepthTf;
    @FXML private TextField paramsMinVafTf;
    @FXML private TextField paramsLibraryTf;
    @FXML private CustomTextField searchInVCFTf;
    @FXML private TextArea vcfHeaderTa;
    @FXML private GridPane parametersGrid;
    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();

    private VCFFileReader reader;
    private String vcfHeader;
    private VCFEncoder encoder;

    public AnalysisViewMetaDataController() {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewMetaData.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
//        initView();

        analysis.addListener((obs, oldV, newV) -> {
            if (newV != null) {
                updateView();
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

    //    private void initView() {
//        analysisNameTf.setText(analysis.getName());
//        analysisSampleNameTf.setText(analysis.getSampleName());
//        analysisDateTf.setText(DateFormatterUtils.formatLocalDateTime(analysis.getCreationDate(), "dd/MM/yyyy à HH:ss"));
//        analysisUserTf.setText(analysis.getCreationUser());
//        analysisRunTf.setText(analysis.getRun().getName());
//        analysisPathTf.setText(analysis.getDirectoryPath());
//        analysisVCFTf.setText(analysis.getVcfFile().getAbsolutePath());
//        analysisBAMTf.setText(analysis.getBamFile() == null ? "na" : analysis.getBamFile().getAbsolutePath());
//        analysisDepthTf.setText(analysis.getDepthFile() == null ? "na" : analysis.getDepthFile().getAbsolutePath());
//        paramsNameTf.setText(analysis.getAnalysisParameters().getAnalysisName());
//        paramsGenomeTf.setText(analysis.getAnalysisParameters().getGenome().getName());
//        paramsPanelTf.setText(analysis.getAnalysisParameters().getPanel().getName());
//        paramsGenesTf.setText(analysis.getAnalysisParameters().getGeneSet().getName());
//        paramsHotspotsTf.setText(analysis.getAnalysisParameters().getHotspotsSet() == null ? "" : analysis.getAnalysisParameters().getHotspotsSet().getName());
//        paramsMinDepthTf.setText(String.valueOf(analysis.getAnalysisParameters().getMinDepth()));
//        paramsWarningDepthTf.setText(String.valueOf(analysis.getAnalysisParameters().getWarningDepth()));
//        paramsMinVafTf.setText(String.valueOf(analysis.getAnalysisParameters().getMinVAF()));
//        paramsLibraryTf.setText(analysis.getAnalysisParameters().getTargetEnrichment().name());
//
//        if (analysis.getVcfFile().exists()) {
//            try {
//                reader = VCFUtils.getVCFReader(analysis.getVcfFile());
//                vcfHeader = VCFUtils.getVcfHeader(reader);
//                encoder = new VCFEncoder(reader.getHeader(), true, true);
//                vcfHeaderTa.setText(vcfHeader);
//            } catch (IOException e) {
//                logger.error(e);
//                vcfHeaderTa.setText(null);
//                Message.error(e.getMessage(), e);
//            }
//        }
//
//        Map<String, String> metadata = analysis.getmMetadataAsMap();
//        if (!metadata.isEmpty()) {
//            int rowIdx = 0;
//            for (Map.Entry<String, String> e : metadata.entrySet()) {
//                Label l = new Label(e.getKey() + " :");
//                l.getStyleClass().add("font-medium");
//                TextField tf = new TextField(e.getValue());
//                tf.getStyleClass().add("textfield-label");
//                tf.setEditable(false);
//                parametersGrid.add(l, 2, rowIdx);
//                parametersGrid.add(tf, 3, rowIdx++);
//            }
//        }
//    }


    private void updateView() {
        analysisNameTf.setText(analysis.get().getName());
        analysisSampleNameTf.setText(analysis.get().getSampleName());
        analysisDateTf.setText(DateFormatterUtils.formatLocalDateTime(analysis.get().getCreationDate(), "dd/MM/yyyy à HH:ss"));
        analysisUserTf.setText(analysis.get().getCreationUser());
        analysisRunTf.setText(analysis.get().getRun().getName());
        analysisPathTf.setText(analysis.get().getDirectoryPath());
        analysisVCFTf.setText(analysis.get().getVcfFile().getAbsolutePath());
        analysisBAMTf.setText(analysis.get().getBamFile() == null ? "na" : analysis.get().getBamFile().getAbsolutePath());
        analysisDepthTf.setText(analysis.get().getDepthFile() == null ? "na" : analysis.get().getDepthFile().getAbsolutePath());
        paramsNameTf.setText(analysis.get().getAnalysisParameters().getAnalysisName());
        paramsGenomeTf.setText(analysis.get().getAnalysisParameters().getGenome().getName());
        paramsPanelTf.setText(analysis.get().getAnalysisParameters().getPanel().getName());
        paramsGenesTf.setText(analysis.get().getAnalysisParameters().getGeneSet().getName());
        paramsHotspotsTf.setText(analysis.get().getAnalysisParameters().getHotspotsSet() == null ? "" : analysis.get().getAnalysisParameters().getHotspotsSet().getName());
        paramsMinDepthTf.setText(String.valueOf(analysis.get().getAnalysisParameters().getMinDepth()));
        paramsWarningDepthTf.setText(String.valueOf(analysis.get().getAnalysisParameters().getWarningDepth()));
        paramsSpecificDepthTf.setText(analysis.get().getAnalysisParameters().getSpecificCoverageSet() == null ? "" : analysis.get().getAnalysisParameters().getSpecificCoverageSet().getName());
        paramsMinVafTf.setText(String.valueOf(analysis.get().getAnalysisParameters().getMinVAF()));
        paramsLibraryTf.setText(analysis.get().getAnalysisParameters().getTargetEnrichment().name());

        if (analysis.get().getVcfFile().exists()) {
            try {
                reader = VCFUtils.getVCFReader(analysis.get().getVcfFile());
                vcfHeader = VCFUtils.getVcfHeader(reader);
                encoder = new VCFEncoder(reader.getHeader(), true, true);
                vcfHeaderTa.setText(vcfHeader);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                vcfHeaderTa.setText(null);
                Message.error(e.getMessage(), e);
            }
        }

        parametersGrid.getChildren().removeIf(n -> {
            Integer idx = GridPane.getColumnIndex(n);
            return idx != null && idx >= 2;
        });
        Map<String, String> metadata = analysis.get().getmMetadataAsMap();
        if (!metadata.isEmpty()) {
            int rowIdx = 0;
            for (Map.Entry<String, String> e : metadata.entrySet()) {
                Label l = new Label(e.getKey() + " :");
                l.getStyleClass().add("font-medium");
                TextField tf = new TextField(e.getValue());
                tf.getStyleClass().add("textfield-label");
                tf.setEditable(false);
                parametersGrid.add(l, 2, rowIdx);
                parametersGrid.add(tf, 3, rowIdx++);
            }
        }
    }


    @FXML
    private void searchInVCF() {
        if (StringUtils.isBlank(searchInVCFTf.getText())) {
            vcfHeaderTa.setText(vcfHeader);
        } else {
            try {
                StringJoiner searchRslt = new StringJoiner("\n");
                SearchQuery searchQuery = parseQuery(searchInVCFTf.getText());
                CloseableIterator<VariantContext> it = reader.query(searchQuery.getContig(), searchQuery.getStart(), searchQuery.getEnd());
                if (it.hasNext()) {
                    while (it.hasNext()) {
                        VariantContext ctx = it.next();
                        if (searchQuery.getRef() != null) {
                            if (ctx.getReference().basesMatch(searchQuery.getRef())) {
                                if (searchQuery.getRef() != null) {
                                    if (searchQuery.getAlt() != null) {
                                        if (ctx.getAlternateAllele(0).basesMatch(searchQuery.getAlt())) {
                                            searchRslt.add(encoder.encode(ctx));
                                        }
                                    } else {
                                        searchRslt.add(encoder.encode(ctx));
                                    }
                                } else {
                                    searchRslt.add(encoder.encode(ctx));
                                }
                            }
                        } else {
                            searchRslt.add(encoder.encode(ctx));
                        }
                    }
                } else {
                    searchRslt.add("No variant found.");
                }
                vcfHeaderTa.setText(searchRslt.toString());

            } catch (MalformedSearchQuery e) {
                logger.error(e.getMessage(), e);
                Message.error(e.getMessage(), e);
            }
        }
    }


    private SearchQuery parseQuery(String query) throws MalformedSearchQuery {
        if (StringUtils.isBlank(query)) {
            throw new MalformedSearchQuery("Query is blank");
        } else {
            String[] searchTks = query.split("-");
            if (searchTks.length < 2 || searchTks.length > 4) {
                throw new MalformedSearchQuery("Malformed query : " + query);
            } else {
                if (!NumberUtils.isInt(searchTks[1])) {
                    throw new MalformedSearchQuery("Invalid Position in query : " + query);
                } else {
                    String contig = searchTks[0];
                    int start = Integer.parseInt(searchTks[1]);
                    SearchQuery searchQuery = new SearchQuery(contig, start);
                    if (searchTks.length > 2) {
                        if (NumberUtils.isInt(searchTks[2])) {
                            searchQuery.setEnd(Integer.parseInt(searchTks[2]));
                        } else {
                            searchQuery.setEnd(start);
                            searchQuery.setRef(searchTks[2]);
                        }
                    } else {
                        searchQuery.setEnd(start);
                    }
                    if (searchTks.length > 3) {
                        searchQuery.setAlt(searchTks[3]);
                    }
                    return searchQuery;
                }
            }
        }
    }

    private static class SearchQuery {
        private String contig;
        private int start;
        private int end;
        private String ref;
        private String alt;

        public SearchQuery(String contig, int start) {
            this.contig = contig;
            this.start = start;
        }

        public String getContig() {return contig;}

        public void setContig(String contig) {
            this.contig = contig;
        }

        public int getStart() {return start;}

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {return end;}

        public void setEnd(int end) {
            this.end = end;
        }

        public String getRef() {return ref;}

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getAlt() {return alt;}

        public void setAlt(String alt) {
            this.alt = alt;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("contig", contig)
                    .append("start", start)
                    .append("end", end)
                    .append("ref", ref)
                    .append("alt", alt)
                    .toString();
        }
    }
}
