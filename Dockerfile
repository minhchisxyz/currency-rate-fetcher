# Build stage (Java 17 to match pom)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# Runtime stage (Temurin JRE + Chromium/Chromedriver)
FROM eclipse-temurin:17-jre
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium chromium-driver fonts-liberation ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Help Selenium locate Chrome/Driver
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER=/usr/bin/chromedriver
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
