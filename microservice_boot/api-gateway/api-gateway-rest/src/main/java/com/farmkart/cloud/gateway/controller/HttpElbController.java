package com.farmkart.cloud.gateway.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * Lightweight ELB / load-balancer probe (Yagna {@code HttpElbController} pattern).
 */
@RestController
public class HttpElbController {

    @GetMapping({"/", "/elb-health", "/api-gateway/elb-health"})
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of("status", "UP", "service", "api-gateway"));
    }
}
