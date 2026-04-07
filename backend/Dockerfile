FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package


FROM eclipse-temurin:21-jre

WORKDIR /data

COPY --from=build /build/target/service-booking-and-consulting-platform-phase2-0.0.1-SNAPSHOT.jar /opt/app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]