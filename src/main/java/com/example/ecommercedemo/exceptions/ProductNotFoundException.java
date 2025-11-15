package com.example.ecommercedemo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String errMsgKey;
  private final String errorCode;

  public ProductNotFoundException(ErrorCode code) {
    super(code.getErrMsgKey());
    this.errMsgKey = code.getErrMsgKey();
    this.errorCode = code.getErrCode();
  }

  public ProductNotFoundException(final String message) {
    super(message);
    this.errMsgKey = ErrorCode.PRODUCT_NOT_FOUND.getErrMsgKey();
    this.errorCode = ErrorCode.PRODUCT_NOT_FOUND.getErrCode();
  }

}
