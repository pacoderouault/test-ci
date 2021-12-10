package ngsdiaglim.modeles.parsers;

import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.Region;
import ngsdiaglim.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SamtoolsDepthParserTest {
    @TempDir
    File tempDir;

    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void parseFile() {
        File depthFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a_depth.tsv").toFile();

        AnalysisParameters params = new AnalysisParameters(
                1, Genome.GRCh37, "paramsTest", 30, 50, 0.1f,
                true, null, null, null, TargetEnrichment.CAPTURE
        );
        SamtoolsDepthParser samtoolsDepthParser = new SamtoolsDepthParser(params, depthFile);
        try {
            File bedOutput = new File(tempDir, "coverage.bed.gz");
            samtoolsDepthParser.parseFile(bedOutput);

            assertTrue(bedOutput.exists());
            List<CoverageRegion> regions = CoverageFileParser.parseCoverageFile(bedOutput, params);
            assertEquals(9, regions.size());
            Region r = new Region("chr14", 105173930, 105174006, "");
            Optional<CoverageRegion> opt = regions.stream().filter(r::equals).findAny();
            assertTrue(opt.isPresent());
            assertEquals(CoverageQuality.NO_COVERED, opt.get().getCoverageQuality());
        } catch (IOException | MalformedCoverageFile e) {
            e.printStackTrace();
        }
    }

    @Test
    void isDepthFile() {
        File badFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf").toFile();
        File depthFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a_depth.tsv").toFile();
        try {
            assertFalse(SamtoolsDepthParser.isDepthFile(badFile));
            assertTrue(SamtoolsDepthParser.isDepthFile(depthFile));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}