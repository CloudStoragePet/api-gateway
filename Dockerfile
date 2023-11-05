FROM openjdk:17-jdk-slim
LABEL authors="Vitalii Seniuk"

ARG JAR_FILE=target/*.jar
ARG EUREKA_URL
ARG UI_ORIGIN
ARG AUTH_VALIDATE_TOKEN_PATH
ARG MANAGEMENT_ZIPKIN_TRACING_ENDPOINT

ENV EUREKA_URL=$EUREKA_URL \
    AUTH_VALIDATE_TOKEN_PATH=$AUTH_VALIDATE_TOKEN_PATH \
    MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=$MANAGEMENT_ZIPKIN_TRACING_ENDPOINT \
    UI_ORIGIN=$UI_ORIGIN

COPY ${JAR_FILE} api-gateway.jar

ENTRYPOINT ["java", "-jar", "/api-gateway.jar"]