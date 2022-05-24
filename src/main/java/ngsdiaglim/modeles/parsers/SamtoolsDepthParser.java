package ngsdiaglim.modeles.parsers;

import ngsdiaglim.comparators.RegionComparator;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.RunConstants;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageSet;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class SamtoolsDepthParser {

    private final static Logger logger = LogManager.getLogger(SamtoolsDepthParser.class);
    private final AnalysisParameters analysisParameters;
    private final File file;

    public SamtoolsDepthParser(AnalysisParameters analysisParameters, File file) {
        this.analysisParameters = analysisParameters;
        this.file = file;
    }

    public File getFile() {return file;}

    public File parseFile(File outFile, File outSpecFile) throws IOException {
        int minDepth = analysisParameters.getMinDepth();
        int warningDepth = analysisParameters.getWarningDepth();

        SpecificCoverageSet specificCoverageSet = analysisParameters.getSpecificCoverageSet();

        // read samtools depth file
        List<CoverageRegion> noCoveredRegions = new ArrayList<>();
        List<CoverageRegion> badCoveredRegions = new ArrayList<>();
        List<CoverageRegion> specificCoveredRegions = new ArrayList<>();

        try (BufferedReader br = IOUtils.getFileReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] tokens = line.trim().split("\t");
                int depth = Integer.parseInt(tokens[2]);
                String contig = tokens[0];
                int pos = Integer.parseInt(tokens[1]);


                if (depth < minDepth) {
                    if (noCoveredRegions.isEmpty() || !noCoveredRegions.get(noCoveredRegions.size() - 1).isTouching(contig, pos)) {
                        CoverageRegion cr = new CoverageRegion(contig, pos-1, pos, null, CoverageQuality.NO_COVERED);
                        cr.addDepthValue(depth);
                        noCoveredRegions.add(cr);
                    } else {
                        noCoveredRegions.get(noCoveredRegions.size() - 1).extendsRegion(depth);
                    }
                } else if (depth < warningDepth) {
                    if (badCoveredRegions.isEmpty() || !badCoveredRegions.get(badCoveredRegions.size() - 1).isTouching(contig, pos)) {
                        CoverageRegion cr = new CoverageRegion(contig, pos-1, pos, null, CoverageQuality.LOW_COVERAGE);
                        cr.addDepthValue(depth);
                        badCoveredRegions.add(cr);
                    } else {
                        badCoveredRegions.get(badCoveredRegions.size() - 1).extendsRegion(depth);
                    }
                }

                if (specificCoverageSet != null) {
                    for (SpecificCoverage r : specificCoverageSet.getSpecificCoverageList()) {
                        if (depth < r.getMinCov() && r.overlaps(contig, pos, pos)) {
                            if (specificCoveredRegions.isEmpty() || !specificCoveredRegions.get(specificCoveredRegions.size() - 1).isTouching(contig, pos)) {
                                CoverageRegion cr = new CoverageRegion(contig, pos-1, pos, null, CoverageQuality.NO_COVERED);
                                cr.addDepthValue(depth);
                                specificCoveredRegions.add(cr);
                            } else {
                                specificCoveredRegions.get(specificCoveredRegions.size() - 1).extendsRegion(depth);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }


        // write regions
        try (FileOutputStream output = new FileOutputStream(outFile); GZIPOutputStream gos = new GZIPOutputStream(output)) {
            List<CoverageRegion> coverageRegions = new ArrayList<>(noCoveredRegions);
            coverageRegions.addAll(badCoveredRegions);
            coverageRegions.sort(new RegionComparator());
            for (CoverageRegion cr : coverageRegions) {
                String line = cr.toIgvBed() + "\n";
                gos.write(line.getBytes(StandardCharsets.UTF_8));
            }
            gos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Files.deleteIfExists(outFile.toPath());
            throw e;
        }

        // specific coverage regions
        if (specificCoverageSet != null) {
            try (FileOutputStream outputSpec = new FileOutputStream(outSpecFile); GZIPOutputStream gos = new GZIPOutputStream(outputSpec)) {
                specificCoveredRegions.sort(new RegionComparator());
                for (CoverageRegion cr : specificCoveredRegions) {
                    String line = cr.toIgvBed() + "\n";
                    gos.write(line.getBytes(StandardCharsets.UTF_8));
                }
                gos.flush();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                Files.deleteIfExists(outSpecFile.toPath());
                throw e;
            }
        }
        return outFile;
    }

    public File parseFile() throws IOException {
        String outFileName = RunConstants.ANALYSIS_COVERAGE_FILENAME;
        String outSpecFileName = RunConstants.ANALYSIS_SPECIFIC_COVERAGE_FILENAME;
        File outFile = Paths.get(file.getParent(), outFileName).toFile();
        File outSpecFile = Paths.get(file.getParent(), outSpecFileName).toFile();
        return parseFile(outFile, outSpecFile);
    }

    /**
     * Return true if the file corresponds to a standard samtools depth output (<String>contig</String>  <int>pos</int> <int>depth</int>)
     */
    public static boolean isDepthFile(File file) throws IOException {
        try (BufferedReader reader = IOUtils.getFileReader(file)) {
            String line = reader.readLine();
            String[] tks = line.trim().split("\t");
            return tks.length == 3 &&
                    NumberUtils.isInt(tks[1]) &&
                    NumberUtils.isInt(tks[2]);
        }
    }

}
