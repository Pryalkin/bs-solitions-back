# Этап сборки
FROM eclipse-temurin:17-jdk-jammy AS build
COPY . .
RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]