
## Build stage
FROM maven:3.8.4-openjdk-17 as builder

COPY src /tmp/mancala-backend/src
COPY pom.xml /tmp/mancala-backend/pom.xml

WORKDIR /tmp/mancala-backend

# Build a release artifact.
RUN mvn package -Dmaven.test.skip

## Package stage
FROM openjdk:17

# Copy the jar to the production image from the builder stage.
COPY --from=builder /tmp/mancala-backend/target/mancala-*.jar /mancala.jar

EXPOSE 8080

# Run the web service on container startup.
CMD ["java", "-jar", "/mancala.jar"]
