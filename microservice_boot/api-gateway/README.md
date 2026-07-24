# API Gateway — Spring Cloud Gateway (Farm Kart-style)
#
# Port: 8079
# Single entry for:
#   /farm-kart/**            → lb://farm-kart            (marketplace :8080)
#   /notification-service/** → lb://notification-service (:8087)
#   /agent-service/**        → lb://agent                (:8091)
#
# Requires discovery-service (Eureka) on :8084 unless profile `static-routing` is active.
#
# Build:  cd microservice_boot && mvn -pl api-gateway/api-gateway-rest -am package
# Run:    java -jar api-gateway/api-gateway-rest/target/api-gateway-rest-*.jar
#
# Static local (no Eureka):
#   java -jar ... --spring.profiles.active=dev,static-routing
