# ── STAGE 1: BUILD ────────────────────────────────────────────────────
# Usa uma imagem com Maven + Java 17 para compilar o projeto
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml primeiro e baixa as dependências
# (isso fica em cache — se o pom.xml não mudar, não baixa tudo de novo)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# ── STAGE 2: RUNTIME ──────────────────────────────────────────────────
# Usa uma imagem menor, só com Java (sem Maven) para rodar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia só o .jar gerado no stage anterior
COPY --from=build /app/target/*.jar app.jar

# Porta que a API vai escutar
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
