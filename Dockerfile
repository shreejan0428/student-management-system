# ---- Stage 1: Build ----
# We use a Maven image that already has JDK 21 installed, just to
# compile the project and produce a runnable .jar file.
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# -DskipTests: we skip running the test suite during the Docker build
# itself (tests are run separately in CI - see .github/workflows/ci.yml)
# just to keep the image build fast.
RUN mvn -q -DskipTests package

# ---- Stage 2: Run ----
# This second, separate stage starts from a much smaller image that
# only has a Java Runtime Environment (JRE), not the full JDK or
# Maven. We only copy the final .jar file over from the build stage.
# This "multi-stage build" keeps the final image small, since none of
# the build tools end up in it.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/student-management-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
