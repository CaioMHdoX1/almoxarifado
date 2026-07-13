# ---- Etapa de build --------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
# Baixa dependências antes de copiar o código — acelera rebuilds (cache do Docker)
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests
 
# ---- Etapa de runtime -------------------------------------------------------
FROM tomcat:10.1-jdk17-temurin
# Remove apps de exemplo do Tomcat (docs, examples, manager) — não precisamos em produção
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/almoxaf-api.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080