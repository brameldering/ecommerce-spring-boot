package com.example.ecommercedemo.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

import static com.example.ecommercedemo.user.Constants.*;
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

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(
                API_URL_PREFIX + "**"
            )
// H2 specific: .ignoringRequestMatchers(toH2Console())
        )
// H2 specific: .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

        // 4. CORS Configuration
        .cors(cors -> {
        }) // Use the default configuration or a custom one if defined elsewhere

        // 5. Authorization
        .authorizeHttpRequests(auth -> auth
// H2 specific:  .requestMatchers(toH2Console()).permitAll()
                .requestMatchers(
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/api/openapi.yaml",
                    "/favicon.ico"
                ).permitAll()
                // Permit the following public Endpoints
            .requestMatchers(HttpMethod.POST, TOKEN_URL).permitAll()
            .requestMatchers(HttpMethod.DELETE, TOKEN_URL).permitAll()
            .requestMatchers(HttpMethod.POST, SIGNUP_URL).permitAll()
            .requestMatchers(HttpMethod.POST, REFRESH_URL).permitAll()
            .requestMatchers(HttpMethod.GET, PRODUCTS_URL).permitAll()

            // Role-based Access (Assuming RoleEnum.ADMIN is a real authority string)
            // Note: Ensure RoleEnum.ADMIN.getAuthority() returns a String like "ADMIN" or "ROLE_ADMIN"
            .requestMatchers("/api/v1/addresses/**").hasAuthority("ADMIN")

            // All other requests require authentication
            .anyRequest().authenticated()
        )

        // 6. OAuth2 Resource Server (JWT)
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        )

        // 7. Session Management
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
