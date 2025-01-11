# Etapa 1: Construção do aplicativo com Maven
FROM openjdk:21-slim AS build

# Instala Maven
RUN apt-get update && apt-get install -y maven

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos necessários para o container
COPY pom.xml /app/
COPY rest /app/rest
COPY common /app/common
COPY calculator /app/calculator

# Executa o Maven para compilar o projeto e gerar o JAR
RUN mvn clean package -DskipTests

# Etapa 2: Criando a imagem final para executar a aplicação
FROM openjdk:21-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado pela fase de construção
COPY --from=build /app/rest/target/rest-0.0.1-SNAPSHOT.jar /app/rest-0.0.1-SNAPSHOT.jar

# Expõe a porta necessária
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "rest-0.0.1-SNAPSHOT.jar"]
