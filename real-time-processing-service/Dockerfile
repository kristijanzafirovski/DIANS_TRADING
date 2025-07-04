# ─────────────────────────────────────────────
# Stage 1: Build a shaded (uber) JAR
# ─────────────────────────────────────────────
FROM maven:3-openjdk-17 AS builder
WORKDIR /app

# Copy whole project
COPY . .

# Build only the real-time processing module (skip tests)
RUN mvn clean package \
    -pl real-time-processing-service \
    -am \
    -DskipTests

# ─────────────────────────────────────────────
# Stage 2: Run with Spark
# ─────────────────────────────────────────────
FROM bitnami/spark:3.4.1

USER root
# Ensure Ivy has an absolute cache dir and disable Kerberos login
ENV HOME=/opt/app
ENV HADOOP_USER_NAME=root
ENV SPARK_CONF_DIR=/opt/app/conf

WORKDIR /opt/app

RUN rm -f /opt/bitnami/spark/jars/spark-sql-kafka-0-10_*.jar /opt/bitnami/spark/jars/spark-token-provider-kafka-0-10_*.jar

# Copy the shaded jar from the builder stage
COPY --from=builder \
     /app/real-time-processing-service/target/*-shaded.jar \
     ./app.jar

# Ensure spark-submit is on PATH
ENV PATH="/opt/bitnami/spark/bin:${PATH}"
# in your Dockerfile, before ENTRYPOINT:
RUN rm -f /opt/bitnami/spark/jars/hadoop-viewfs-*.jar \
          /opt/bitnami/spark/jars/hadoop-client-*viewfs*.jar

ENTRYPOINT ["spark-submit", \
  "--packages",  "org.apache.spark:spark-sql-kafka-0-10_2.12:3.4.1", \
  "--conf",      "spark.hadoop.hadoop.security.authentication=simple", \
  "--conf",      "spark.hadoop.security.authentication=simple", \
  "--conf",      "spark.hadoop.fs.defaultFS=file:///", \
  "--conf",      "spark.hadoop.fs.viewfs.impl=org.apache.hadoop.fs.LocalFileSystem", \
  "--conf",      "spark.sql.catalogImplementation=in-memory", \
  "--conf",      "spark.sql.warehouse.dir=/tmp", \
  "--conf",      "spark.jars.ivy=/opt/app/.ivy2", \
  "--class",     "com.example.processing.SparkProcessingApp", \
  "--master",    "local[*]", \
  "--deploy-mode","client", \
  "/opt/app/app.jar"]
