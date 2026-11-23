# DIANS Trading

> This project was developed as a **FINKI course project** (Faculty of Computer Science and Engineering, Ss. Cyril and Methodius University in Skopje).

An event-driven trading analytics platform built as a set of microservices. Market trade data is ingested from the [Twelve Data](https://twelvedata.com/) API, streamed through Apache Kafka, processed in real time with Apache Spark, stored in Apache Cassandra and PostgreSQL, analysed for moving-average crossover signals, and visualised in a web dashboard.

## Architecture

| Service | Technology | Responsibility | Port (host) |
|---|---|---|---|
| `data-ingestion-service` | Spring Boot, Kafka producer | Fetches market data and publishes raw trades to the `trades` topic | 3001 |
| `real-time-processing-service` | Apache Spark (Structured Streaming) | Reads `trades`, parses and aggregates records, publishes results to `processed-data` | 4040 (Spark UI) |
| `data-storage-service` | Spring Boot, Cassandra / PostgreSQL | Persists trades and exposes them over a REST API | 8000 |
| `data-analysis-service` | Spring Boot, Kafka consumer | Consumes `processed-data`, computes SMA crossover signals (BUY / SELL) and exposes them | 8001 |
| `data-visualization-service` | React, Vite, Plotly | Dashboard for trade data and generated signals | 8080 |

Infrastructure (Zookeeper, Kafka, Cassandra, PostgreSQL) is defined in `docker-compose.yml`.

## Signal generation

The analysis service applies a **Simple Moving Average (SMA) crossover** strategy:

- A short-window SMA is compared against a long-window SMA over each bar's average price (open, high, low, close).
- A **BUY** signal is emitted when the short SMA crosses above the long SMA.
- A **SELL** signal is emitted when the short SMA crosses below the long SMA.

The signals are informational and are not financial advice.

## Getting started

### Prerequisites

- Docker and Docker Compose
- A [Twelve Data](https://twelvedata.com/) API key

### Run the full stack

```bash
docker compose up --build
```

Once the containers are healthy:

- Dashboard: http://localhost:8080
- Storage API: http://localhost:8000
- Analysis API: http://localhost:8001

Keep API keys and database credentials out of version control. Supply them through environment variables or a local `.env` file, which should be git-ignored.

## Project structure

```
.
├── docker-compose.yml
├── pom.xml                       # Maven parent for the Java services
├── data-ingestion-service/
├── real-time-processing-service/
├── data-storage-service/
├── data-analysis-service/
└── data-visualization-service/   # React frontend
```

## Tech stack

Java 17 · Spring Boot · Apache Kafka · Apache Spark · Apache Cassandra · PostgreSQL · React · Plotly · Docker

## Author

**Kristijan Zafirovski**
