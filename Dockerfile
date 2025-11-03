# Stage 1: Сборка приложения
# Используем образ Maven с Java 21 для сборки
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Копируем файл сборки Maven
COPY pom.xml ./
# Копируем исходный код Java
COPY src ./src/

# Запускаем сборку Maven
RUN mvn clean package -DskipTests

# Stage 2: Запуск приложения
# Используем легковесный образ Java 21 Runtime для запуска
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем готовый JAR-файл из первого этапа
COPY --from=builder /app/target/ComfortSoftTask-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]