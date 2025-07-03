FROM maven:3-openjdk-17 AS builder
WORKDIR /app

COPY . .

RUN mvn clean package \
    -pl real-time-processing-service \
    -am \
    -DskipTests

FROM bitnami/spark:3.4.1

WORKDIR /opt/app

COPY --from=builder \
     /app/real-time-processing-service/target/real-time-processing-service-*.jar \
     ./app.jar

ENTRYPOINT ["spark-submit", \
    "--class", "com.example.processing.SparkProcessingApp", \
    "--master", "local[*]", \
    "--deploy-mode", "client", \
    "/opt/app/app.jar"]
