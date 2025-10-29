package com.example.ecommercedemo.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class AddressCreationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public AddressCreationException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public AddressCreationException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.ADDRESS_CREATION_FAILED.getErrMsgKey();
    this.errorCode = ErrorCode.ADDRESS_CREATION_FAILED.getErrCode();
  }

}
