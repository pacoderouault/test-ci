package ngsdiaglim.modeles.parsers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.comparators.RegionComparator;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.exceptions.MalformedCoverageFile;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

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


}
