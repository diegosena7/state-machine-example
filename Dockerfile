#FROM maven:3.9.4-eclipse-temurin-17 AS builder
#
#WORKDIR /app
#
#COPY pom.xml .
#COPY src ./src
#
#RUN mvn clean package -DskipTests
#
#FROM eclipse-temurin:17-jdk-alpine
#
#WORKDIR /app
#
#COPY --from=builder /app/target/spring-consent-rabbitmq-statemachine-1.0.0.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
