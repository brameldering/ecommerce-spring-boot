package com.example.ecommercedemo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ItemAlreadyExistsException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public ItemAlreadyExistsException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public ItemAlreadyExistsException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.ITEM_ALREADY_EXISTS.getErrMsgKey();
    this.errorCode = ErrorCode.ITEM_ALREADY_EXISTS.getErrCode();
  }
}
