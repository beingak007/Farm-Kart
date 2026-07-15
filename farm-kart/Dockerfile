FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl farm-kart/farm-kart-rest -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /app/farm-kart/farm-kart-rest/target/farm-kart-rest-*.jar farm-kart-rest.jar
COPY farm-kart/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["farm-kart-rest.jar"]
