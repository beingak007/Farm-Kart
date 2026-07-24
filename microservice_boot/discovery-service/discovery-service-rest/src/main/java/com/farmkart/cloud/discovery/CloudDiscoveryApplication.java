package com.farmkart.cloud.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka service registry — mirrors Farm Kart discovery-service.
 * Clients (marketplace, notification, agent, api-gateway) register here
 * so the gateway can resolve them via lb://&lt;service-id&gt;.
 */
@SpringBootApplication
@EnableEurekaServer
public class CloudDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudDiscoveryApplication.class, args);
    }
}
