# Build stage
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Проверяем, что JAR файл создан
RUN ls -la /app/target/

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Установка необходимых инструментов
RUN apk add --no-cache curl

# Копируем JAR файл
COPY --from=builder /app/target/*.jar app.jar

# Создаем не-root пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]