package com.example.ecommercedemo.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class OrderCreationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public OrderCreationException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public OrderCreationException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.ORDER_CREATION_FAILED.getErrMsgKey();
    this.errorCode = ErrorCode.ORDER_CREATION_FAILED.getErrCode();
  }
}
