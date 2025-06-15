FROM eclipse-temurin:21-jre

# 作業ディレクトリ
WORKDIR /app

# fat JAR をコピー
COPY target/scala-3.3.1/function-festival-chat.jar ./app.jar

# Railway が渡すポート番号を開放
EXPOSE 8080

# アプリ起動（mainClass は JAR に埋め込まれている前提）
ENTRYPOINT ["java", "-jar", "app.jar"]
