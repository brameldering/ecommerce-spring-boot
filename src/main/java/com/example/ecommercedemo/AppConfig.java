package com.example.ecommercedemo;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.Map;

import static com.example.ecommercedemo.auth.Constants.ENCODER_ID;

// 🎯 The @OpenAPIDefinition is placed here on the @Configuration class
@OpenAPIDefinition(
    info = @Info(
        title = "Sample Ecommerce App",
        description = "This is a ***sample ecommerce app API***.  You can find out more about Swagger at [swagger.io](http://swagger.io).",
        termsOfService = "https://github.com/PacktPublishing/Modern-API-Development-with-Spring-and-Spring-Boot/blob/master/LICENSE",
        contact = @Contact(email = "support@packtpub.com"),
        license = @License(name = "MIT", url = "https://github.com/PacktPublishing/Modern-API-Development-with-Spring-and-Spring-Boot/blob/master/LICENSE"),
        version = "1.0.0"
    ),
    externalDocs = @ExternalDocumentation(
        description = "Any document link you want to generate along with API.",
        url = "http://swagger.io"
    ),
    servers = {
        @Server(url = "/api/v1", description = "Current server")
    },
    tags = {
        @Tag(name = "admin", description = "Administrative operations"),
        @Tag(name = "product", description = "Operations related to products"),
        @Tag(name = "customer", description = "Operations related to a customer"),
        @Tag(name = "address", description = "Operations related to a customer address"),
        @Tag(name = "card", description = "Operations related to credit/debit cards"),
        @Tag(
            name = "cart",
            description = "Everything related to cart",
            externalDocs = @ExternalDocumentation(
                description = "Find out more (extra document link)",
                url = "http://swagger.io"
            )
        ),
        @Tag(name = "order", description = "Operation related to orders"),
        @Tag(name = "payment", description = "Operations related to payments"),
        @Tag(name = "shipment", description = "Operations related to shipments"),
        @Tag(name = "user", description = "Operations about signup, signin and so on")
    }
)
@Configuration
public class AppConfig {
  @Bean
  public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
    return new ShallowEtagHeaderFilter();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // Supports other password encoding, a must for existing applications.
    // However, uses BCrypt for new passwords. This will allow to use new or future encoders
    Map<String, PasswordEncoder> encoders =
        Map.of(
            ENCODER_ID,
            new BCryptPasswordEncoder(),
            "pbkdf2",
            Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
            "scrypt",
            SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
    return new DelegatingPasswordEncoder(ENCODER_ID, encoders);
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }
}
