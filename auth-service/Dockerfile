FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
COPY dashboard-service/pom.xml dashboard-service/
COPY stock-service/pom.xml stock-service/
RUN mvn dependency:go-offline -pl auth-service -q
COPY auth-service/src auth-service/src
RUN mvn package -pl auth-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
