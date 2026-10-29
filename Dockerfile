# ---------- Stage 1: build the JAR ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first and download dependencies.
# This layer is cached and only re-runs when pom.xml changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Now copy the source code and build (tests are run by Jenkins in its own stage)
COPY src ./src
RUN mvn -B clean package -DskipTests


# ---------- Stage 2: slim runtime image ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create and use a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy only the built JAR from the build stage
COPY --from=build /app/target/calculator.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
