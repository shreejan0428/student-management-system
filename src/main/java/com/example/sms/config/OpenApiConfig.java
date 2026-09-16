package com.example.sms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tells springdoc (the library that generates our Swagger UI page and
 * the underlying OpenAPI spec) that this API uses HTTP Basic
 * authentication.
 *
 * Without this class, Spring Security still requires a username and
 * password for every request (see SecurityConfig.java), but Swagger UI
 * has no way of knowing that, so its "Authorize" button either doesn't
 * appear or doesn't actually attach credentials to the requests it
 * sends. This class fixes that by explicitly registering a security
 * scheme named "basicAuth" and applying it to every endpoint.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "basicAuth";

    @Bean
    public OpenAPI customOpenApi() {
        SecurityScheme basicAuthScheme =
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, basicAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
