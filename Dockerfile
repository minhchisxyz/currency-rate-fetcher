# Dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# Runtime stage (Debian slim with JRE + Chromium)
FROM openjdk:21-jre-slim
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium chromium-driver fonts-liberation ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Help Selenium locate Chrome/Driver
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER=/usr/bin/chromedriver
# Container-friendly JVM defaults
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

# Local dev convenience
EXPOSE 8080

# Render sets $PORT. Pass it to Spring via -Dserver.port
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
