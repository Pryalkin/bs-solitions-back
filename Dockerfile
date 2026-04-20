## Render Dockerfile (Spring Boot + Gradle, multi-stage)
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Better caching: wrapper + build scripts first
COPY gradlew gradlew.bat settings.gradle build.gradle /app/
COPY gradle /app/gradle

# Sources
COPY src /app/src

RUN chmod +x /app/gradlew
RUN /app/gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app
ENV PORT=8080

# build.gradle sets bootJar archiveFileName = app.jar
COPY --from=build /app/build/libs/app.jar /app/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]

## Multi-stage build for Render (Spring Boot + Gradle)
## Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and build scripts first (better layer caching)
COPY gradlew gradlew.bat settings.gradle build.gradle /app/
COPY gradle /app/gradle

# Copy sources
COPY src /app/src

# Ensure wrapper is executable on Linux builders
RUN chmod +x /app/gradlew

# Build runnable Spring Boot jar (skip tests for deploy speed/stability)
RUN /app/gradlew clean bootJar -x test

## Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Render provides PORT env var; app also defaults to 8080
ENV PORT=8080

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]

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