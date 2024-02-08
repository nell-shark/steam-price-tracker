# Build stage
FROM gradle:8.6.0-jdk21-alpine AS build

WORKDIR /backend

COPY build.gradle settings.gradle gradle ./

RUN gradle --no-daemon clean dependencies

COPY . .

RUN gradle --no-daemon clean bootJar

# Run stage
FROM openjdk:21-oracle

WORKDIR /backend

COPY --from=build /backend/build/libs/*.jar ./backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend.jar"]
