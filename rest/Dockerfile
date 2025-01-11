FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml /app/
COPY rest /app/rest
COPY common /app/common
COPY calculator /app/calculator

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /app

COPY --from=build /app/rest/target/rest-0.0.1-SNAPSHOT.jar /app/rest-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "rest-0.0.1-SNAPSHOT.jar"]
