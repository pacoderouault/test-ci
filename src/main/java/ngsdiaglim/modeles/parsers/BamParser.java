package ngsdiaglim.modeles.parsers;

import htsjdk.samtools.*;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;
import htsjdk.samtools.util.SamLocusIterator;
import ngsdiaglim.comparators.RegionComparator;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageSet;
import ngsdiaglim.utils.BamUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class BamParser {

    private final Logger logger = LogManager.getLogger(BamParser.class);
    //    private final Analysis analysis;
    private final AnalysisParameters analysisParameters;
    private final File bamFile;

    public BamParser(AnalysisParameters analysisParameters, File file) {
//        this.analysis = analysis;
        this.analysisParameters = analysisParameters;
        this.bamFile = file;
    }


    public void parseFile(File outFile, File outSpecFile) throws Exception {
        // Open the bam file
        final SamReader samReader = SamReaderFactory.makeDefault()
                .enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS)
                .validationStringency(ValidationStringency.DEFAULT_STRINGENCY)
                .open(bamFile);

        // Index the bam file if no .bai file is found
        if (!samReader.hasIndex()) {
            BamUtils.buildBamIndex(bamFile, samReader);
        }
        samReader.close();

        // read the coverage
        depthBamReader(outFile, outSpecFile);
    }


    /**
     * Read and calculate the coverage of the bam file
     * @throws Exception
     */
    private void depthBamReader(File outFile, File outSpecFile) throws Exception {

        Panel panel = analysisParameters.getPanel();

        // Open the bam file and read the coverage for each region
        SamReader samReaderLocal = SamReaderFactory.makeDefault()
                .disable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS)
                .enable(SamReaderFactory.Option.EAGERLY_DECODE)
                .validationStringency(ValidationStringency.SILENT)
                .open(bamFile);
        readDepth(panel, samReaderLocal, outFile, outSpecFile);
        try {
            samReaderLocal.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the depth of coverage of the BedRegion
     * @param samReader
     * @throws Exception
     */
    private void readDepth(Panel panel, SamReader samReader, File outFile, File outSpecFile) throws Exception {

        int minDepth = analysisParameters.getMinDepth();
        int warningDepth = analysisParameters.getWarningDepth();
        SpecificCoverageSet specificCoverageSet = analysisParameters.getSpecificCoverageSet();

        // create IntervalList from panel:
        // Loci to sample (could be a single position):
        IntervalList il= new IntervalList(samReader.getFileHeader());
        for (PanelRegion region : panel.getRegions()) {
            il.add(new Interval(region.getContig(), region.getStart(), region.getEnd()));
        }
        // Start iterating through loci:
        SamLocusIterator samLocIter = new SamLocusIterator(samReader, il, true);

        List<CoverageRegion> noCoveredRegions = new ArrayList<>();
        List<CoverageRegion> badCoveredRegions = new ArrayList<>();
        List<CoverageRegion> specificCoveredRegions = new ArrayList<>();

        for (SamLocusIterator.LocusInfo sli : samLocIter) {

            String chrom = sli.getContig();
            int from = sli.getStart();
            int depth = sli.getRecordAndOffsets().size();
            if (depth < minDepth) {
                if (noCoveredRegions.isEmpty() || !noCoveredRegions.get(noCoveredRegions.size() - 1).isTouching(chrom, from)) {
                    CoverageRegion cr = new CoverageRegion(chrom, from, from, null, CoverageQuality.NO_COVERED);
                    cr.addDepthValue(depth);
                    noCoveredRegions.add(cr);
                } else {
                    noCoveredRegions.get(noCoveredRegions.size() - 1).extendsRegion(depth);
                }
            } else if (depth < warningDepth) {
                if (badCoveredRegions.isEmpty() || !badCoveredRegions.get(badCoveredRegions.size() - 1).isTouching(chrom, from)) {
                    CoverageRegion cr = new CoverageRegion(chrom, from, from, null, CoverageQuality.LOW_COVERAGE);
                    cr.addDepthValue(depth);
                    badCoveredRegions.add(cr);
                } else {
                    badCoveredRegions.get(badCoveredRegions.size() - 1).extendsRegion(depth);
                }
            }

            if (specificCoverageSet != null) {
                for (SpecificCoverage r : specificCoverageSet.getSpecificCoverageList()) {
                    if (depth < r.getMinCov() && r.overlaps(chrom, from, from)) {
                        if (specificCoveredRegions.isEmpty() || !specificCoveredRegions.get(specificCoveredRegions.size() - 1).isTouching(chrom, from)) {
                            CoverageRegion cr = new CoverageRegion(chrom, from - 1, from, null, CoverageQuality.NO_COVERED);
                            cr.addDepthValue(depth);
                            specificCoveredRegions.add(cr);
                        } else {
                            specificCoveredRegions.get(specificCoveredRegions.size() - 1).extendsRegion(depth);
                        }
                    }
                }
            }
        }

//        for (PanelRegion region : panel.getRegions()) {
//            try {
//                int[] positions = getDepthByPosSplit(region, samReader);
//                for (int pos = 0; pos < positions.length; pos++) {
//                    int depth = positions[pos];
//                    int alignmentPos = region.getStart() + pos;
//
//
//                    if (depth < minDepth) {
//                        if (noCoveredRegions.isEmpty() || !noCoveredRegions.get(noCoveredRegions.size() - 1).isTouching(region.getContig(), alignmentPos)) {
//                            CoverageRegion cr = new CoverageRegion(region.getContig(), alignmentPos-1, alignmentPos, null, CoverageQuality.NO_COVERED);
//                            cr.addDepthValue(depth);
//                            noCoveredRegions.add(cr);
//                        } else {
//                            noCoveredRegions.get(noCoveredRegions.size() - 1).extendsRegion(depth);
//                        }
//                    } else if (depth < warningDepth) {
//                        if (badCoveredRegions.isEmpty() || !badCoveredRegions.get(badCoveredRegions.size() - 1).isTouching(region.getContig(), alignmentPos)) {
//                            CoverageRegion cr = new CoverageRegion(region.getContig(), alignmentPos-1, alignmentPos, null, CoverageQuality.LOW_COVERAGE);
//                            cr.addDepthValue(depth);
//                            badCoveredRegions.add(cr);
//                        } else {
//                            badCoveredRegions.get(badCoveredRegions.size() - 1).extendsRegion(depth);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//                throw e;
//            }
//        }

        // write regions
        try (FileOutputStream output = new FileOutputStream(outFile);
             GZIPOutputStream gos = new GZIPOutputStream(output)) {
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
    }



    private int[] getDepthByPosSplit(PanelRegion br, SamReader samReader) {
        int[] positions = new int[br.getSize()];
        int start = br.getStart();
        int end = br.getEnd();
        SAMRecordIterator iter = samReader.query(br.getContig(), start, end,false);
        while (iter.hasNext()) {
            final SAMRecord rec = iter.next();
            if (rec.isSecondaryOrSupplementary() || rec.getDuplicateReadFlag() || rec.getReadFailsVendorQualityCheckFlag()) continue;
            int readStart = rec.getStart() - start;

            if (readStart < 0) {
                readStart = 0;
            }

            int readEnd = rec.getEnd() - start;
            if (readEnd > end - start) readEnd = end - start;

            for (int i = readStart; i <= readEnd; i++) {
                positions[i]++;
            }
        }
        iter.close();
        return positions;
    }
}
