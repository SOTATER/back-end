FROM gradle:7.4.2-jdk8 AS builder
WORKDIR /build
ADD build.gradle.kts settings.gradle.kts /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

COPY . /build
RUN gradle clean build -x test --parallel

FROM openjdk:8-jdk-alpine
WORKDIR /app
ARG JAR_FILE=/build/build/libs/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]