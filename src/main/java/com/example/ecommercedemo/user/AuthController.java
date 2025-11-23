package com.example.ecommercedemo.user;

import com.example.ecommercedemo.api.UserApi;
import com.example.ecommercedemo.exception.InvalidRefreshTokenException;
import com.example.ecommercedemo.model.RefreshToken;
import com.example.ecommercedemo.model.SignInReq;
import com.example.ecommercedemo.model.SignedInUser;
import com.example.ecommercedemo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.springframework.http.ResponseEntity.*;

@RestController
public class AuthController implements UserApi {

  private final UserService service;
  private final PasswordEncoder passwordEncoder;

  public AuthController(UserService service, PasswordEncoder passwordEncoder) {
    this.service = service;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public ResponseEntity<SignedInUser> signUp(User user) {
    // 1. Validation (Input and Business Logic) is handled before or during the service call.
    // 2. The called service is guaranteed to return a SignedInUser (or throw an exception).
    return status(HttpStatus.CREATED).body(service.createUser(user));
  }

  @Override
  public ResponseEntity<SignedInUser> signIn(SignInReq signInReq) {
    UserEntity userEntity = service.findUserByUsername(signInReq.getUsername());
    if (passwordEncoder.matches(signInReq.getPassword(), userEntity.getPassword())) {
      return ok(service.getSignedInUser(userEntity));
    }
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
