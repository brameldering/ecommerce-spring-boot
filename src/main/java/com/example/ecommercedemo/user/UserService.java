package com.example.ecommercedemo.user;

import com.example.ecommercedemo.model.RefreshToken;
import com.example.ecommercedemo.model.SignedInUser;
import com.example.ecommercedemo.model.SignUpReq;

import java.util.Optional;

public interface UserService {

  SignedInUser createUser(SignUpReq user);

  UserEntity findUserByUsername(String username);

  SignedInUser getSignedInUser(UserEntity userEntity);

  Optional<SignedInUser> getAccessToken(RefreshToken refreshToken);

  void removeRefreshToken(RefreshToken refreshToken);
}
