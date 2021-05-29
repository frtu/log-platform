# https://spring.io/guides/gs/spring-boot-docker/
FROM openjdk:8-jdk-alpine

WORKDIR .
VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
