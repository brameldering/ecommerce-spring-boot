package com.example.ecommercedemo.auth;

import com.example.ecommercedemo.api.UserApi;
import com.example.ecommercedemo.exception.InvalidRefreshTokenException;
import com.example.ecommercedemo.model.RefreshToken;
import com.example.ecommercedemo.model.SignInReq;
import com.example.ecommercedemo.model.SignedInUser;
import com.example.ecommercedemo.model.SignUpReq;
import com.example.ecommercedemo.user.UserEntity;
import com.example.ecommercedemo.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/v1") // listen for the /api/v1 prefix for all methods in that controller, overriding what the OpenAPI generator put in the method-level mapping
public class AuthController implements UserApi {

  private final UserService service;
  private final PasswordEncoder passwordEncoder;

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  public AuthController(UserService service, PasswordEncoder passwordEncoder) {
    this.service = service;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public ResponseEntity<SignedInUser> signUp(SignUpReq user) {
    // 1. Validation (Input and Business Logic) is handled before or during the service call.
    // 2. The called service is guaranteed to return a SignedInUser (or throw an exception).
    return status(HttpStatus.CREATED).body(service.createUser(user));
  }

  @Override
  public ResponseEntity<SignedInUser> signIn(SignInReq signInReq) {
    LOG.info("SignIn Username: " + signInReq.getUsername());
    UserEntity userEntity = service.findUserByUsername(signInReq.getUsername());
    LOG.info("SignIn UserEntity: " + userEntity);
    if (passwordEncoder.matches(signInReq.getPassword(), userEntity.getPassword())) {
      LOG.info("Password matches");
      LOG.info("Role: " + userEntity.getRole());
      return ok(service.getSignedInUser(userEntity));
    }
    LOG.info("Password does NOT match");
    throw new InsufficientAuthenticationException("Unauthorized.");
  }

  @Override
  public ResponseEntity<Void> signOut(RefreshToken refreshToken) {
    // We are using removeToken API for signout.
    // Ideally you would like to get tgit she user ID from Logged in user's request
    // and remove the refresh token based on retrieved user id from request.
    service.removeRefreshToken(refreshToken);
    return accepted().build();
  }

  @Override
  public ResponseEntity<SignedInUser> getAccessToken(RefreshToken refreshToken) {
    return ok(service.getAccessToken(refreshToken).orElseThrow(InvalidRefreshTokenException::new));
  }
}
