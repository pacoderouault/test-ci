package ngsdiaglim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.controllers.dialogs.AddRunDialog;
import ngsdiaglim.database.dao.*;
import ngsdiaglim.enumerations.AnalysisStatus;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.exceptions.MalformedGeneTranscriptFile;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.exceptions.NotBiallelicVariant;
import ngsdiaglim.modeles.analyse.*;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.parsers.GeneSetParser;
import ngsdiaglim.modeles.parsers.PanelParser;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.VariantUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalAnalysisTest extends BaseSetup {

    private static final UsersDAO userDAO = new UsersDAO();
    private static final PanelDAO panelDAO = new PanelDAO();
    private static final PanelRegionDAO panelRegionDAO = new PanelRegionDAO();
    private static final GeneSetDAO geneSetDAO = new GeneSetDAO();
    private static final GeneDAO geneDAO = new GeneDAO();
    private static final TranscriptsDAO transcriptsDAO = new TranscriptsDAO();
    private static final AnalysisParametersDAO analysisParametersDAO = new AnalysisParametersDAO();
    private static final RunsDAO runsDAO = new RunsDAO();
    private static final AnalysisDAO analysisDAO = new AnalysisDAO();

    @TempDir
    File tempDir;
    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void addAnalysisTest() {

        // start App
        new App();
        try {
            User user = userDAO.checkUserConnection("admin", "admin");
            App.get().setLoggedUser(user);
        } catch (SQLException e) {
            fail(e);
        }

        // add panel
        File panelFile = Paths.get(resourcesDirectory.getPath(), "data", "run", "params", "CMT_panel.bed").toFile();
        long panelId = -1;
        try {
            List<PanelRegion> regions = PanelParser.parsePanel(panelFile);
            panelId = panelDAO.addPanel("GlobalPanel", panelFile.getPath());
            for (PanelRegion region : regions) {
                panelRegionDAO.addRegion(region, panelId);
            }
        } catch (IOException | MalformedPanelFile | SQLException e) {
            fail(e);
        }
        assertNotEquals(-1, panelId);

        // add transcripts
        File transcriptsFile = Paths.get(resourcesDirectory.getPath(), "data", "run", "params", "liste_transcripts_avec_alternatifs.tsv").toFile();
        long geneSetId = -1;
        try {
            HashSet<Gene> genes = GeneSetParser.parseGeneSet(transcriptsFile);
            geneSetId = geneSetDAO.addGeneSet("globalTranscripts");
            for (Gene gene : genes) {
                long geneId = geneDAO.addGene(gene, geneSetId);
                gene.setId(geneId);
                for (Transcript transcript : gene.getTranscripts().values()) {
                    long transcriptId = transcriptsDAO.addTranscript(transcript.getName(), gene.getId());
                    transcript.setId(transcriptId);
                }
                // if only one transcript for the gene, set it as "preferred transcript"
                if (gene.getTranscripts().size() == 1) {
                    Optional<Transcript> opt = gene.getTranscripts().values().stream().findAny();
                    if(opt.isPresent()) {
                        geneDAO.setPreferredTranscript(gene.getId(), opt.get().getId());
                    }
                }
            }
        } catch (IOException | SQLException | MalformedGeneTranscriptFile e) {
            fail(e);
        }
        assertNotEquals(-1, geneSetId);

        // create Analysis Parameters
        long paramsId = - 1;
        try {
            paramsId = analysisParametersDAO.addAnalysisParameters(
                    "ParamsTest",
                    Genome.GRCh37,
                    30,
                    50,
                    0.10,
                    panelId,
                    geneSetId,
                    null,
                    TargetEnrichment.CAPTURE
            );
        } catch (SQLException e) {
            fail(e);
        }
        assertNotEquals(-1, paramsId);
        AnalysisParameters analysisParameters = null;
        try {
            analysisParameters = analysisParametersDAO.getAnalysisParameters(paramsId);
        } catch (SQLException e) {
            fail(e);
        }
        assertNotNull(analysisParameters);

        // create run
        Path runsDataPath = Paths.get(tempDir.getPath(), "data", "run_data");
        AddRunDialog.RunCreattionData runData = new AddRunDialog.RunCreattionData();
        runData.setRunDate(LocalDate.now());
        runData.setRunName("GlobalRun");

        RunCreator runCreator = new RunCreator(runData, runsDataPath);
        long runId = -1;
        try {
            runId = runCreator.createRun();
        } catch (IOException | SQLException e) {
            fail(e);
        }
        assertNotEquals(-1, runId);
        Run run = null;
        try {
            run = runsDAO.getRun(runId);
        } catch (SQLException e) {
            fail(e);
        }
        assertNotNull(run);

        // add analyse
        List<RunFile> runFileList = new ArrayList<>();
        File reportFile = Paths.get(resourcesDirectory.getPath(), "data", "run", "runtest", "M21.05_15B68a_report.html").toFile();
        runFileList.add(new RunFile(reportFile, run));

        ObservableList<AnalysisInputData> analysisInputDataList = FXCollections.observableArrayList();
        AnalysisInputData analysisInputData = new AnalysisInputData(run, "analysetest", "M21.05_15B68a");
        File vcfFile = Paths.get(resourcesDirectory.getPath(), "data", "run", "runtest", "M21.05_15B68a.vcf").toFile();
        File depthFile = Paths.get(resourcesDirectory.getPath(), "data", "run", "runtest", "M21.05_15B68a_depth.tsv").toFile();
        File bamFile = Paths.get(resourcesDirectory.getPath(), "data", "run", "runtest", "15B68a_subsamplePMP22.bam").toFile();
        analysisInputData.setVcfFile(vcfFile);
        analysisInputData.setDepthFile(depthFile);
        analysisInputData.setBamFile(bamFile);
        analysisInputData.setAnalysisParameters(analysisParameters);
        assertEquals(AnalysisInputData.AnalysisInputState.VALID, analysisInputData.getState());

        analysisInputDataList.add(analysisInputData);

        RunImporter runImporter = new RunImporter(run, runFileList, analysisInputDataList, null);
        try {
            runImporter.importRun();
        } catch (Exception e) {
            fail(e);
        }


        try {
            ObservableList<Analysis> analyses = analysisDAO.getAnalysis(run);
            assertEquals(1, analyses.size());

            Analysis analysis = analyses.get(0);
            assertEquals(analysis.getName(), "analysetest");
            assertEquals(analysis.getSampleName(), "M21.05_15B68a");
            assertNotNull(analysis.getVcfFile());
            assertNotNull(analysis.getBamFile());
            assertNotNull(analysis.getDepthFile());
            assertEquals(AnalysisStatus.INPROGRESS, analysis.getStatus());
            assertEquals(paramsId, analysis.getAnalysisParameters().getId());

            // load info
            VCFParser vcfParser = new VCFParser(analysis.getVcfFile(), analysis.getAnalysisParameters(), analysis.getRun());
            vcfParser.parseVCF(true);
            analysis.setAnnotations(vcfParser.getAnnotations());
            analysis.loadCoverage();

            // check annotations
            assertEquals(979, vcfParser.getVariantParserReportData().getVariantsCount());
            assertEquals(2, vcfParser.getVariantParserReportData().getFilteredVariantsCount());
            assertEquals(2, vcfParser.getVariantParserReportData().getVafFilteredCount());
            assertEquals(977, analysis.getAnnotations().size());

            Optional<Annotation> filteredVariant1 = analysis.getAnnotations().stream().filter(a -> VariantUtils.getHashVariant(a.getVariant()).equals("chr2:241696840ATCC>A")).findAny();
            assertFalse(filteredVariant1.isPresent());

            Optional<Annotation> filteredVariant2 = analysis.getAnnotations().stream().filter(a -> VariantUtils.getHashVariant(a.getVariant()).equals("chr11:68080214CGCT>C")).findAny();
            assertFalse(filteredVariant2.isPresent());

            // check coverage
            assertEquals(9, analysis.getCoverageRegions().size());

            CoverageRegion regionTest1 = new CoverageRegion("chr8", 24811064, 24811065, null, CoverageQuality.LOW_COVERAGE);
            assertEquals(regionTest1, analysis.getCoverageRegions().get(0));

            CoverageRegion regionTest2 = new CoverageRegion("chr14", 105173930, 105174006, null, CoverageQuality.NO_COVERED);
            assertEquals(regionTest2, analysis.getCoverageRegions().get(3));

        } catch (SQLException | IOException | NotBiallelicVariant | MalformedCoverageFile e) {
            fail(e);
        }

    }

}
