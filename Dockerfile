# ─── STAGE 1: BUILD ───────────────────────────────────────
FROM maven:3.9-amazoncorretto-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ─── STAGE 2: RUN ─────────────────────────────────────────
FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=builder /build/target/TaskFlow-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]