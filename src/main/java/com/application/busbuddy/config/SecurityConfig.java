package com.application.busbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/**").permitAll() // Allow unauthenticated access to user endpoints
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Deprecated version is .csrf().disable()
                .httpBasic(Customizer.withDefaults()); // Enables basic auth

        return http.build();
    }
}
