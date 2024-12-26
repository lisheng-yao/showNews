package com.leo.showNews.sheetReader.service;

import java.io.IOException;
import java.util.Map;
import java.util.List;


public interface SheetReaderService {

    List<Map<String, String>> readExcel(String filePath) throws IOException;

//    List<Map<String, String>> readCsv(String filePath) throws IOException;
}
