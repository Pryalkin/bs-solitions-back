# Этап сборки (используем образ с предустановленным Gradle)
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Собираем проект (используем встроенный gradle из образа)
RUN gradle build --no-daemon -x test

# Этап запуска (легкий образ JRE)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Копируем jar-файл из этапа сборки
# Путь может отличаться (build/libs/*.jar), проверяем стандартный для Gradle
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]