FROM maven:3.9.4-eclipse-temurin-21 AS build

LABEL authors="ognjenburmaz"

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN useradd -ms /bin/bash appuser

COPY --from=build /app/target/FishingBuddy-1.0.0.jar app.jar

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
