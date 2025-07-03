# 1) build
FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package \
      -pl data-analysis-service \
      -am \
      -DskipTests

# 2) run
FROM openjdk:17-slim
WORKDIR /app

COPY --from=build \
     /app/data-analysis-service/target/data-analysis-service-*.jar app.jar

EXPOSE 8001
ENTRYPOINT ["java","-jar","app.jar"]
