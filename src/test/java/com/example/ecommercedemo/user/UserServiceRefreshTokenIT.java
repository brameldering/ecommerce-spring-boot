package com.example.ecommercedemo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ecommercedemo.auth.RoleEnum;
import com.example.ecommercedemo.config.TestcontainersConfiguration;
import com.example.ecommercedemo.exception.InvalidRefreshTokenException;
import com.example.ecommercedemo.model.RefreshToken;
import com.example.ecommercedemo.model.SignedInUser;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Integration test for refreshing an access token against a real PostgreSQL database.
 *
 * <p>Deliberately not {@code @Transactional}: a test transaction would keep the Hibernate session
 * open and hide lazy-loading errors that happen in production, where {@code open-in-view} is off.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserServiceRefreshTokenIT {

  @Autowired private UserService userService;
  @Autowired private UserRepository userRepository;
  @Autowired private UserTokenRepository userTokenRepository;

  private UserEntity user;

  @BeforeEach
  void createDedicatedUser() {
    // username column is limited to 16 characters
    String username = "it-" + UUID.randomUUID().toString().substring(0, 12);
    user =
        userRepository.save(
            new UserEntity()
                .setUsername(username)
                .setPassword("{noop}not-used-by-this-test")
                .setRole(RoleEnum.USER));
  }

  @AfterEach
  void deleteDedicatedUser() {
    userTokenRepository.findAll().forEach(
        token -> {
          if (user.getId().equals(token.getUser().getId())) {
            userTokenRepository.delete(token);
          }
        });
    userRepository.deleteById(user.getId());
  }

  @Test
  void getAccessTokenIssuesNewAccessTokenForValidRefreshToken() {
    SignedInUser signedIn = userService.getSignedInUser(user);

    SignedInUser refreshed =
        userService
            .getAccessToken(new RefreshToken().refreshToken(signedIn.getRefreshToken()))
            .orElseThrow();

    assertThat(refreshed.getUsername()).isEqualTo(user.getUsername());
    assertThat(refreshed.getUserId()).isEqualTo(user.getId());
    assertThat(refreshed.getAccessToken()).isNotBlank();
    assertThat(refreshed.getRefreshToken()).isEqualTo(signedIn.getRefreshToken());
  }

  @Test
  void getAccessTokenRejectsUnknownRefreshToken() {
    RefreshToken unknown = new RefreshToken().refreshToken("not-a-real-refresh-token");

    assertThatThrownBy(() -> userService.getAccessToken(unknown))
        .isInstanceOf(InvalidRefreshTokenException.class);
  }

  @Test
  void removeRefreshTokenRevokesItSoItCanNoLongerBeUsed() {
    RefreshToken token =
        new RefreshToken().refreshToken(userService.getSignedInUser(user).getRefreshToken());

    userService.removeRefreshToken(token);

    assertThatThrownBy(() -> userService.getAccessToken(token))
        .isInstanceOf(InvalidRefreshTokenException.class);
  }
}
