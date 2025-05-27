FROM eclipse-temurin:17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/build/libs/docstory-0.0.1-SNAPSHOT.jar /app/docstory.jar
ENTRYPOINT ["java", "-jar", "/app/docstory.jar"]
