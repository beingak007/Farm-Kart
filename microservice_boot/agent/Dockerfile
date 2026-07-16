FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl farm-kart-agent/farm-kart-agent-rest -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /app/farm-kart-agent/farm-kart-agent-rest/target/farm-kart-agent-rest-*.jar app.jar
COPY farm-kart/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8091
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["app.jar"]
