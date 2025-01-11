FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml /app/
COPY calculator /app/calculator
COPY common /app/common
COPY rest /app/rest

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /app

COPY --from=build /app/calculator/target/calculator-0.0.1-SNAPSHOT.jar /app/calculator-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "calculator-0.0.1-SNAPSHOT.jar"]
