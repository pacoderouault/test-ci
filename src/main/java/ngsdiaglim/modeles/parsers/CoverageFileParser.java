package ngsdiaglim.modeles.parsers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.comparators.RegionComparator;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageRegion;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoverageFileParser {

    private final static String column_delimiter = "\t";

    public static ObservableList<CoverageRegion> parseCoverageFile(File file, AnalysisParameters params) throws IOException, MalformedCoverageFile {
        ObservableList<CoverageRegion> coverageRegions = FXCollections.observableArrayList();

        try (BufferedReader reader = IOUtils.getFileReader(file)) {
            String line;
            int lineCount = 0;
            while((line = reader.readLine()) != null) {
                lineCount++;
                String[] tks = line.split(column_delimiter);
                if (tks.length >= 5) {

                    if (!NumberUtils.isInt(tks[1]) || ! NumberUtils.isInt(tks[2]) || !NumberUtils.isFloat(tks[4])) {
                        throw new MalformedCoverageFile("Invalid file format, line " + lineCount);
                    }
                    String contig = tks[0];
                    int start = Integer.parseInt(tks[1]);
                    int end = Integer.parseInt(tks[2]);
                    String name = tks[3];
                    double depth = Double.parseDouble(tks[4]);

                    CoverageQuality coverageQuality = depth < params.getMinDepth() ? CoverageQuality.NO_COVERED : CoverageQuality.LOW_COVERAGE;

                    CoverageRegion cr = new CoverageRegion(
                        contig, start, end, name, coverageQuality
                    );
                    cr.setMeanDepth(depth);
                    coverageRegions.add(cr);
                }
            }
        }
        coverageRegions.sort(new RegionComparator());
        return coverageRegions;
    }


    public static ObservableList<SpecificCoverageRegion> parseSpecCoverageFile(File file, AnalysisParameters params) throws IOException, MalformedCoverageFile {

        ObservableList<SpecificCoverageRegion> specCoverageRegions = FXCollections.observableArrayList();
        List<CoverageRegion> coverageRegions = new ArrayList<>();

        if (params.getSpecificCoverageSet() != null) {
            try (BufferedReader reader = IOUtils.getFileReader(file)) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    String[] tks = line.split(column_delimiter);
                    if (tks.length >= 5) {

                        if (!NumberUtils.isInt(tks[1]) || !NumberUtils.isInt(tks[2]) || !NumberUtils.isFloat(tks[4])) {
                            throw new MalformedCoverageFile("Invalid file format, line " + lineCount);
                        }
                        String contig = tks[0];
                        int start = Integer.parseInt(tks[1]);
                        int end = Integer.parseInt(tks[2]);
                        String name = tks[3];
                        double depth = Double.parseDouble(tks[4]);
                        CoverageRegion cr = new CoverageRegion(contig, start, end, name, CoverageQuality.NO_COVERED);
                        cr.setMeanDepth(depth);
                        coverageRegions.add(cr);
//                        for (SpecificCoverage region : params.getSpecificCoverageSet().getOverlapingRegions(contig, start, end, depth)) {
//                            SpecificCoverageRegion scr = new SpecificCoverageRegion(
//                                    region, contig, start, end, name, CoverageQuality.NO_COVERED
//                            );
//                            scr.setMeanDepth(depth);
//                            coverageRegions.add(scr);
//                        }
                    }
                }
            }
        }
        coverageRegions.sort(new RegionComparator());

        for (SpecificCoverage sc : params.getSpecificCoverageSet().getSpecificCoverageList()) {
            SpecificCoverageRegion scr = new SpecificCoverageRegion(sc);
//            System.out.println(sc.getName());
//            boolean regionFound = false;
            for (CoverageRegion cr : coverageRegions) {
                if (sc.overlaps(cr.getContig(), cr.getStart(), cr.getEnd())) {
//                    SpecificCoverageRegion scr = new SpecificCoverageRegion(sc);
//                    scr.setCoverageRegion(cr);
                    scr.addCoverageRegions(cr);
//                    regionFound = true;
                }
            }
            specCoverageRegions.add(scr);
//            System.out.println(regionFound);
//            if (!regionFound) { // create empty region
//                SpecificCoverageRegion scr = new SpecificCoverageRegion(sc);
//                specCoverageRegions.add(scr);
//            }
        }

        return specCoverageRegions;
    }
}
