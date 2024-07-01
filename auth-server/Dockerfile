FROM openjdk:17.0.2-slim
WORKDIR /app
COPY ./target/auth-server-0.0.1.jar /app/auth-server-0.0.1.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "auth-server-0.0.1.jar"]
