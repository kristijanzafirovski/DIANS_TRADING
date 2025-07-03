# data-ingestion-service/Dockerfile
FROM maven:3-openjdk-17 AS build
WORKDIR /app

# COPY the *whole* project, including all sub-modules
COPY . .

# Now build only the ingestion service and anything it depends on
RUN mvn clean package \
      -pl data-ingestion-service \
      -am \
      -DskipTests

FROM openjdk:17-slim
WORKDIR /app

# grab just the fat-jar for ingestion
COPY --from=build \
     /app/data-ingestion-service/target/data-ingestion-service-*.jar app.jar

EXPOSE 3000
ENTRYPOINT ["java","-jar","app.jar"]
