package com.leo.showNews;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestCsv2Xls {
    public static void main(String[] args) {
        // 輸入 CSV 文件路徑
        String csvFilePath = "/Users/yaolisheng/Desktop/Csv2Xls/24潮.csv";
        // 輸出的 XLSX 文件路徑
        String xlsxFilePath = "/Users/yaolisheng/Desktop/Csv2Xls/24潮.xlsx";

        try (Reader reader = new InputStreamReader(new FileInputStream(csvFilePath), StandardCharsets.UTF_8);
             Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(xlsxFilePath)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            Sheet sheet = workbook.createSheet("Sheet1");
            int rowIndex = 0;

            // 寫入標題
            Row headerRow = sheet.createRow(rowIndex++);
            int colIndex = 0;
            for (String header : records.iterator().next().toMap().keySet()) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(header);
            }

            // 寫入數據
            for (CSVRecord record : records) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String value : record) {
                    Cell cell = row.createCell(colIndex++);
                    cell.setCellValue(value);
                }
            }

            // 儲存文件
            workbook.write(fileOut);
            System.out.println("轉換完成！XLSX 文件已儲存到：" + xlsxFilePath);

        } catch (IOException e) {
            System.err.println("發生錯誤：" + e.getMessage());
        }
    }
}

