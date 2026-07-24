FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl api-gateway/api-gateway-rest -am

FROM eclipse-temurin:25-jre-alpine
WORKDIR /home
COPY --from=build /app/api-gateway/api-gateway-rest/target/api-gateway-rest-*.jar app.jar
COPY marketplace/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8079
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["app.jar"]
