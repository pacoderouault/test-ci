package ngsdiaglim.modeles.reports.bgm;

import javafx.scene.paint.Color;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.enumerations.CoverageQuality;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.enumerations.SamplingType;
import ngsdiaglim.enumerations.Zygotie;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.biofeatures.CoverageRegion;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.ColorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportCreator {

    private final ReportData reportData;
    private final File reportFile;

    private final HashMap<String, List<XWPFRun>> paragraphMap = new HashMap<>();
    private XWPFTable geneListTable;
    private XWPFTable noCovRegionsTable;

    private final List<String> tags = new ArrayList<>();

    private final static String TAG_REPORT_DATETIME = "FOOTER_NAME_FOOTER";
    private final static String TAG_REPORT_NAME_FOOTER = "REPORT_NAME_FOOTER";
    private final static String TAG_PRESRIBER_NAME = "PRESCRIBER_NAME";
    private final static String TAG_PRESRIBER_ADDRESS = "PRESCRIBER_ADDRESS";
    private final static String TAG_PATIENT_NAME_AND_BIRTHDATE = "PATIENT_NAME_AND_BIRTHDATE";
    private final static String TAG_PATIENT_BARCODE = "PATIENT_BARCODE";
    private final static String TAG_SAMPLING_INFO = "SAMPLING_INFO";
    private final static String TAG_PATIENT_INFO_TITLE = "PATIENT_INFO_TITLE";
    private final static String TAG_GENES_NUMBER = "GENES_NUMBER";
    private final static String TAG_REPORT_MUTATION_RESULTS = "REPORT_MUTATION_RESULTS";
    private final static String TAG_REPORT_COMMENTARY = "REPORT_COMMENTARY";
    private final static String TAG_REPORT_POSITIVE_MUTATION_NB= "REPORT_POSITIVE_MUTATION_NB";
    private final static String TAG_ANNEXE_1_TABLE= "ANNEXE_1_TABLE";
    private final static String TAG_ANNEXE_3_TABLE= "ANNEXE_3_TABLE";

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportCreator(ReportData reportData, File reportFile) {
        this.reportData = reportData;
        this.reportFile = reportFile;
        tags.add(TAG_REPORT_DATETIME);
        tags.add(TAG_REPORT_NAME_FOOTER);
        tags.add(TAG_PRESRIBER_NAME);
        tags.add(TAG_PRESRIBER_ADDRESS);
        tags.add(TAG_PATIENT_NAME_AND_BIRTHDATE);
        tags.add(TAG_PATIENT_BARCODE);
        tags.add(TAG_SAMPLING_INFO);
        tags.add(TAG_PATIENT_INFO_TITLE);
        tags.add(TAG_GENES_NUMBER);
        tags.add(TAG_REPORT_MUTATION_RESULTS);
        tags.add(TAG_REPORT_COMMENTARY);
        tags.add(TAG_REPORT_POSITIVE_MUTATION_NB);
    }

    public void createReport() throws Exception {
        XWPFDocument document = getReportBlankDoc();
        getParagraph(document);
        getTables(document);
        fillFirstPageReport();
        fillAnnexe1Table();
        fillAnnexe3Table();
        replaceTextInFooter(document);
        closeDocument(document);
    }

    private void getParagraph(XWPFDocument document) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun r : paragraph.getRuns()) {
                for (String tag : tags) {
                    if (r.text().contains(tag)) {
                        if (!paragraphMap.containsKey(tag)) {
                            paragraphMap.put(tag, new ArrayList<>());
                        }
                        paragraphMap.get(tag).add(r);
                    }
                }
            }
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        for (XWPFRun r : paragraph.getRuns()) {
                            for (String tag : tags) {
                                if (r.text().contains(tag)) {
                                    if (!paragraphMap.containsKey(tag)) {
                                        paragraphMap.put(tag, new ArrayList<>());
                                    }
                                    paragraphMap.get(tag).add(r);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void getTables(XWPFDocument document) {
        for (XWPFTable t : document.getTables()) {
            for (XWPFTableRow row : t.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {

                    if (cell.getText().contains(TAG_ANNEXE_1_TABLE)) {
                        geneListTable = t;
                    }

                    else if (cell.getText().contains(TAG_ANNEXE_3_TABLE)) {
                        noCovRegionsTable = t;
                    }
                }
            }
        }
    }


    private void fillFirstPageReport() {
        setPrescriberName();
        setPrescriberAdress();
        setReportDatetime();
        setReportGeneNumber();
        setTagPatientNameAndBirthdate();
        setTagPatientBarcode();
        setTagSamplingInfo();
        setTagReportMutationResults();
        setTagReportCommentary();
        setTagReportPositiveMutationNb();
    }

    private void fillAnnexe1Table() {
        if (geneListTable != null) {
            int templateRowIdx = 2;

            XWPFRun templateRun = geneListTable.getRow(templateRowIdx).getCell(0).getParagraphs().get(0).getRuns().get(0);

            Iterator<Gene> geneIterator = reportData.getGenesList().iterator();
            while (geneIterator.hasNext()) {
                Gene firstGene = geneIterator.next();
                Gene secondGene = geneIterator.hasNext() ? geneIterator.next() : null;
                XWPFTableRow row = new XWPFTableRow(getTableRowFromTemplate(geneListTable, templateRowIdx), geneListTable);
                row.setCantSplitRow(true);
                XWPFRun firstGeneNameRun = row.getCell(0).getParagraphs().get(0).getRuns().get(0);
                firstGeneNameRun.setText(firstGene.getGeneName(), 0);
                copyRunStyles(templateRun, firstGeneNameRun);

                Gene firstPanelGene = reportData.getAnalysis().getAnalysisParameters().getGeneSet().getGene(firstGene.getGeneName());
                if (firstPanelGene != null) {
                    XWPFRun firstTranscriptsRun = row.getCell(1).getParagraphs().get(0).createRun();
                    firstTranscriptsRun.setText(firstPanelGene.getTranscriptsAsString());
                    copyRunStyles(templateRun, firstTranscriptsRun);
                }

                if (secondGene != null) {
                    XWPFRun secondGeneNameRun = row.getCell(2).getParagraphs().get(0).createRun();
                    secondGeneNameRun.setText(secondGene.getGeneName());
                    copyRunStyles(templateRun, secondGeneNameRun);
                    Gene secondPanelGene = reportData.getAnalysis().getAnalysisParameters().getGeneSet().getGene(secondGene.getGeneName());
                    if (secondPanelGene != null) {
                        XWPFRun secondTranscriptsRun = row.getCell(3).getParagraphs().get(0).createRun();
                        secondTranscriptsRun.setText(secondPanelGene.getTranscriptsAsString());
                        copyRunStyles(templateRun, secondTranscriptsRun);
                    }
                }
                geneListTable.addRow(row);
            }
            geneListTable.removeRow(templateRowIdx);
            geneListTable.removeRow(0);
        }
    }

    private void fillAnnexe3Table() throws SQLException {
        if (noCovRegionsTable != null) {
            int templateRowIdx = 2;

            XWPFRun templateRun = noCovRegionsTable.getRow(templateRowIdx).getCell(0).getParagraphs().get(0).getRuns().get(0);

            for (CoverageRegion coverageRegion : reportData.getAnalysis().getCoverageRegions()) {
                if (coverageRegion.getCoverageQuality().equals(CoverageQuality.NO_COVERED) && !(coverageRegion.getContig().equals("chrY") && !coverageRegion.getContig().equals("Y"))) {
                    XWPFTableRow row = new XWPFTableRow(getTableRowFromTemplate(noCovRegionsTable, templateRowIdx), noCovRegionsTable);
                    row.setCantSplitRow(true);

                    Panel panel = reportData.getAnalysis().getAnalysisParameters().getPanel();
                    List<PanelRegion> overlappingRegions = panel.getRegions(coverageRegion.getContig(), coverageRegion.getStart(), coverageRegion.getEnd());
                    StringJoiner geneNames = new StringJoiner(";");
                    for (PanelRegion region : overlappingRegions) {
                        geneNames.add(region.getName());
                    }

                    int cellIdx = 0;
                    // genes
                    XWPFRun genesRun = row.getCell(cellIdx++).getParagraphs().get(0).getRuns().get(0);
                    genesRun.setText(geneNames.toString(), 0);
                    copyRunStyles(templateRun, genesRun);

                    // contig
                    XWPFRun contigRun = row.getCell(cellIdx++).getParagraphs().get(0).createRun();
                    contigRun.setText(coverageRegion.getContig(), 0);
                    copyRunStyles(templateRun, contigRun);

                    // start
                    XWPFRun startRun = row.getCell(cellIdx++).getParagraphs().get(0).createRun();
                    startRun.setText(String.valueOf(coverageRegion.getStart()), 0);
                    copyRunStyles(templateRun, startRun);

                    // end
                    XWPFRun endRun = row.getCell(cellIdx++).getParagraphs().get(0).createRun();
                    endRun.setText(String.valueOf(coverageRegion.getEnd()), 0);
                    copyRunStyles(templateRun, endRun);

                    // size
                    XWPFRun sizeRun = row.getCell(cellIdx++).getParagraphs().get(0).createRun();
                    sizeRun.setText(String.valueOf(coverageRegion.getSize()), 0);
                    copyRunStyles(templateRun, sizeRun);

                    // profondeur
                    XWPFRun depthRun = row.getCell(cellIdx++).getParagraphs().get(0).createRun();
                    depthRun.setText(String.valueOf(coverageRegion.getAverageDepth()), 0);
                    copyRunStyles(templateRun, depthRun);

                    noCovRegionsTable.addRow(row);
                }
            }
            noCovRegionsTable.removeRow(templateRowIdx);
            noCovRegionsTable.removeRow(0);
        }
    }

    private void setPrescriberName() {
        if (paragraphMap.containsKey(TAG_PRESRIBER_NAME)) {
            for (XWPFRun run : paragraphMap.get(TAG_PRESRIBER_NAME)) {
                String text = run.text().replace(TAG_PRESRIBER_NAME, reportData.getPrescriber().getStatus()
                        + " " + reportData.getPrescriber().getFirstName()
                        + " " + reportData.getPrescriber().getLastName());
                run.setText(text, 0);
            }
        }
    }

    private void setPrescriberAdress() {
        if (paragraphMap.containsKey(TAG_PRESRIBER_ADDRESS)) {
            for (XWPFRun run : paragraphMap.get(TAG_PRESRIBER_ADDRESS)) {
                IRunBody parent = run.getParent();
                if (parent instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) parent;
                    for (String addressLine : reportData.getPrescriber().getAddress().split("\n")) {
                        if (!addressLine.isEmpty()) {
                            XWPFRun r = paragraph.createRun();
                            r.setText(addressLine);
                            r.addBreak();
                            copyRunStyles(run, r);
                        }
                    }
                    paragraph.removeRun(paragraph.getRuns().indexOf(run));
                }
            }
        }
    }

    private void setReportDatetime() {
        if (paragraphMap.containsKey(TAG_REPORT_DATETIME)) {
            for (XWPFRun run : paragraphMap.get(TAG_REPORT_DATETIME)) {
                run.setText(run.getText(0).replace(TAG_REPORT_DATETIME, dateTimeFormatter.format(LocalDateTime.now())), 0);
            }
        }
    }

    private void setReportGeneNumber() {
        if (paragraphMap.containsKey(TAG_GENES_NUMBER)) {
            for (XWPFRun run : paragraphMap.get(TAG_GENES_NUMBER)) {
                run.setText(run.getText(0).replace(TAG_GENES_NUMBER, String.valueOf(reportData.getGenesList().size())), 0);
            }
        }
    }

    private void setTagPatientNameAndBirthdate() {
        if (paragraphMap.containsKey(TAG_PATIENT_NAME_AND_BIRTHDATE)) {
            for (XWPFRun run : paragraphMap.get(TAG_PATIENT_NAME_AND_BIRTHDATE)) {
                run.setText(run.getText(0).replace(TAG_PATIENT_NAME_AND_BIRTHDATE, getPatientNameAndBirthdate()), 0);
            }
        }
    }

    private void setTagPatientBarcode() {
        if (paragraphMap.containsKey(TAG_PATIENT_BARCODE)) {
            for (XWPFRun run : paragraphMap.get(TAG_PATIENT_BARCODE)) {
                run.setText(run.getText(0).replace(TAG_PATIENT_BARCODE, reportData.getBarcode()), 0);
            }
        }
    }

    private void setTagSamplingInfo() {
        if (paragraphMap.containsKey(TAG_SAMPLING_INFO)) {
            for (XWPFRun run : paragraphMap.get(TAG_SAMPLING_INFO)) {
                run.setText(run.getText(0).replace(TAG_SAMPLING_INFO, getSamplingInfo()), 0);
            }
        }
    }

    private void setTagReportMutationResults() {
        if (paragraphMap.containsKey(TAG_REPORT_MUTATION_RESULTS)) {
            for (XWPFRun run : paragraphMap.get(TAG_REPORT_MUTATION_RESULTS)) {
                IRunBody parent = run.getParent();
                if (parent instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) parent;
                    if (reportData.getReportedVariants() == null || reportData.getReportedVariants().isEmpty()) {
                        XWPFRun r = paragraph.createRun();
                        r.setText("Aucun variant exonique à priori pathogène de type SNP ou petit Indel n'a été détecté.");
                        r.addBreak();
                        copyRunStyles(run, r);
                    } else {
                        for (Annotation a : reportData.getReportedVariants()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Présence, à l'état ");
                            if (a.getZygotie().equals(Zygotie.HETEROZYGOUS)) {
                                sb.append("hétérozygote");
                            } else {
                                sb.append("homozygote");
                            }
                            sb.append(" de la mutation pathogène");
                            if (a.getTranscriptConsequence() != null) {
                                boolean hasHGVSc = !StringUtils.isEmpty(a.getTranscriptConsequence().getHgvsc());
                                boolean hasHGVSp = !StringUtils.isEmpty(a.getTranscriptConsequence().getHgvsp());
                                if (hasHGVSc) {
                                    sb.append(" ").append(a.getTranscriptConsequence().getHgvsc());
                                }
                                if (hasHGVSp) {
                                    sb.append(" ");
                                    if (hasHGVSc) {
                                        sb.append("(");
                                    }
                                    sb.append(a.getTranscriptConsequence().getHgvsp());
                                    if (hasHGVSc) {
                                        sb.append(")");
                                    }
                                }
                            } else {
                                sb.append(a.getVariant().toString());
                            }
                            sb.append(", du gène ");
                            if (a.getGene() != null) {
                                sb.append(a.getGene().getGeneName());
                            } else {
                                if (a.getTranscriptConsequence() != null && !StringUtils.isEmpty(a.getTranscriptConsequence().getGeneName())) {
                                    sb.append(" ").append(a.getTranscriptConsequence().getGeneName());
                                }
                            }
                            XWPFRun r = paragraph.createRun();
                            r.setText(sb.toString());
                            r.addBreak();
                            copyRunStyles(run, r);
                            r.setColor(ColorUtils.colorToHex(Color.RED, false));
                        }
                    }
//                    XWPFRun r = paragraph.createRun();
//                    r.setText(addressLine);
//                    r.addBreak();
//                    copyRunStyles(run, r);

                    paragraph.removeRun(paragraph.getRuns().indexOf(run));
                }
//                run.setText(run.getText(0).replace(TAG_REPORT_MUTATION_RESULTS, getMutationResult()), 0);
            }
        }
    }

    private void setTagReportCommentary() {
        if (paragraphMap.containsKey(TAG_REPORT_COMMENTARY)) {
            for (XWPFRun run : paragraphMap.get(TAG_REPORT_COMMENTARY)) {
                IRunBody parent = run.getParent();
                if (parent instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) parent;
                    for (ReportCommentary commentary : reportData.getCommentaries()) {
                        XWPFRun r = paragraph.createRun();
                        r.setText(commentary.getComment());
                        r.addBreak();
                        copyRunStyles(run, r);
                    }
                    paragraph.removeRun(paragraph.getRuns().indexOf(run));
                }
            }
        }
    }

    private void setTagReportPositiveMutationNb() {
        if (paragraphMap.containsKey(TAG_REPORT_POSITIVE_MUTATION_NB)) {
            for (XWPFRun run : paragraphMap.get(TAG_REPORT_POSITIVE_MUTATION_NB)) {
                run.setText(run.getText(0).replace(TAG_REPORT_POSITIVE_MUTATION_NB, "Par ailleurs, conformément au décret N°2013-527," +
                        " les membres de la famille potentiellement concernés doivent être informés de l'existence de" +
                        " cette mutation héréditaire dans la famille."), 0);
            }
        }
    }

    private XWPFDocument getReportBlankDoc() throws InvalidFormatException, IOException {
//        ClassLoader classLoader = getClass().getClassLoader();
        File template = new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.BGM_BLANK_REPORT.name()));

        XWPFDocument document = new XWPFDocument(OPCPackage.open(template.getPath()));

