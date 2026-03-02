# Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Make wrapper executable
RUN chmod +x mvnw

# Download dependencies separately
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests


# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
VOLUME /tmp

# Copy built jar from build
COPY --from=build /app/target/*.jar app.jar

# Port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]