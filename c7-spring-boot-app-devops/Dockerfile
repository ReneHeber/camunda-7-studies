# Running this dockerfile alone won't work because the app is configured to connect to a postgres db
FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ./target/c7-spring-boot-app-devops-1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]