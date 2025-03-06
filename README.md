# showNews 輿情系統前端顯示

## 簡介
是一個基於 Java Spring Boot 的 Web 應用，負責從雲端 CSV 檔案抓取數據，解析後顯示在前端。透過動態方式更新新聞或資訊的場景。

## 功能
- 透過 Spring Boot 後端從雲端 URL 下載 CSV 檔案
- 使用 OpenCSV 解析 CSV 內容
- 提供 REST API 給前端存取數據
- 在前端呈現資訊
- 支援前端頁籤過濾分類

## 技術細節
- **後端**: Java Spring Boot
- **數據處理**: Apache Commons CSV
- **前端**: HTML5、JavaScript（可擴展）
- **資料存取**: 可選擇使用 MySQL、PostgreSQL 或直接處理 CSV

### 環境準備
確保已安裝以下環境：
- Java 17 或以上
- Maven

