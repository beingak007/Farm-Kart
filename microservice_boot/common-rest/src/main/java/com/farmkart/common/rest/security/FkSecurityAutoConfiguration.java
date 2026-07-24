package com.farmkart.common.rest.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import({JwtAuthenticationFilter.class, FkSecurityHandlers.class})
@ConditionalOnProperty(name = "farmkart.security.enabled", havingValue = "true", matchIfMissing = true)
public class FkSecurityAutoConfiguration {

    @Bean
    SecurityFilterChain fkSecurityFilterChain(HttpSecurity http,
                                              JwtAuthenticationFilter jwtFilter,
                                              FkSecurityHandlers securityHandlers,
                                              FkRoleAuthorizationManager authorizationManager) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityHandlers)
                        .accessDeniedHandler(securityHandlers))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/**/swagger-ui/**",
                                "/**/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/**/api-docs/**",
                                "/**/v3/api-docs/**")
                        .permitAll()
                        .anyRequest().access(authorizationManager))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    FkRoleAuthorizationManager fkRoleAuthorizationManager() {
        return new FkRoleAuthorizationManager();
    }
}
