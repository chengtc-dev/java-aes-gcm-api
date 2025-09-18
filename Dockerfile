# 選擇OpenJDK官方映像作為基底
FROM openjdk:17

# 設定工作目錄
WORKDIR /app

# 複製本機JAR檔到容器內
COPY java-aes-gcm-api.jar /app/app.jar

# 暴露應用程式的埠(依實際情況設定)
EXPOSE 8080

# 啟動指令
CMD ["java", "-jar", "/app/app.jar"]
