package com.example.ecommercedemo.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import static com.example.ecommercedemo.auth.Constants.EXPIRATION_TIME;
import static com.example.ecommercedemo.auth.Constants.ROLE_CLAIM;
import static java.util.stream.Collectors.toList;

@Component
public class JwtManager {

  private final RSAPrivateKey privateKey;
  private final RSAPublicKey publicKey;

  public JwtManager(@Lazy RSAPrivateKey privateKey, @Lazy RSAPublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

// The JWT Manager does 2 things:
// 1. Creation (Building the Payload):
//    It gathers the necessary user information (username, roles/authorities)
//    and time constraints (issue time, expiration time)
//    and constructs the token's payload (the claims).
//
// 2. Signing (Ensuring Security):
//    It uses the asymmetric algorithm RSA256 and the injected private key
//    to generate a digital signature for the token. This signature proves
//    the token was issued by your server and ensures its contents haven't been tampered with.
  public String create(UserDetails principal) {
    final long now = System.currentTimeMillis();
    return JWT.create()
        .withIssuer("Modern API Development with Spring and Spring Boot")
        .withSubject(principal.getUsername())
        .withClaim(
            ROLE_CLAIM,
            principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(toList()))
        .withIssuedAt(new Date(now))
        .withExpiresAt(new Date(now + EXPIRATION_TIME))
        // .sign(Algorithm.HMAC512(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
        .sign(Algorithm.RSA256(publicKey, privateKey));
  }
}
