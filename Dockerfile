# ===== Build stage =====
FROM sbtscala/scala-sbt:eclipse-temurin-21.0.7_6_1.11.2_3.3.6 AS builder

WORKDIR /app

COPY . .

RUN sbt assembly

# ====== Run stage ======
FROM eclipse-temurin:21-jre

WORKDIR /app

# 上で生成したfat jarをコピー
COPY --from=builder /app/target/scala-*/function-festival-chat.jar ./app.jar

# （Optional）PORT環境変数で起動する前提なので exposeは任意
# EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
