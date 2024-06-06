FROM eclipse-temurin:22-jdk-jammy AS build

COPY assets /usr/local/assets
COPY db /usr/local/db
COPY src /usr/local/src
COPY pom.xml /usr/local/pom.xml

FROM eclipse-temurin:22-jre-jammy
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/runner.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/runner.jar