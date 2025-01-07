// 格式化時間函數
function formatDateTime(dateTimeString) {
  const [date] = dateTimeString.split(" "); // 分離日期和時間
  const [year, month, day] = date.split("-"); // 分割年、月、日
  return `${year}年${month}月${day}日`; // 格式化為所需格式
}

// 顯示/隱藏 Loading 提示
function toggleLoading(show) {
  const loadingElement = document.getElementById("loading");
  loadingElement.style.display = show ? "flex" : "none";
}

// 渲染數據至頁面
function renderData(data) {
  const cardContainer = document.getElementById("card-container");
  cardContainer.innerHTML = ""; // 清空內容

  data.forEach((item) => {
    const card = document.createElement("div");
    card.className = "card";

    // 縮圖
    const img = document.createElement("img");
    img.src = "assets/images/default-picture.png";
    img.alt = "縮圖";
    img.className = "card-img";

    // 卡片內容
    const content = document.createElement("div");
    content.className = "card-content";

    // 類別與時間
    const header = document.createElement("div");
    header.className = "header";

    const category = document.createElement("div");
    category.className = "category";
    category.textContent =
      `＜${item["關鍵字分類"] || ""}＞ ${item["新聞分類"] || ""}`.trim() ||
      "未分類";

    const time = document.createElement("div");
    time.className = "time";
    time.textContent = item["內容擷取時間"]
      ? formatDateTime(item["內容擷取時間"])
      : "日期未知";

    header.appendChild(category);
    header.appendChild(time);

    // 標題
    const link = document.createElement("a");
    link.href = item["網址"] || "#";
    link.target = "_blank";
    link.className = "title";
    link.textContent = item["標題"]?.trim() || "無標題";

    // 摘要
    const summary = document.createElement("div");
    summary.className = "news-details";
    summary.textContent = item["新聞片段"] || "無新聞片段";

    // 展開/收合按鈕（點點點樣式）
    const toggleButton = document.createElement("button");
    toggleButton.className = "toggle-button";
    toggleButton.setAttribute("aria-label", "新聞片段"); // 提供可訪問性標籤
    toggleButton.innerHTML = "⋮"; // 使用三個點點點符號

    toggleButton.addEventListener("click", () => {
      if (summary.classList.contains("open")) {
        summary.style.maxHeight = "0"; // 收合
        summary.classList.remove("open");
      } else {
        summary.style.maxHeight = `${summary.scrollHeight}px`; // 动态设置高度
        summary.classList.add("open");
      }
    });

    // // 展開/收合按鈕
    // const toggleButton = document.createElement("button");
    // toggleButton.className = "toggle-button";
    // toggleButton.textContent = "新聞片段▼"; // 預設為收合狀態

    // toggleButton.addEventListener("click", () => {
    //   if (summary.classList.contains("open")) {
    //     summary.style.maxHeight = "0"; // 收合
    //     summary.classList.remove("open");
    //     toggleButton.textContent = "新聞片段▼";
    //   } else {
    //     summary.style.maxHeight = `${summary.scrollHeight}px`; // 动态设置高度
    //     summary.classList.add("open");
    //     toggleButton.textContent = "新聞片段▲";
    //   }
    // });

    // 組裝卡片
    const cardHeader = document.createElement("div");
    cardHeader.className = "card-header";
    cardHeader.appendChild(img);
    cardHeader.appendChild(content);
    cardHeader.appendChild(toggleButton);

    content.appendChild(header);
    content.appendChild(link);

    card.appendChild(cardHeader);
    card.appendChild(summary);
    cardContainer.appendChild(card);
  });
}

// 請求後端 API 並初始化按鈕點擊邏輯
fetch("/api/csv2")
  .then((response) => {
    toggleLoading(true); // 顯示 Loading 提示
    return response.json();
  })
  .then((data) => {
    toggleLoading(false); // 請求成功，隱藏 Loading 提示

    // 移除可能存在的 BOM
    const sanitizedData = data.map((item) => {
      const sanitizedItem = {};
      for (let key in item) {
        const sanitizedKey = key.startsWith("\uFEFF") ? key.slice(1) : key; // 去掉 BOM
        sanitizedItem[sanitizedKey] = item[key];
      }
      return sanitizedItem;
    });

    // 初始顯示所有數據
    renderData(sanitizedData);

    // 綁定按鈕事件
    const buttons = document.querySelectorAll(".button-container button");
    buttons.forEach((button) => {
      button.addEventListener("click", () => {
        const category = button.dataset.category; // 獲取按鈕的分類
        // console.log(category);

        if (category) {
          const filteredData = sanitizedData.filter((item) =>
            item["關鍵字分類"]?.includes(category)
          );
          renderData(filteredData); // 根據分類篩選數據並重新渲染
        } else {
          renderData(sanitizedData); // 如果分類不存在，顯示所有數據
        }
      });
    });
  })
  .catch((error) => {
    toggleLoading(false); // 隱藏 Loading 提示
    console.error("Error fetching data:", error);
  });
