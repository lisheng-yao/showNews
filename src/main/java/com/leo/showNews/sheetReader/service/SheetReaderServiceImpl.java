package com.leo.showNews.sheetReader.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class SheetReaderServiceImpl implements SheetReaderService {

    @Override
    public List<Map<String, String>> readExcel(String filePath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("表頭行為空，請檢查 Excel 文件！");
            }

            int numColumns = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();

            // 讀取表頭
            for (int j = 0; j < numColumns; j++) {
                Cell cell = headerRow.getCell(j);
                String header = cell != null ? cell.getStringCellValue().trim() : "Column" + j;
                if (header.startsWith("\ufeff")) {
                    header = header.substring(1); // 去除 BOM
                }
                headers.add(header);
            }

            // 遍歷數據行，過濾空行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue; // 跳過 null 行
                }

                Map<String, String> rowData = new LinkedHashMap<>();
                boolean hasValidData = false; // 標記是否有有效數據

                for (int j = 0; j < numColumns; j++) {
                    Cell cell = row.getCell(j);
                    String value = cell != null ? cell.toString().trim() : "";
                    if (!value.isEmpty()) {
                        hasValidData = true; // 發現有效數據
                    }
                    rowData.put(headers.get(j), value);
                }

                if (hasValidData) { // 只添加有數據的行
                    data.add(rowData);
                }
            }
        }

        return data;
    }

    @Override
    public List<Map<String, String>> readCsv(String folderPath) throws IOException {
        // 1. CSV 轉 XLSX
        String outputFolderPath = "/Users/yaolisheng/Desktop/showNews/data";
        convertCsvToXlsx(folderPath, outputFolderPath);

        // 2. 遍歷 XLSX，篩選並排序
        return processXlsxFiles(outputFolderPath);
    }

    // 方法1: 將所有 CSV 文件轉換為 XLSX 文件
    public void convertCsvToXlsx(String inputFolderPath, String outputFolderPath) throws IOException {
        File inputFolder = new File(inputFolderPath);
        File outputFolder = new File(outputFolderPath);

        // 檢查輸入資料夾是否有效
        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            throw new IOException("指定的路徑不是有效的資料夾！");
        }

        // 如果輸出資料夾不存在，則建立
        if (!outputFolder.exists() && !outputFolder.mkdirs()) {
            throw new IOException("無法建立輸出資料夾！");
        }

        // 遍歷資料夾內的所有 CSV 文件
        File[] csvFiles = inputFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            System.out.println("沒有找到任何 CSV 文件！");
            return;
        }

        for (File csvFile : csvFiles) {
            String outputFileName = csvFile.getName().replace(".csv", ".xlsx");
            File outputFile = new File(outputFolder, outputFileName);

            try (Reader reader = new FileReader(csvFile);
                 Workbook workbook = new XSSFWorkbook()) {

                Sheet sheet = workbook.createSheet("Sheet1");
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

                // 表頭處理
                Map<String, Integer> headerMap = parser.getHeaderMap();
                Row headerRow = sheet.createRow(0);
                int colIndex = 0;
                for (String header : headerMap.keySet()) {
                    Cell cell = headerRow.createCell(colIndex++);
                    cell.setCellValue(header);
                }

                // 數據行處理
                int rowIndex = 1;
                for (CSVRecord record : parser) {
                    Row row = sheet.createRow(rowIndex++);
                    colIndex = 0;
                    for (String value : record) {
                        Cell cell = row.createCell(colIndex++);
                        cell.setCellValue(value);
                    }
                }

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    workbook.write(fos);
                }

                System.out.println("轉換成功: " + csvFile.getName() + " -> " + outputFileName);
            } catch (Exception e) {
                System.out.println("轉換失敗: " + csvFile.getName() + "，錯誤：" + e.getMessage());
            }
        }
    }

    // 方法2: 遍歷 XLSX 文件，篩選並排序
    public List<Map<String, String>> processXlsxFiles(String xlsxFolderPath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        File xlsxFolder = new File(xlsxFolderPath);

        // 確認目錄是否有效
        if (!xlsxFolder.exists() || !xlsxFolder.isDirectory()) {
            throw new IOException("指定的路徑不是有效的資料夾！");
        }

        // 獲取所有 .xlsx 文件
        File[] xlsxFiles = xlsxFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        if (xlsxFiles == null || xlsxFiles.length == 0) {
            System.out.println("沒有找到任何 XLSX 文件！");
            return data;
        }

        // 遍歷每個 .xlsx 文件
        for (File xlsxFile : xlsxFiles) {
            try (Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
                Sheet sheet = workbook.getSheetAt(0); // 預設讀取第一個工作表
                if (sheet == null) continue;

                // 讀取表頭
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) continue;

                Map<Integer, String> headers = new HashMap<>();
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    headers.put(i, cell != null ? cell.getStringCellValue().trim() : "");
                }

                // 讀取數據行
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Map<String, String> rowData = new LinkedHashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String value = cell != null ? cell.toString().trim() : "";
                        rowData.put(headers.get(j), value);
                    }

                    // 篩選條件：新聞評分 >= 60
                    String scoreStr = rowData.getOrDefault("新聞評分", "0");
                    try {
                        int score = Integer.parseInt(scoreStr);
                        if (score >= 60) {
                            data.add(rowData);
                        }
                    } catch (NumberFormatException e) {
                        // 略過無效的評分
                    }
                }
            } catch (Exception e) {
                System.out.println("處理 XLSX 文件失敗: " + xlsxFile.getName() + "，錯誤：" + e.getMessage());
            }
        }

        // 根據內容擷取時間排序（從近到遠）
        data.sort((map1, map2) -> {
            String time1 = map1.getOrDefault("內容擷取時間", "");
            String time2 = map2.getOrDefault("內容擷取時間", "");
            if (time1.isEmpty() && time2.isEmpty()) return 0;
            if (time1.isEmpty()) return 1;
            if (time2.isEmpty()) return -1;
            return time2.compareTo(time1); // 降序排序（從近到遠）
        });

        return data;
    }




}

