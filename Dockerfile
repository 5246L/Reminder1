# Этап 1: Сборка приложения
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем pom.xml и скачиваем зависимости (кеширование слоёв)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Копируем JAR из первого этапа
COPY --from=build /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8081

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
