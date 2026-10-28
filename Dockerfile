# =====================================================================
# Lab 6 Dockerfile - Multi-stage build for the CLI Calculator
# Stage 1 (builder): compile the project with Maven + JDK 17
# Stage 2 (runtime): copy only the executable jar into a slim JRE image
# =====================================================================

# ---------- Stage 1: Build ----------
# FROM: base image that already contains Maven and JDK 17, so the build
# tool chain does not have to be installed by hand. "AS builder" names
# the stage so the runtime stage can copy from it.
# NOTE: the handout suggests maven:3.8-openjdk-17, but that tag was removed
# from Docker Hub in 2022 when the official openjdk images were retired, so
# it now fails with "manifest unknown". maven:3.9-eclipse-temurin-17 is the
# maintained equivalent (same Maven line, same JDK 17).
FROM maven:3.9-eclipse-temurin-17 AS builder

# WORKDIR: working directory inside the image for all following commands.
WORKDIR /app

# COPY: copy only the POM first. Docker caches one layer per instruction,
# so the dependency layer stays valid when only the source code changes.
COPY pom.xml .

# RUN: pre-download all Maven dependencies during the build
# (-B = batch mode, non-interactive output).
RUN mvn dependency:go-offline -B

# COPY: copy the application source and tests.
COPY src ./src

# RUN: compile and package the executable (shaded) jar.
# -DskipTests: unit tests already run in the CI test job.
RUN mvn package -DskipTests

# ---------- Stage 2: Runtime ----------
# FROM: lightweight JRE-only image. No Maven, no compiler, therefore the
# final image is far smaller than the build stage.
# NOTE: openjdk:17-jre-slim is deprecated and no longer published either;
# eclipse-temurin is the distribution recommended by the Docker docs, and
# the alpine variant keeps the runtime image around 150 MB.
FROM eclipse-temurin:17-jre-alpine

# WORKDIR: working directory of the runtime stage.
WORKDIR /app

# COPY --from: bring the jar produced in stage 1 into this image.
# The file name follows <finalName> in pom.xml: calculator-${project.version}.jar
# (maven-shade-plugin replaces the main artifact with the fat jar).
COPY --from=builder /app/target/calculator-1.0.jar app.jar

# ENTRYPOINT: the process started when the container runs. The exec form
# (JSON array) makes java PID 1, so it receives signals directly and can
# shut down cleanly. Extra arguments are appended:
#   docker run --rm calculator:v1 3 + 5
ENTRYPOINT ["java", "-jar", "app.jar"]
