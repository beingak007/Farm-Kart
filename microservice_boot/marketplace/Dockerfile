FROM maven:3-eclipse-temurin-26 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests -pl marketplace/marketplace-rest -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /home
COPY --from=build /app/marketplace/marketplace-rest/target/marketplace-rest-*.jar marketplace-rest.jar
COPY marketplace/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["marketplace-rest.jar"]
