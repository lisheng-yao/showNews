package com.leo.showNews.sheetReader.controller;

import com.leo.showNews.sheetReader.service.SheetReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
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
        String filePath = "/Users/yaolisheng/Desktop/showNews/data/新能源.xlsx";
        try{
            return sheetReaderService.readExcel(filePath);
        }catch (IOException e){
            throw new RuntimeException("Error file" + e.getMessage());
        }
    }


}
