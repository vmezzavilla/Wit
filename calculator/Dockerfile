# Etapa 1: Construção do aplicativo com Maven
FROM openjdk:21-slim AS build

# Instala Maven
RUN apt-get update && apt-get install -y maven

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos necessários para o container
COPY pom.xml /app/
COPY calculator /app/calculator
COPY common /app/common
COPY rest /app/rest

# Executa o Maven para compilar o projeto e gerar o JAR
RUN mvn clean package -DskipTests

# Etapa 2: Criando a imagem final para executar a aplicação
FROM openjdk:21-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado pela fase de construção
COPY --from=build /app/calculator/target/calculator-0.0.1-SNAPSHOT.jar /app/calculator-0.0.1-SNAPSHOT.jar

# Não expõe portas, pois não há necessidade de uma API HTTP no Calculator

# Comando para rodar a aplicação
CMD ["java", "-jar", "calculator-0.0.1-SNAPSHOT.jar"]
