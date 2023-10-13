FROM openjdk:17-jdk-slim
LABEL authors="Vitalii Seniuk"

ARG JAR_FILE=target/*.jar
ARG EUREKA_URL
ARG AUTH_VALIDATE_TOKEN_PATH

ENV EUREKA_URL=$EUREKA_URL \
    AUTH_VALIDATE_TOKEN_PATH=$AUTH_VALIDATE_TOKEN_PATH

COPY ${JAR_FILE} api-gateway.jar

ENTRYPOINT ["java", "-jar", "/api-gateway.jar"]