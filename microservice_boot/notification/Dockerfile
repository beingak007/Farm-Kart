FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl notification/notification-rest -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /app/notification/notification-rest/target/notification-rest-*.jar app.jar
COPY marketplace/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8087
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["app.jar"]
