package com.example.ecommercedemo.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An enumeration of error codes and associated i18n message keys for order
 * related validation errors.
 *
 **/
@Getter
@AllArgsConstructor
public enum ErrorCode {
  // Internal Errors: 1 to 0999
  GENERIC_ERROR("PACKT-0001", "The system is unable to complete the request. Contact system support."),
  HTTP_MEDIATYPE_NOT_SUPPORTED("PACKT-0002", "Requested media type is not supported. Please use application/json or application/xml as 'Content-Type' header value"),
  HTTP_MESSAGE_NOT_WRITABLE("PACKT-0003", "Missing 'Accept' header. Please add 'Accept' header."),
  HTTP_MEDIA_TYPE_NOT_ACCEPTABLE("PACKT-0004", "Requested 'Accept' header value is not supported. Please use application/json or application/xml as 'Accept' value"),
  JSON_PARSE_ERROR("PACKT-0005", "Make sure request payload should be a valid JSON object."),
  HTTP_MESSAGE_NOT_READABLE("PACKT-0006", "Make sure request payload should be a valid JSON or XML object according to 'Content-Type'."),
  HTTP_REQUEST_METHOD_NOT_SUPPORTED("PACKT-0007", "Request method not supported."),
  CONSTRAINT_VIOLATION("PACKT-0008", "Validation failed."),
  ILLEGAL_ARGUMENT_EXCEPTION("PACKT-0009", "Invalid data passed."),
  PRODUCT_NOT_FOUND("PACKT-0010", "Product not found"),
  CUSTOMER_NOT_FOUND("PACKT-0011", "Requested customer not found"),
  CART_NOT_FOUND("PACKT-0012", "Cart not found"),
  ITEM_NOT_FOUND("PACKT-0013", "Item not found"),
  CUSTOMER_ALREADY_EXISTS("PACKT-0014", "Customer already exists."),
  ITEM_ALREADY_EXISTS("PACKT-0015", "Item already exists."),
  ADDRESS_CREATION_FAILED("PACKT-0016", "AAddress creation failed."),
  ORDER_CREATION_FAILED("PACKT-0017", "Order creation failed."),
  VALIDATION_ERROR("PACKT-0018", "Validation of input failed."),;

  private final String errCode;
  private final String errMsgKey;
}
