FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY farm-kart-starter-common/pom.xml farm-kart-starter-common/
COPY farm-kart-common-rest/pom.xml farm-kart-common-rest/
COPY farm-kart-framework/pom.xml farm-kart-framework/
COPY farm-kart-framework/farm-kart-framework-client/pom.xml farm-kart-framework/farm-kart-framework-client/
COPY farm-kart-framework/farm-kart-framework-repository/pom.xml farm-kart-framework/farm-kart-framework-repository/
COPY farm-kart-framework/farm-kart-framework-service/pom.xml farm-kart-framework/farm-kart-framework-service/
COPY farm-kart/pom.xml farm-kart/
COPY farm-kart/farm-kart-client/pom.xml farm-kart/farm-kart-client/
COPY farm-kart/farm-kart-repository/pom.xml farm-kart/farm-kart-repository/
COPY farm-kart/farm-kart-service/pom.xml farm-kart/farm-kart-service/
COPY farm-kart/farm-kart-rest/pom.xml farm-kart/farm-kart-rest/
RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -DskipTests -pl farm-kart/farm-kart-rest -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /home
COPY --from=build /app/farm-kart/farm-kart-rest/target/farm-kart-rest-*.jar farm-kart-rest.jar
COPY farm-kart/docker-entrypoint.sh docker-entrypoint.sh
RUN chmod +x /home/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/home/docker-entrypoint.sh"]
CMD ["farm-kart-rest.jar"]
