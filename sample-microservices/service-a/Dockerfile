# https://spring.io/guides/gs/spring-boot-docker/
FROM openjdk:8-jdk-alpine

WORKDIR .
VOLUME /tmp

ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
