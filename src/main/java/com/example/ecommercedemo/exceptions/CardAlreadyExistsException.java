package com.example.ecommercedemo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class CardAlreadyExistsException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public CardAlreadyExistsException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public CardAlreadyExistsException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.ITEM_ALREADY_EXISTS.getErrMsgKey();
    this.errorCode = ErrorCode.ITEM_ALREADY_EXISTS.getErrCode();
  }
}
