package ngsdiaglim.cnv.parsers;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import ngsdiaglim.cnv.*;
import ngsdiaglim.comparators.RegionComparator;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.MathUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CaptureDepthParser {

    private final Analysis analysis;
    private final Panel panel;
    private final int windowSize;

    public CaptureDepthParser(Analysis analysis, Panel panel, int windowSize) {
        this.analysis = analysis;
        this.panel = panel;
        this.windowSize = windowSize;
    }

    public CovCopCNVData parseDepthFiles() throws SQLException, IOException {
        // Select the analysis that sharing the design
        List<Analysis> analysisSharingDesign = analysis.getRun().getAnalyses().stream()
                .filter(a -> a.getAnalysisParameters().getPanel().equals(panel))
                .collect(Collectors.toList());
        LinkedHashMap<String, CNVSample> samples = new LinkedHashMap<>();
        HashMap<String, CovCopRegion> regions = new HashMap<>();

        int sampleIdx = 0;

        for (Analysis a : analysisSharingDesign) {

            File coverageFile = a.getDepthFile();
            if (!coverageFile.exists()) {
                throw new IOException("The file : " + coverageFile.getAbsolutePath() + " doesn't exists anymore.");
            }

            TsvParserSettings parserSettings = new TsvParserSettings();
            RowListProcessor rowProcessor = new RowListProcessor();
            parserSettings.setRowProcessor(rowProcessor);
            parserSettings.setHeaderExtractionEnabled(false);
            parserSettings.setNullValue("");
            TsvParser parser = new TsvParser(parserSettings);

            BufferedReader br = IOUtils.getFileReader(coverageFile);
//            parser.parse(coverageFile);
            parser.parse(br);


            CNVSample sample = new CNVSample(a, a.getSampleName());
            samples.put(sample.getBarcode(), sample);

            List<String[]> rows = rowProcessor.getRows();
            String lastContig = "";
            int lastPos = -1;
            List<String[]> depth = new ArrayList<>();
            int i = 0;
            for (String[] row : rows) {
                String contig = row[0];
                int pos = Integer.parseInt(row[1]);

                if (i == 0) {
                    lastContig = contig;
                    lastPos = pos;
                }
                if (!contig.equals(lastContig) || pos - lastPos > 1 || depth.size() >= windowSize) {
                    String windowName = "window" + i++;
                    if (sampleIdx == 0) {
                        int windowStart = Integer.parseInt(depth.get(0)[1]);
                        int windowEnd =  Integer.parseInt(depth.get(depth.size() - 1)[1]);
                        PanelRegion panelRegion = panel.getRegion(lastContig, windowStart, windowEnd);
                        String geneName;
                        if (panelRegion == null) geneName = "Unknown";
                        else geneName = panelRegion.getName();
                        CovCopRegion covCopRegion = new CovCopRegion(lastContig, windowStart, windowEnd, windowName, "A", geneName);
                        regions.put(windowName, covCopRegion);
                    }

                    CovCopRegion cnvAmplicon = regions.get(windowName);
                    List<Double> depths = new ArrayList<>();
                    for (String[] strings : depth) {
                        depths.add(Double.parseDouble(strings[2]));
//                        depths.add(Double.parseDouble(row[2]));
                    }
                    Double median = MathUtils.median(depths);
                    if (median != null) {
                        cnvAmplicon.addRawValue(median.intValue());
                    }
                    else {
                        cnvAmplicon.addRawValue(0);
                    }
                    lastContig = contig;
                    lastPos = pos;
                    depth.clear();
                } else {
                    lastContig = contig;
                    lastPos = pos;
                }
                depth.add(row);
            }
            sampleIdx++;
        }
        // sort the amplicons by their pool
        ObservableMap<String, ObservableList<CovCopRegion>> regionByPool = FXCollections.observableHashMap();
        regions.values().forEach(a -> {
            regionByPool.putIfAbsent(a.getPool(), FXCollections.observableArrayList());
            regionByPool.get(a.getPool()).add(a);
        });
        regionByPool.forEach((pool, regionsList) -> regionsList.sort(new RegionComparator()));

        CovCopCNVData cnvData = new CovCopCNVData(analysis.getAnalysisParameters().getGenome(), panel);
        cnvData.setSamples(samples);
        cnvData.setCovcopRegions(regionByPool);

        QualityCalculator.calculateStatistics(cnvData);
        GenderCalculator.calculateGender(cnvData);

        return cnvData;
    }


    public static CovCopCNVData getCNVSample(Genome genome, CNVControlGroup cnvControlGroup, int windowSize) throws IOException, SQLException {
        LinkedHashMap<String, CNVSample> samples = new LinkedHashMap<>();
        HashMap<String, CovCopRegion> regions = new HashMap<>();

        int sampleIdx = 0;

        for (CNVControl c : cnvControlGroup.getControlsList()) {

            File coverageFile = c.getDepthFile();
            if (!coverageFile.exists()) {
                throw new IOException("The file : " + coverageFile.getAbsolutePath() + " doesn't exists anymore.");
            }

            TsvParserSettings parserSettings = new TsvParserSettings();
            RowListProcessor rowProcessor = new RowListProcessor();
            parserSettings.setRowProcessor(rowProcessor);
            parserSettings.setHeaderExtractionEnabled(false);
            parserSettings.setNullValue("");
            TsvParser parser = new TsvParser(parserSettings);

            BufferedReader br = IOUtils.getFileReader(coverageFile);
//            parser.parse(coverageFile);
            parser.parse(br);


            CNVSample sample = new CNVSample(null, c.getName());
            samples.put(sample.getBarcode(), sample);

            List<String[]> rows = rowProcessor.getRows();
            String lastContig = "";
            int lastPos = -1;
            List<String[]> depth = new ArrayList<>();
            int i = 0;
            for (String[] row : rows) {
                String contig = row[0];
                int pos = Integer.parseInt(row[1]);

                if (i == 0) {
                    lastContig = contig;
                    lastPos = pos;
                }
                if (!contig.equals(lastContig) || pos - lastPos > 1 || depth.size() >= windowSize) {
                    String windowName = "window" + i++;
                    if (sampleIdx == 0) {
                        int windowStart = Integer.parseInt(depth.get(0)[1]);
                        int windowEnd = Integer.parseInt(depth.get(depth.size() - 1)[1]);
                        PanelRegion panelRegion = cnvControlGroup.getPanel().getRegion(lastContig, windowStart, windowEnd);
                        String geneName;
                        if (panelRegion == null) geneName = "Unknown";
                        else geneName = panelRegion.getName();
                        CovCopRegion covCopRegion = new CovCopRegion(lastContig, windowStart, windowEnd, windowName, "A", geneName);
                        regions.put(windowName, covCopRegion);
                    }

                    CovCopRegion cnvAmplicon = regions.get(windowName);
                    List<Double> depths = new ArrayList<>();
                    for (String[] strings : depth) {
                        depths.add(Double.parseDouble(strings[2]));
//                        depths.add(Double.parseDouble(row[2]));
                    }
                    Double median = MathUtils.median(depths);
                    if (median != null) {
                        cnvAmplicon.addRawValue(median.intValue());
                    } else {
                        cnvAmplicon.addRawValue(0);
                    }
                    lastContig = contig;
                    lastPos = pos;
                    depth.clear();
                } else {
                    lastContig = contig;
                    lastPos = pos;
                }
                depth.add(row);
            }
            sampleIdx++;
        }
        // sort the amplicons by their pool
        ObservableMap<String, ObservableList<CovCopRegion>> regionByPool = FXCollections.observableHashMap();
        regions.values().forEach(a -> {
            regionByPool.putIfAbsent(a.getPool(), FXCollections.observableArrayList());
            regionByPool.get(a.getPool()).add(a);
        });
        regionByPool.forEach((pool, regionsList) -> regionsList.sort(new RegionComparator()));

        CovCopCNVData covCopCNVData = new CovCopCNVData(genome, cnvControlGroup.getPanel());
        covCopCNVData.setSamples(samples);
        covCopCNVData.setCovcopRegions(regionByPool);

        QualityCalculator.calculateStatistics(covCopCNVData);
        GenderCalculator.calculateGender(covCopCNVData);
        return covCopCNVData;
    }

}
