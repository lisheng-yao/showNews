package com.leo.showNews.sheetReader.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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


}

