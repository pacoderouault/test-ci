package ngsdiaglim.modeles;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.TableViewUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TableExporter {

    /**
     * Export the tableview content to a tsv file
     */
    public static void exportTableToExcel(Analysis analysis, TableView<Annotation> table, List<TableColumn<Annotation, ?>> columns, File outFile) throws IOException {

        // Create a Workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
//        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        XSSFSheet sheet = workbook.createSheet(analysis.getRun().getName() + "-" + analysis.getName() + "-" + analysis.getSampleName());

//        XSSFCellStyle hrefStyle = workbook.createCellStyle();

        // HEADER
        XSSFRow headerRow = sheet.createRow(0);
        int colNum = 0;
        for (TableColumn<Annotation, ?> tc : table.getColumns()) {
            if (columns.contains(tc)) {
//                if (TableUtils.getColumnTitle(tc).equals(ColumnNames.EXTERNAL_LINKS.getName())) {
//                    XSSFCell cellGnomad = headerRow.createCell(colNum++);
//                    cellGnomad.setCellValue("gnomad");
//
//                    XSSFCell celldbSNP = headerRow.createCell(colNum++);
//                    celldbSNP.setCellValue("dnSNP");
//
//                    XSSFCell cellOMIM = headerRow.createCell(colNum++);
//                    cellOMIM.setCellValue("Cosmic");
//
//                }
//                else {
                XSSFCell cell = headerRow.createCell(colNum++);
                cell.setCellValue(TableViewUtils.getColmunTitle(tc));
//                }
            }
        }

        // VARIANTS
        // write each rows
        int rowNum = 1;
        for (Annotation item : table.getItems()) {
            XSSFRow row = sheet.createRow(rowNum++);

            colNum = 0;
            for (TableColumn<Annotation, ?> column : table.getColumns()) {
                if (columns.contains(column)) {

                    XSSFCell cell = row.createCell(colNum++);

                    if (TableViewUtils.getColmunTitle(column).equals(VariantsTableColumns.ACMG.getName())) {
                        cell.setCellValue(item.getVariant().getAcmg().getName());
                    } else if (TableViewUtils.getColmunTitle(column).equals(VariantsTableColumns.VAF.getName())) {
                        if (item.getVaf() != null) {
                            cell.setCellValue(String.valueOf(item.getVaf()));
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

        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(outFile);
        workbook.write(fileOut);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }
}
