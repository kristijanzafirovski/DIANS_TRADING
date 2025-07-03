package com.example.processing;

import com.example.processing.model.Trade;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

import static org.apache.spark.sql.functions.*;

public class SparkProcessingApp {
    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("RealTimeProcessing")
                .master("local[*]")
                .getOrCreate();

        // 1) Read raw JSON from Kafka
        Dataset<String> json = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "raw-data")
                .load()
                .selectExpr("CAST(value AS STRING)")
                .as(Encoders.STRING());

        // 2) Parse JSON into Trade objects
        Dataset<Trade> trades = spark.read()
                .json(json)
                .as(Encoders.bean(Trade.class));

        // 3) Example filter: only “AAPL”
        Dataset<Trade> filtered = trades.filter(col("symbol").equalTo("AAPL"));

        // 4) Transform: add an adjustedPrice column (just as example)
        Dataset<?> transformed = filtered
                .withColumn("adjustedPrice", col("price").multiply(1.01));

        // 5) Aggregate: 1-min tumbling window
        Dataset<?> aggregated = transformed
                .withWatermark("timestamp", "2 minutes")
                .groupBy(
                        window(col("timestamp"), "1 minute"),
                        col("symbol")
                )
                .agg(
                        avg("price").alias("avgPrice"),
                        max("price").alias("maxPrice"),
                        min("price").alias("minPrice")
                );

        // 6) Write results back to Kafka “analysis-data”
        StreamingQuery query = aggregated
                .selectExpr("to_json(struct(*)) AS value")
                .writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("topic", "analysis-data")
                .option("checkpointLocation", "/tmp/spark-checkpoint")
                .start();

        query.awaitTermination();
    }
}
