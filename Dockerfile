# Use Eclipse Temurin (Standard OpenJDK build)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the compiled class file
# (Ensure your CI pipeline names the file SecureNotesApp.class correctly!)
COPY SecureNotesApp.class .

# Run the application
CMD ["java", "SecureNotesApp"]