//        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
//        CTPageMar pageMar = sectPr.addNewPgMar();
//        pageMar.setLeft(BigInteger.valueOf(360L));
//        pageMar.setTop(BigInteger.valueOf(250L));
//        pageMar.setRight(BigInteger.valueOf(360L));
        return document;
    }


    private String getPatientNameAndBirthdate() {
        if (reportData.isChild()) {
            return "Enfant " + reportData.getFirstName() + " " + reportData.getLastName()
                    + ", né le " + dateFormatter.format(reportData.getBirthdate());
        } else {
            if (reportData.getGender().equals(Gender.FEMALE)) {
                if (reportData.getMaidenName() != null && !reportData.getMaidenName().isEmpty()) {
                    return "Madame " + reportData.getFirstName() + " " + reportData.getLastName()
                            + " née " + reportData.getMaidenName() + ", le " + dateFormatter.format(reportData.getBirthdate());
                } else {
                    return "Madame " + reportData.getFirstName() + " " + reportData.getLastName()
                            + ", née le " + dateFormatter.format(reportData.getBirthdate());

                }
            } else {
                return "Monsieur " + reportData.getFirstName() + " " + reportData.getLastName()
                        + ", né le " + dateFormatter.format(reportData.getBirthdate());
            }
        }
    }


    private String getSamplingInfo() {
        StringBuilder sb = new StringBuilder();
        if (reportData.getSamplingType().equals(SamplingType.DNA)) {
            sb.append("ADN dissous");
        } else {
            sb.append("SANG total EDTA");
        }
        if (reportData.getSamplingDate() != null) {
            sb.append(" prélevé le ").append(dateFormatter.format(reportData.getSamplingDate()));
        }
        if (reportData.getSamplingArrivedDate() != null) {
            sb.append(", reçu le ").append(dateFormatter.format(reportData.getSamplingArrivedDate()));
        }
        return sb.toString();
    }


    private void replaceTextInFooter(XWPFDocument document) {
        for (XWPFFooter footer : document.getFooterList()) {
            for (XWPFTable t : footer.getTables()) {
                for (XWPFTableRow row : t.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        if (cell.getText().contains(TAG_REPORT_NAME_FOOTER)) {
                            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                                for (XWPFRun run : paragraph.getRuns()) {
                                    SimpleDateFormat formatter = new SimpleDateFormat("EEEE d MMM yyyy 'à' HH'h'mm");
                                    Date today = new Date();
                                    String footerStr = "Compte-rendu " + reportData.getFirstName() + " "
                                            + reportData.getLastName()
                                            + ", Edité le " + formatter.format(today);
                                    run.setText(run.getText(0).replaceAll(TAG_REPORT_NAME_FOOTER, footerStr), 0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void closeDocument(XWPFDocument document) throws Exception {
        FileOutputStream outputSream = new FileOutputStream(reportFile);
        document.write(outputSream);
        outputSream.close();
        reportFile.setWritable(true);
    }


    public static void copyRunStyles(XWPFRun source, XWPFRun run) {
        run.setFontFamily(source.getFontFamily());
        if (source.getFontSize() > 0) {
            run.setFontSize(source.getFontSize());
        }
        run.setBold(source.isBold());
        run.setItalic(source.isItalic());
        run.setCapitalized(source.isCapitalized());
        run.setSmallCaps(source.isSmallCaps());
        run.setColor(source.getColor());
    }

    public static CTRow getTableRowFromTemplate(XWPFTable table, int templateRowIndex) {
        XWPFTableRow oldRow = table.getRow(templateRowIndex);
        CTRow ctrow = null;
        try {
            ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
        } catch (IOException | XmlException e) {e.printStackTrace();
        }
        return ctrow;
    }

    public static void setTableColumnsWidth(XWPFTable table, long... widths) {
        CTTblGrid grid = table.getCTTbl().addNewTblGrid();
        for (long w : widths) {
            grid.addNewGridCol().setW(BigInteger.valueOf(w));
        }
    }
}
