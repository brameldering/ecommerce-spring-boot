package com.example.ecommercedemo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class UsernameAlreadyExistsException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public UsernameAlreadyExistsException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public UsernameAlreadyExistsException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.USERNAME_ALREADY_EXISTS.getErrMsgKey();
    this.errorCode = ErrorCode.USERNAME_ALREADY_EXISTS.getErrCode();
  }
}
