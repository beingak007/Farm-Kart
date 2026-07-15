package com.farmkart.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI farmKartOpenApi(@Value("${server.servlet.context-path:}") String contextPath) {
        String serverUrl = contextPath.isBlank() ? "/" : contextPath;

        return new OpenAPI()
                .addServersItem(new Server().url(serverUrl).description("Farm Kart API"))
                .info(new Info()
                        .title("Farm Kart Marketplace API")
                        .description("""
                                REST API for Farm Kart — mobile OTP login, Google OAuth, \
                                product sheets, orders, and notifications.

                                **OTP login flow**
                                1. `POST /api/v1/auth/send-otp` — send 6-digit OTP via SMS
                                2. `POST /api/v1/auth/verify-otp` — verify OTP and receive JWT

                                **Google login flow**
                                1. Obtain Google ID token on the frontend
                                2. `POST /api/v1/auth/oauth/login` with provider `GOOGLE`
                                """)
                        .version("v1")
                        .contact(new Contact().name("Farm Kart").email("support@farmkart.local"))
                        .license(new License().name("Proprietary")))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token returned by verify-otp, login, or oauth/login")));
    }
}
