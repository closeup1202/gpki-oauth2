FROM openjdk:17.0.2-slim
COPY build/libs/*.jar /app/

RUN mkdir /app/logs
RUN mkdir /app/errorlogs

EXPOSE 9900
ENTRYPOINT ["java", "-jar", "/app/gpki-auth-0.0.1-SNAPSHOT.jar"]