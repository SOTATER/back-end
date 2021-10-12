FROM gradle:jdk8 AS builder

WORKDIR /src

ADD . .

RUN gradle clean bootJar

FROM openjdk:8-jdk-alpine

WORKDIR /root/

COPY --from=builder /src/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/root/app.jar"]