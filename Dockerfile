FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY notes_app.class .

CMD [ "java", "notes_app" ]

