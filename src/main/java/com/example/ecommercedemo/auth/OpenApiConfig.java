package com.example.ecommercedemo.auth;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenApiConfig {

  @Bean
  @Primary
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        // 1. Define the Security Scheme (JWT)
        .components(new Components()
            .addSecuritySchemes("bearer-key",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
        // 2. Apply it globally to all operations
        .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
  }
}
