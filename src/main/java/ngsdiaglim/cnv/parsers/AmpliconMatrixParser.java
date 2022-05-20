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
import ngsdiaglim.exceptions.FileFormatException;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.utils.NumberUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AmpliconMatrixParser {

    private final Analysis analysis;
    private final Panel panel;
    private final File matrixFile;

    public AmpliconMatrixParser(Analysis analysis, Panel panel, File matixFile) {
        this.analysis = analysis;
        this.panel = panel;
        this.matrixFile = matixFile;
    }

    public CovCopCNVData parseMatrixFile() throws SQLException, FileFormatException {

        final int LABEL_COLUMNS_NUMBER = 2;
        TsvParserSettings parserSettings = new TsvParserSettings();

//        parserSettings.setLineSeparatorDetectionEnabled(true);
        RowListProcessor rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setNullValue("");
        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(matrixFile);

        List<Analysis> analysisSharingDesign = analysis.getRun().getAnalyses().stream().filter(p -> p.getAnalysisParameters().getPanel().equals(panel)).collect(Collectors.toList());

        String[] header = rowProcessor.getHeaders();
        LinkedHashMap<String, CNVSample> samples = new LinkedHashMap<>();

        // get the columns index of the patient to read (patient corresponding to the design
        List<Integer> columnsIndexToRead = new ArrayList<>();

//        Pattern barcodePattern = Pattern.compile("(BC(\\d+))");
//        Pattern ionxpressPattern = Pattern.compile("(IonXpress_(\\d+))");

        for (int i = LABEL_COLUMNS_NUMBER; i < header.length; i++) {
            int finalI = i;

            List<Analysis> analysisMatching = analysisSharingDesign.stream().filter(a -> a.getName().contains(header[finalI])).collect(Collectors.toList());
            if (analysisMatching.size() > 1) {
                throw new FileFormatException("Duplicate sample " + header[i]);
            }
            else if (!analysisMatching.isEmpty()){
                CNVSample sample = new CNVSample(analysisMatching.get(0), header[i]);
                samples.put(header[i], sample);
                columnsIndexToRead.add(i);
            }
        }

        List<String[]> rows = rowProcessor.getRows();

        Map<String, CovCopRegion> ampliconsMap = new LinkedHashMap<>();

        for (String[] row : rows) {
            if (row.length > header.length) {
                throw new FileFormatException("Bad number of columns " + String.join("\t", row));
            }

            String ampliconName = row[1];
            String geneName = row[0];

            PanelRegion panelRegion = panel.getRegion(ampliconName);
            if (panelRegion != null) {


                CovCopRegion covCopRegion = new CovCopRegion(panelRegion.getContig(), panelRegion.getStart(), panelRegion.getEnd(),
                        ampliconName, panelRegion.getPoolAmplification(), geneName);
                for (int k : columnsIndexToRead) {
                    if (!row[k].isEmpty() && !NumberUtils.isInt(row[k])) {
                        throw new FileFormatException("Incorrect value : " + String.join("\t", row));
                    }
                    Integer depth = null;
                    if (!row[k].isEmpty()) {
                        depth = Integer.parseInt(row[k]);
                    }
                    covCopRegion.addRawValue(depth);
                }
                if (ampliconsMap.containsKey(covCopRegion.getName())) {
                    throw new FileFormatException("Duplicate amplicon : " + covCopRegion.getName());
                } else {
                    ampliconsMap.put(covCopRegion.getName(), covCopRegion);
                }
            }

        }

        // sort the amplicons by their pool
        ObservableMap<String, ObservableList<CovCopRegion>> ampliconsByPool = FXCollections.observableHashMap();
        ampliconsMap.values().forEach(a -> {
            ampliconsByPool.putIfAbsent(a.getPool(), FXCollections.observableArrayList());
            ampliconsByPool.get(a.getPool()).add(a);
        });

        //sort each pool amplicons by their genomic position
        RegionComparator regionComparator = new RegionComparator();
        ampliconsByPool.values().forEach(o -> o.sort(regionComparator));

        CovCopCNVData covCopCNVData = new CovCopCNVData(analysis.getAnalysisParameters().getGenome(), panel);
        covCopCNVData.setSamples(samples);
        covCopCNVData.setCovcopRegions(ampliconsByPool);

        QualityCalculator.calculateStatistics(covCopCNVData);
        GenderCalculator.calculateGender(covCopCNVData);

        return covCopCNVData;
    }


    public static boolean checkFile(Panel panel, File matrix) throws FileFormatException, SQLException {
        final int LABEL_COLUMNS_NUMBER = 2;
        TsvParserSettings parserSettings = new TsvParserSettings();

//        parserSettings.setLineSeparatorDetectionEnabled(true);
        RowListProcessor rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setNullValue("");
        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(matrix);

        String[] header = rowProcessor.getHeaders();
//        LinkedHashMap<String, CNVSample> samples = new LinkedHashMap<>();

        Set<String> sampleNamesSet = new HashSet<>();
        for (int i = LABEL_COLUMNS_NUMBER; i < header.length; i++) {

            if (!sampleNamesSet.add(header[i])) {
                throw new FileFormatException("Duplicate sample " + header[i]);
            }
        }

        List<String[]> rows = rowProcessor.getRows();

        Map<String, CovCopRegion> ampliconsMap = new LinkedHashMap<>();

        for (String[] row : rows) {
            if (row.length > header.length) {
                throw new FileFormatException("Bad number of columns " + String.join("\t", row));
            }

            String ampliconName = row[1];
            String geneName = row[0];

            PanelRegion panelRegion = panel.getRegion(ampliconName);
            if (panelRegion == null) {
                throw new FileFormatException("The amplicon : " + ampliconName + " isn't fond in the panel " + panel.getName());
            }

            CovCopRegion covCopRegion = new CovCopRegion(panelRegion.getContig(), panelRegion.getStart(), panelRegion.getEnd(),
                    ampliconName, panelRegion.getPoolAmplification(), geneName);
            for (int k = LABEL_COLUMNS_NUMBER; k < header.length; k++) {
                if (!row[k].isEmpty() && !NumberUtils.isInt(row[k])) {
                    throw new FileFormatException("Incorrect value : " + String.join("\t", row));
                }
            }
            if (ampliconsMap.containsKey(covCopRegion.getName())) {
                throw new FileFormatException("Duplicate amplicon : " + covCopRegion.getName());
            } else {
                ampliconsMap.put(covCopRegion.getName(), covCopRegion);
            }


        }
        return true;
    }


    public static Set<String> getSampleNames(File matrix) throws FileFormatException, SQLException {
        final int LABEL_COLUMNS_NUMBER = 2;
        TsvParserSettings parserSettings = new TsvParserSettings();

//        parserSettings.setLineSeparatorDetectionEnabled(true);
        RowListProcessor rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setNullValue("");
        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(matrix);

        String[] header = rowProcessor.getHeaders();
//        LinkedHashMap<String, CNVSample> samples = new LinkedHashMap<>();

        Set<String> sampleNamesSet = new HashSet<>();
        for (int i = LABEL_COLUMNS_NUMBER; i < header.length; i++) {

            if (!sampleNamesSet.add(header[i])) {
                throw new FileFormatException("Duplicate sample " + header[i]);
            }
        }
        return sampleNamesSet;
    }


    public static CovCopCNVData getCNVSamples(File matrixFile, Genome genome, Panel panel) throws FileFormatException, SQLException {
        final int LABEL_COLUMNS_NUMBER = 2;
        TsvParserSettings parserSettings = new TsvParserSettings();

//        parserSettings.setLineSeparatorDetectionEnabled(true);
        RowListProcessor rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setNullValue("");
        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(matrixFile);

        String[] header = rowProcessor.getHeaders();
        LinkedHashMap<String, CNVSample> samples = new LinkedHashMap<>();

        for (int i = LABEL_COLUMNS_NUMBER; i < header.length; i++) {
            CNVSample sample = new CNVSample(null, header[i]);
            samples.put(header[i], sample);
        }

        List<String[]> rows = rowProcessor.getRows();

        Map<String, CovCopRegion> ampliconsMap = new LinkedHashMap<>();

        for (String[] row : rows) {
            if (row.length > header.length) {
                throw new FileFormatException("Bad number of columns " + String.join("\t", row));
            }

            String ampliconName = row[1];
            String geneName = row[0];

            PanelRegion panelRegion = panel.getRegion(ampliconName);
            if (panelRegion == null) {
                throw new FileFormatException("The amplicon : " + ampliconName + " isn't fond in the panel " + panel.getName());
            }

            CovCopRegion covCopRegion = new CovCopRegion(panelRegion.getContig(), panelRegion.getStart(), panelRegion.getEnd(),
                    ampliconName, panelRegion.getPoolAmplification(), geneName);
            for (int k = LABEL_COLUMNS_NUMBER; k < header.length; k++) {
                if (!row[k].isEmpty() && !NumberUtils.isInt(row[k])) {
                    throw new FileFormatException("Incorrect value : " + String.join("\t", row));
                }
                Integer depth = null;
                if (!row[k].isEmpty()) {
                    depth = Integer.parseInt(row[k]);
                }
                covCopRegion.addRawValue(depth);
            }
            if (ampliconsMap.containsKey(covCopRegion.getName())) {
                throw new FileFormatException("Duplicate amplicon : " + covCopRegion.getName());
            }
            else {
                ampliconsMap.put(covCopRegion.getName(), covCopRegion);
            }

        }

        // sort the amplicons by their pool
        ObservableMap<String, ObservableList<CovCopRegion>> ampliconsByPool = FXCollections.observableHashMap();
        ampliconsMap.values().forEach(a -> {
            ampliconsByPool.putIfAbsent(a.getPool(), FXCollections.observableArrayList());
            ampliconsByPool.get(a.getPool()).add(a);
        });

        //sort each pool amplicons by their genomic position
        RegionComparator regionComparator = new RegionComparator();
        ampliconsByPool.values().forEach(o -> o.sort(regionComparator));

        CovCopCNVData covCopCNVData = new CovCopCNVData(genome, panel);
        covCopCNVData.setSamples(samples);
        covCopCNVData.setCovcopRegions(ampliconsByPool);

        QualityCalculator.calculateStatistics(covCopCNVData);
        GenderCalculator.calculateGender(covCopCNVData);
        return covCopCNVData;
    }
}
