FROM eclipse-temurin:21-jdk-jammy AS build

LABEL authors="ognjenburmaz"

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/FishingBuddy-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]