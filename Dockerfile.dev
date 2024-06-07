
FROM openjdk:17-jdk-slim

ARG PROFILE=prod

ENV SPRING_PROFILES_ACTIVE=${PROFILE}

WORKDIR /app

COPY target/ocean-0.0.1-SNAPSHOT.war /app/ocean.war

RUN useradd -m -s /bin/bash usuariotpc
USER usuariotpc
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/ocean.war"]
