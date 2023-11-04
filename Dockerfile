FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the Gradle Wrapper files
COPY gradle/wrapper gradle/wrapper
COPY gradlew .

# Copy the build and source files
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Copy config file for docker
WORKDIR /app/src/main/resources
RUN if [ -e application-docker.yml ]; then cp application-docker.yml application.yml && rm application-docker.yml; fi
WORKDIR /app

# Run the Gradle Wrapper to build the project
RUN ./gradlew build -x test

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/software-testing-crud-0.0.1-SNAPSHOT.jar"]