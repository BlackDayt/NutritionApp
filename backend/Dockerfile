# Используем JDK 21
FROM openjdk:21-jdk-slim
LABEL authors="proto"
# Рабочая директория внутри контейнера
WORKDIR /app
# Для Heaalthckeack
#RUN apt-get update && apt-get install -y curl

# Копируем собранный .jar файл
COPY target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
