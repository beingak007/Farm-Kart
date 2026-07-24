FROM maven:3-eclipse-temurin-26 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl discovery-service/discovery-service-rest -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /app/discovery-service/discovery-service-rest/target/discovery-service-rest-*.jar app.jar
COPY marketplace/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8084
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["app.jar"]
