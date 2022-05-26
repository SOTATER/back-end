FROM openjdk:8-jdk-alpine as builder

WORKDIR /workspace

COPY . .
RUN ./gradlew bootJar

FROM openjdk:8-jdk-alpine
WORKDIR /
ARG JAR_FILE=/workspace/build/libs/*.jar
COPY --from=builder ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]