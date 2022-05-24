package ngsdiaglim.modeles.reports.anapath;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.DateFormatterUtils;
import ngsdiaglim.utils.MathUtils;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.TableViewUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AnnotationExporter {

    private static final Logger logger = LogManager.getLogger(AnnotationExporter.class);
    private final Analysis analysis;
    private final TableView<Annotation> table;

    public AnnotationExporter(Analysis analysis, TableView<Annotation> table) {
        this.analysis = analysis;
        this.table = table;
    }

    public void export(File outFile) throws IOException {
        XSSFSheet sheet;
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            sheet = workbook.createSheet(analysis.getSampleName());


            int rowIdx = 0;
            rowIdx = addHeader(sheet, rowIdx);
            rowIdx = addTableHeader(sheet, rowIdx);
            addTableItems(sheet, rowIdx);

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(outFile);
            workbook.write(fileOut);
            workbook.write(fileOut);
            fileOut.close();

        }
    }

    private int addHeader(XSSFSheet sheet, int rowIdx) {
        sheet.createRow(rowIdx++).createCell(0).setCellValue("#" + analysis.getRun().getName());
        sheet.createRow(rowIdx++).createCell(0).setCellValue("#" + analysis.getName());
        sheet.createRow(rowIdx++).createCell(0).setCellValue("#" + DateFormatterUtils.formatLocalDateTime(LocalDateTime.now()));
        return rowIdx;
    }

    private int addTableHeader(XSSFSheet sheet, int rowIdx) {
        XSSFRow row = sheet.createRow(rowIdx++);
        int cellIdx = 0;
        for (TableColumn<Annotation, ?> column : table.getColumns()) {
            row.createCell(cellIdx++).setCellValue(TableViewUtils.getColmunTitle(column));
        }
        return rowIdx;
    }

    private int addTableItems(XSSFSheet sheet, int rowIdx) {


        for (Annotation item : table.getItems()) {
            XSSFRow row = sheet.createRow(rowIdx++);
            int cellIdx = 0;
            for (TableColumn<Annotation, ?> column : table.getColumns()) {
                XSSFCell cell = row.createCell(cellIdx++);
                if (TableViewUtils.getColmunTitle(column).equals(VariantsTableColumns.HOTSPOT.getName())) {
                    if (item.getVariant().isHotspot()) {
                        cell.setCellValue("Hotspot");
                    } else {
                        cell.setCellValue("");
                    }
                } else if (TableViewUtils.getColmunTitle(column).equals(VariantsTableColumns.ACMG.getName())) {
                    cell.setCellValue(item.getVariant().getAcmg().getName());
                } else if (TableViewUtils.getColmunTitle(column).equals(VariantsTableColumns.VAF.getName())) {
                    if (item.getVaf() != null) {
                        cell.setCellValue(String.valueOf(NumberUtils.round(item.getVaf(), 2)));
                    } else {
                        cell.setCellValue("");
                    }
                } else {
                    if (column.getCellObservableValue(item) != null && column.getCellObservableValue(item).getValue() != null) {
                        cell.setCellValue(String.valueOf(column.getCellObservableValue(item).getValue()));
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
        }
        return rowIdx;
    }
}
