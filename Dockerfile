# ====== Build stage ======
FROM hseeberger/scala-sbt:11.0.17_1.8.0_1.9.6 AS builder

WORKDIR /app

COPY . .

RUN sbt assembly

# ====== Run stage ======
FROM eclipse-temurin:21-jre

WORKDIR /app

# 上で生成したfat jarをコピー
COPY --from=builder /app/target/scala-3.3.1/function-festival-chat.jar ./app.jar

# （Optional）PORT環境変数で起動する前提なので exposeは任意
# EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
