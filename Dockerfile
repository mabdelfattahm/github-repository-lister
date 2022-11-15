# 1st stage .. build the app
FROM eclipse-temurin:17-jdk-focal as build

WORKDIR /tmp/github-lister

# Copy gradle files
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Download gradle dependencies
RUN ./gradlew dependencies

# Build the app
COPY src src
RUN ./gradlew bootJar

# 2nd stage .. build the runtime image
FROM eclipse-temurin:17-jre-focal

WORKDIR /opt/github-lister

# Copy the binary built in the 1st stage
COPY --from=build /tmp/github-lister/build/libs/github-lister-boot.jar ./

CMD ["java", "-Xms512m", "-Xmx512m", "-jar", "github-lister-boot.jar"]

EXPOSE 8080