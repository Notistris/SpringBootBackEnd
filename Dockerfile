FROM gradle:8.8-jdk21 AS dev

WORKDIR /app

COPY . .

RUN chmod +x gradlew

EXPOSE 8080

CMD ["./gradlew", "bootRun", "--no-daemon"]