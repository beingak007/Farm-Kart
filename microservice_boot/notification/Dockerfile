FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl farm-kart-notification/farm-kart-notification-rest -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /app/farm-kart-notification/farm-kart-notification-rest/target/farm-kart-notification-rest-*.jar app.jar
COPY farm-kart/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8087
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["app.jar"]
