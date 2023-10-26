FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the Gradle Wrapper files
COPY gradle/wrapper gradle/wrapper
COPY gradlew .

# Copy the build and source files
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Run the Gradle Wrapper to build the project
RUN ./gradlew build -x test

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/software-testing-crud-0.0.1-SNAPSHOT.jar"]