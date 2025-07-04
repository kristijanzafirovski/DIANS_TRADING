package com.example.processing;

import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;

import static org.apache.spark.sql.functions.*;

public class SparkProcessingApp {
    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("RealTimeProcessing")
                .master("local[*]")
                .getOrCreate();

        // 1) Ingest raw JSON strings from Kafka
        Dataset<String> rawJson = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "trades")
                .load()
                .selectExpr("CAST(value AS STRING) as json_str")
                .as(Encoders.STRING());

        // 2) Parse into columns: symbol, timestamp, open,high,low,close,volume
        StructType schema = new StructType()
                .add("symbol", "string")
                .add("timestamp", "string")
                .add("data", new StructType()
                        .add("open", "double")
                        .add("high", "double")
                        .add("low", "double")
                        .add("close", "double")
                        .add("volume", "long")
                );

        Dataset<Row> parsed = rawJson
                .select(from_json(col("json_str"), schema).as("m"))
                .selectExpr(
                        "m.symbol as symbol",
                        "m.timestamp as ts_str",
                        "m.data.open as open",
                        "m.data.high as high",
                        "m.data.low as low",
                        "m.data.close as close",
                        "m.data.volume as volume"
                );

        // 3) Convert timestamp to true event-time and watermark
        Dataset<Row> withEventTime = parsed
                .withColumn("eventTime",
                        to_timestamp(col("ts_str"), "yyyy-MM-dd'T'HH:mm"))
                .withWatermark("eventTime", "2 minutes");

        // 4) 1-min tumbling window aggregation into OHLC + sum(vol)
        Dataset<Row> agg = withEventTime
                .groupBy(
                        window(col("eventTime"), "1 minute").alias("w"),
                        col("symbol")
                )
                .agg(
                        first("open", true).alias("open"),
                        max("high").alias("high"),
                        min("low").alias("low"),
                        last("close", true).alias("close"),
                        sum("volume").alias("volume")
                );

        // 5) Flatten out into the desired JSON shape
        Dataset<Row> out = agg.selectExpr(
                "symbol",
                "date_format(w.start, \"yyyy-MM-dd'T'HH:mm\") as timestamp",
                "struct(open, high, low, close, volume) as data"
        );

        // 6) Emit to Kafka
        StreamingQuery query = out
                .selectExpr("to_json(struct(symbol, timestamp, data)) AS value")
                .writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("topic", "processed-data")
                .option("checkpointLocation", "/tmp/spark-checkpoint")
                .start();

        query.awaitTermination();
    }
}
