package com.leo.showNews.sheetReader.controller;

import com.leo.showNews.sheetReader.service.SheetReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class SheetReaderController {

    private final SheetReaderService sheetReaderService;

    @Autowired
    public SheetReaderController(SheetReaderService sheetReaderService) {
        this.sheetReaderService  = sheetReaderService;
    }

    @GetMapping("/api/excel")
    public List<Map<String, String>> getData(){
        String filePath = "/Users/yaolisheng/Desktop/showNews/data/old/新能源.xlsx";
        try{
            return sheetReaderService.readExcel(filePath);
        }catch (IOException e){
            throw new RuntimeException("Error file" + e.getMessage());
        }
    }

    @GetMapping("/api/csv")
    public ResponseEntity<List<Map<String, String>>> getCsvData() {
        String folderPath = "/Users/yaolisheng/SynologyDrive/Backup/CHINA-MOTOR/C/Dennis/Py/9_FormosaPublicOpinionSystemX/05_News_Recommender";

        try {
            List<Map<String, String>> data = sheetReaderService.readCsv(folderPath);
            return ResponseEntity.ok(data); // 直接返回 List<Map<String, String>>
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(Map.of("error", "無法讀取 CSV 文件: " + e.getMessage())));
        }
    }



}
