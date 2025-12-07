package com.example.ecommercedemo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import static com.example.ecommercedemo.auth.Constants.*;
// H2 specific:  import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final Logger LOG = LoggerFactory.getLogger(getClass());
//  private final UserDetailsService userService;
  private final PasswordEncoder bCryptPasswordEncoder;

  private final ObjectMapper mapper;

//  @Value("${app.security.jwt.keystore-location}")
//  private String keyStorePath;

  @Value("${app.security.jwt.keystore-location}")
  private Resource keyStoreResource;

  @Value("${app.security.jwt.keystore-password}")
  private String keyStorePassword;

  @Value("${app.security.jwt.key-alias}")
  private String keyAlias;

  @Value("${app.security.jwt.private-key-passphrase}")
  private String privateKeyPassphrase;

  public SecurityConfig(
//      UserDetailsService userService,
      @Lazy PasswordEncoder bCryptPasswordEncoder,
      @Lazy ObjectMapper mapper) {
//    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.mapper = mapper;
  }

  /**
   * 1. Public Filter Chain (Runs first due to @Order(1))
   * This chain handles all unprotected endpoints (Sign Up, Sign In, Swagger).
   */
  @Bean
  @Order(1)
  public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
    http
    // Define all public paths this chain should match
    .securityMatcher(
            TOKEN_URL, SIGNUP_URL, REFRESH_URL, PRODUCTS_URL, // API Auth Paths
            "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/api/openapi.yaml", "/favicon.ico" // Swagger/OpenAPI Paths
    )
    .csrf(csrf -> csrf.disable())
    .cors(cors -> {})
    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all matched paths
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  /**
   * 2. Protected Filter Chain (Runs second due to @Order(2))
   * This chain handles all remaining (protected) paths and applies JWT validation.
   * This is essentially your original filterChain, slightly modified.
   */
  @Bean
  @Order(2)
  protected SecurityFilterChain protectedFilterChain(HttpSecurity http) throws Exception {
      http
      .httpBasic(basic -> basic.disable())
      .formLogin(form -> form.disable())
      .csrf(csrf -> csrf.ignoringRequestMatchers(API_URL_PREFIX))
      .cors(cors -> {})

      // Authorization (only defining rules for protected APIs now)
      .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/v1/addresses/**").hasAuthority(RoleEnum.Const.ADMIN)
              .anyRequest().authenticated()
      )

      // OAuth2 Resource Server (JWT) - ONLY runs on protected paths because of the @Order
      .oauth2ResourceServer(oauth2 -> oauth2
              .jwt(jwt -> jwt
                      .jwtAuthenticationConverter(jwtAuthenticationConverter())
              )
      )

      .sessionManagement(session -> session
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      );

    return http.build();
  }

  private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {

    // 1. Configure the Granted Authorities Converter
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix(AUTHORITY_PREFIX);
    grantedAuthoritiesConverter.setAuthoritiesClaimName(ROLE_CLAIM);

    // 2. Configure the main Authentication Converter
    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

    return jwtConverter;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
    configuration.addAllowedHeader("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public KeyStore keyStore() {
    try {
      LOG.info("Loading keystore from: {}", keyStoreResource.getDescription());
      LOG.info("Resolved Keystore Path: {}", keyStoreResource.getURI());

      // Explicitly use PKCS12 instead of getDefaultType()
      KeyStore keyStore = KeyStore.getInstance("PKCS12");

      // Use the Resource object to get the stream
      InputStream resourceAsStream = keyStoreResource.getInputStream();
//      InputStream resourceAsStream =
//          Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath);

      keyStore.load(resourceAsStream, keyStorePassword.toCharArray());

      return keyStore;

    } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
      LOG.error("Unable to load keystore: {}", keyStoreResource.getDescription(), e);
    }
    throw new IllegalArgumentException("Unable to load keystore");
  }

  // The RSA private key is returned which is the key used for signing
  @Bean
  public RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
    try {
      Key key = keyStore.getKey(keyAlias, privateKeyPassphrase.toCharArray());
      if (key instanceof RSAPrivateKey) {
        return (RSAPrivateKey) key;
      }
    } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
      LOG.error("Unable to load private key from keystore: {}", keyStoreResource.getDescription(), e);
    }
    throw new IllegalArgumentException("Unable to load private key");
  }

  // The RSA public key is returned which is the key used for validating a signed token
  @Bean
  public RSAPublicKey jwtValidationKey(KeyStore keyStore) {
    try {
      // --- DEBUGGING BLOCK START ---
      LOG.info("--> Looking for alias: '{}'", this.keyAlias);
      java.util.Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        LOG.info("--> Found alias in keystore: '{}'", aliases.nextElement());
      }
      // --- DEBUGGING BLOCK END ---

      Certificate certificate = keyStore.getCertificate(this.keyAlias);

      if (certificate == null) {
        // The key alias lookup failed
        LOG.error("Certificate not found for alias: {}", this.keyAlias);
        throw new IllegalArgumentException("Certificate not found in keystore for alias: " + this.keyAlias);
      }

      PublicKey publicKey = certificate.getPublicKey();

      if (publicKey instanceof RSAPublicKey) {
        return (RSAPublicKey) publicKey;
      }

      throw new IllegalArgumentException("Unable to load RSA public key");

    } catch (KeyStoreException e) {
      LOG.error("Unable to load private key from keystore: {}", keyStoreResource.getDescription(), e);
      // Re-throw a RuntimeException to terminate bean creation
      throw new RuntimeException("Failed to access keystore for JWT validation.", e);
    }
  }

  @Bean
  public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey) {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
  }
}
