# Build stage (Java 17 to match pom)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# Runtime stage (Temurin JRE + Chromium/Chromedriver + deps)
FROM eclipse-temurin:17-jre
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium chromium-driver \
    libnss3 libatk-bridge2.0-0 libatk1.0-0 libdrm2 libgbm1 libgtk-3-0 \
    libxcomposite1 libxrandr2 libxdamage1 libxfixes3 libxkbcommon0 \
    libasound2 libu2f-udev fonts-liberation ca-certificates \
    && rm -rf /var/lib/apt/lists/*

ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER=/usr/bin/chromedriver
ENV XDG_RUNTIME_DIR=/tmp/runtime
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
RUN mkdir -p /tmp/runtime && chmod 700 /tmp/runtime

WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
CMD ["sh","-c","java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
