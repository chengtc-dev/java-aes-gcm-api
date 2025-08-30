# java-aes-gcm-api

一個基於 Spring Boot 的簡易 AES-GCM 加解密 REST API，使用 AES/GCM/NoPadding 模式，對輸入的文字資料進行加密與解密操作。

## 功能特色

- 使用 AES-GCM（Galois/Counter Mode）演算法，提供安全的加密。
- 支援 Base64 格式的 AES 密鑰（16、24、32 bytes 長度）。
- 加密結果先 Base64 編碼，再進行 Hex 編碼，方便傳輸與儲存。
- 提供 REST API 端點，一鍵加密與解密。
- 支援指定字元編碼（如 UTF-8、Big5 等）。

## API 端點

### POST /encrypt

加密明文。

- 請求格式（JSON）:

```

{
"plainText": "待加密文字",
"key": "Base64編碼的AES密鑰",
"charset": "字元編碼（選填，預設 UTF-8）"
}

```

- 回應: 加密後的 Hex 編碼字串。

### POST /decrypt

解密 ciphertext。

- 請求格式（JSON）:

```

{
"cipherText": "待解密的 Hex 編碼字串",
"key": "Base64編碼的AES密鑰",
"charset": "字元編碼（選填，預設 UTF-8）"
}

```

- 回應: 解密後的原始文字。

## 使用說明

1. 準備一個 Base64 編碼的 AES 密鑰，長度必須是 16、24 或 32 bytes。
2. 透過工具（如 Postman）呼叫加解密 API。
3. 傳入欲加密或解密的文字與密鑰，取得結果。

## 範例

### 加密範例請求

```

curl -X POST http://localhost:8080/encrypt \
-H "Content-Type: application/json" \
-d '{
"plainText": "Hello World",
"key": "bWluZHNlY3JldGtleTEyMw==",
"charset": "UTF-8"
}'

```

### 解密範例請求

```

curl -X POST http://localhost:8080/decrypt \
-H "Content-Type: application/json" \
-d '{
"cipherText": "Hex編碼的加密結果",
"key": "bWluZHNlY3JldGtleTEyMw==",
"charset": "UTF-8"
}'

```

## Postman Pre-request 範例

以下示範如何在 Postman 的 Pre-request Script 中，分別呼叫加密與解密 API。

### 加密 API 呼叫範例

```

pm.sendRequest({
    url: 'http://35.212.146.250:8080/encrypt',
    method: 'POST',
    header: {
        'Content-Type': 'application/json'
    },
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            "plainText": "Hello, this is a secret message.",
            "key": "XpFD0q8l1xFrDw15g/XKt/TR2m0FQP3TbbWzfcXRkcY=",
            "charset": "UTF-8"
        })
    }
}, function(err, res) {
    if (err) {
        // 處理錯誤，例如網路連線失敗
        console.error("請求發生錯誤：", err);
    } else {
        // 檢查 HTTP 狀態碼是否為 200
        if (res.code === 200) {
            try {
                // 嘗試將回傳解析為 JSON
                const responseData = res.json();
                console.log("成功解析的回應資料：", responseData);
            } catch (jsonErr) {
                // 如果解析 JSON 失敗，印出原始文字內容
                console.error("無法將回應解析為 JSON。原始文字內容：", res.text());
            }
        } else {
            // 如果狀態碼不是 200，印出錯誤訊息和狀態碼
            console.error(`請求失敗，狀態碼：${res.code}。回應內容：${res.text()}`);
        }
    }
});

```

### 解密 API 呼叫範例

```

pm.sendRequest({
    url: 'http://35.212.146.250:8080/decrypt',
    method: 'POST',
    header: {
        'Content-Type': 'application/json'
    },
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            "cipherText": "326435424437725234593169567a4e6575473254674e4c414a4c4c5a7a3065516f476e642b56416c54756e2b75742b456141586241526f72546638674c457563734d73674435493477384577705a476b",
            "key": "XpFD0q8l1xFrDw15g/XKt/TR2m0FQP3TbbWzfcXRkcY=",
            "charset": "UTF-8"
        })
    }
}, function(err, res) {
    if (err) {
        // 處理錯誤，例如網路連線失敗
        console.error("請求發生錯誤：", err);
    } else {
        // 檢查 HTTP 狀態碼是否為 200
        if (res.code === 200) {
            try {
                // 嘗試將回傳解析為 JSON
                const responseData = res.json();
                console.log("成功解析的回應資料：", responseData);
            } catch (jsonErr) {
                // 如果解析 JSON 失敗，印出原始文字內容
                console.error("無法將回應解析為 JSON。原始文字內容：", res.text());
            }
        } else {
            // 如果狀態碼不是 200，印出錯誤訊息和狀態碼
            console.error(`請求失敗，狀態碼：${res.code}。回應內容：${res.text()}`);
        }
    }
});

```

## 注意事項

- AES 密鑰必須是有效且符合長度要求的 Base64 字串。
- 請使用相同的密鑰和字元編碼做加密與解密，否則會失敗。
- 若部署於其他主機或端口，請調整 API URL。
- API 基於 Spring Boot，需先安裝 JDK 和 Maven 進行建置與啟動。

## 建置與執行

```

mvn clean install
java -jar target/java-aes-gcm-api-0.0.1-SNAPSHOT.jar

```

---

此專案用於示範與學習，請注意產品環境下的安全性與密鑰管理。