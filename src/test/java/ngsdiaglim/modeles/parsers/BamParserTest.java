package ngsdiaglim.modeles.parsers;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.PanelDAO;
import ngsdiaglim.database.dao.PanelRegionDAO;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.Region;
import ngsdiaglim.utils.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BamParserTest extends BaseSetup {

    private static final File resourcesDirectory = new File("src/test/resources");
    private static final PanelDAO panelDAO = new PanelDAO();
    private static final PanelRegionDAO panelRegionDAO = new PanelRegionDAO();
    private static AnalysisParameters params;

    @TempDir
    File tempDir;

    @BeforeAll
    static void setupParameters()  {
        File panelFile = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel_PMP22.bed").toFile();
        try {
            List<PanelRegion> regions = PanelParser.parsePanel(panelFile);
            long panelId = panelDAO.addPanel("panelTest", panelFile.getPath());
            for (PanelRegion region : regions) {
                panelRegionDAO.addRegion(region, panelId);
            }
            Panel panel = panelDAO.getPanel(panelId);
//            long id, Genome genome, String analysisName, int minDepth, int warningDepth,
//            float minVAF, boolean isActive, Panel panel, GeneSet geneSet,
//                    HotspotsSet hotspotsSet, TargetEnrichment targetEnrichment
            params = new AnalysisParameters(
                    1, Genome.GRCh37, "test", 30, 50, 0.1f, true, panel, null, null, null, TargetEnrichment.CAPTURE
            );
        } catch (IOException | MalformedPanelFile | SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void parseFile() {
        File bamFile = Paths.get(resourcesDirectory.getPath(), "data/15B68a_subsamplePMP22.bam").toFile();
        BamParser bamParser = new BamParser(params, bamFile);

        File outFile = new File(tempDir, "coverage.bed.gz");
        try {
            bamParser.parseFile(outFile, null);
            assertTrue(outFile.exists());
            List<CoverageRegion> regions = CoverageFileParser.parseCoverageFile(outFile, params);
            assertEquals(1, regions.size());
            CoverageRegion r = new CoverageRegion("chr17", 40688285, 40688678, null, CoverageQuality.NO_COVERED);
            assertEquals(r, regions.get(0));
            assertEquals(CoverageQuality.NO_COVERED, regions.get(0).getCoverageQuality());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}