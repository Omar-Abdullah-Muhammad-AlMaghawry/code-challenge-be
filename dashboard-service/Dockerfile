FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
COPY dashboard-service/pom.xml dashboard-service/
COPY stock-service/pom.xml stock-service/
RUN mvn dependency:go-offline -pl dashboard-service -q
COPY dashboard-service/src dashboard-service/src
RUN mvn package -pl dashboard-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/dashboard-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
