# --- Stage 1: Build the App ---
# We use the JDK (Java Development Kit) to compile the code
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy the SOURCE code (the .java file), not the compiled file
COPY notes_app.java .

# Compile it inside the container
RUN javac notes_app.java

# --- Stage 2: Run the App ---
# We use the JRE (Java Runtime Environment) which is smaller and safer
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy ONLY the compiled .class file from the 'builder' stage above
COPY --from=builder /app/notes_app.class .

# Run the app
CMD ["java", "notes_app"]
