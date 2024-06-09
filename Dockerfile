FROM csanchez/maven:4.0-sapmachine-22 as build

WORKDIR /usr/local
COPY src ./src
COPY pom.xml ./pom.xml
RUN mvn clean install

FROM openjdk:22-slim-bullseye

COPY assets ./assets
COPY db ./db
COPY --from=build /usr/local/target/BunnyEmu-jar-with-dependencies.jar /app/BunnyEmu.jar
ENTRYPOINT ["java", "-jar", "/app/BunnyEmu.jar"]