package com.example.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This class configures Spring Security for the whole application:
 * how passwords are hashed, and which endpoints require a logged-in
 * user versus which ones are open to anyone.
 *
 * @EnableMethodSecurity turns on support for security annotations
 * directly on service/controller methods, in case we want to add
 * fine-grained, role-based checks later (for example, restricting
 * "delete student" to ADMIN accounts only).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Defines HOW passwords get turned into a stored hash.
     *
     * We use BCrypt, which is a well-tested, industry-standard hashing
     * algorithm designed specifically for passwords (it's intentionally
     * slow, which makes brute-force attacks much harder). Spring Security
     * automatically uses this bean whenever it needs to hash a new
     * password or check a submitted password against a stored hash.
     *
     * This is used in two places: DataInitializer (when seeding demo
     * accounts) and automatically by Spring Security itself during
     * HTTP Basic login.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines WHICH requests require authentication.
     *
     * We keep this simple for a student project:
     *   - Swagger UI, the raw OpenAPI docs, and the health check
     *     endpoint are open to everyone (no login needed), so anyone
     *     can explore the API docs without credentials.
     *   - Every other request must be authenticated using HTTP Basic
     *     auth (a username/password sent with every request), checked
     *     against the accounts in the app_users table.
     *
     * CSRF (Cross-Site Request Forgery) protection is disabled here
     * because CSRF tokens are designed for browser-based apps that use
     * cookies for authentication. This API uses HTTP Basic auth
     * instead, which isn't vulnerable to CSRF in the same way, so CSRF
     * protection isn't needed.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic(httpBasic -> {
                    // Using the defaults here is fine for this project -
                    // no extra customization needed.
                });

        return httpSecurity.build();
    }
}
