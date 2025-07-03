FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package \
    -pl real-time-processing-service \
    -am \
    -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build \
    /app/real-time-processing-service/target/real-time-processing-service-*.jar \
    ./app.jar

ENTRYPOINT ["java","-jar","app.jar"]
