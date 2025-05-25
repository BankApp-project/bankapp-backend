FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src/ ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

RUN addgroup -S backend \
 && adduser -S -G backend backend

RUN mkdir -p /app/logs \
&& chown backend:backend /app/logs

USER backend

EXPOSE 8080
ENTRYPOINT ["docker-entrypoint.sh"]
