FROM java:8
EXPOSE 8080
ARG JAR_FILE=build/libs/copy-opgg-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} copygg.jar
ENTRYPOINT ["java", "-jar", "/copygg.jar"]