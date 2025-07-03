FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package \
      -pl data-storage-service \
      -am \
      -DskipTests

FROM openjdk:17-slim
WORKDIR /app

COPY --from=build \
     /app/data-storage-service/target/data-storage-service-*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
