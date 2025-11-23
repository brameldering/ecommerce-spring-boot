package com.example.ecommercedemo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public ItemNotFoundException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public ItemNotFoundException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.ITEM_NOT_FOUND.getErrMsgKey();
    this.errorCode = ErrorCode.ITEM_NOT_FOUND.getErrCode();
  }

}
