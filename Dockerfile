# Multi-stage build для Spring Boot приложения
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Копируем pom и исходники и собираем приложение
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B

# Финальный образ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN adduser -D -g '' appuser
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
