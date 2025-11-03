package com.example.ecommercedemo.exceptions;

import com.fasterxml.jackson.core.JsonParseException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestApiErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(RestApiErrorHandler.class);
  private final MessageSource messageSource;

  @Autowired
  public RestApiErrorHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> handleException(HttpServletRequest request, Exception ex, Locale locale) {
    log.error("Server error: {} for {} {} with stacktrace {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL(), ex.getStackTrace());

    Error error = ErrorUtils
        .createError(ErrorCode.GENERIC_ERROR.getErrMsgKey(), ErrorCode.GENERIC_ERROR.getErrCode(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Error> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex, Locale locale) {
    log.warn("Constraint violation: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.CONSTRAINT_VIOLATION.getErrMsgKey(),
            ErrorCode.CONSTRAINT_VIOLATION.getErrCode(),
            HttpStatus.BAD_REQUEST.value()) // 400 Bad Request
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Optionally add more detail from the exception
    // error.setErrors(ex.getConstraintViolations()...);

    // Use HttpStatus.BAD_REQUEST for the response
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Error> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex, Locale locale) {
    // Log this as a 'warn' because it's a client error, not a server failure
    log.warn("Illegal argument: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrMsgKey(), // Or a more specific key
            ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrCode(),           // if you have one
            HttpStatus.BAD_REQUEST.value()) // 400 Bad Request
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Make sure to return BAD_REQUEST in the ResponseEntity itself
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  //  This is the exception Spring throws when the request body DTO fails Bean Validation
  //  (due to annotations like @NotNull, @Size, etc. on fields in AddAddressReq).
  //  This is the standard mechanism for handling @Valid on controller method arguments.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Error> handleMethodArgumentNotValidException(
      HttpServletRequest request,
      MethodArgumentNotValidException ex,
      Locale locale) {

    log.warn("Method Argument Not Valid: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    // Create a map of field errors for detailed response
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      fieldErrors.put(fieldName, errorMessage);
    });

    // Create the main error object
    Error error = ErrorUtils
        .createError(
            ErrorCode.VALIDATION_ERROR.getErrMsgKey(),
            ErrorCode.VALIDATION_ERROR.getErrCode(),
            HttpStatus.BAD_REQUEST.value()
        ).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Attach the detailed field errors to the response
    // (assuming your Error class has a setErrors method for a Map)
    // If your 'Error' class cannot hold a Map of field errors,
    // you'll need to update its structure or
    // create a separate DTO specifically for validation error responses.
    // Assuming ErrorUtils.createError returns an object that can hold field details:
    // error.setErrors(fieldErrors);

    // For a simple response without listing all field errors:
    // Just return the general BAD_REQUEST error.

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions thrown when a path or query parameter (like a UUID) cannot be converted to the required type.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Error> handleMethodArgumentTypeMismatchException(
      HttpServletRequest request,
      MethodArgumentTypeMismatchException ex,
      Locale locale) {

    // Log the event with details about the failed parameter
    log.warn("Method argument type mismatch: Failed to convert value '{}' to type '{}' for parameter '{}'. Error: {}",
        ex.getValue(), ex.getRequiredType(), ex.getName(), ex.getMessage());

    String message = String.format("Parameter '%s' has an invalid format. Expected type: %s",
        ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");

    // Create the error object
    Error error = ErrorUtils
        .createError(
            message,
            ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrCode(),
            HttpStatus.BAD_REQUEST.value()
        ).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

     return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Error> handleHttpMediaTypeNotSupportedException(HttpServletRequest request, HttpMediaTypeNotSupportedException ex, Locale locale) {
    log.warn("Http Media Type Not Supported: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MEDIATYPE_NOT_SUPPORTED.getErrMsgKey(),
            ErrorCode.HTTP_MEDIATYPE_NOT_SUPPORTED.getErrCode(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpMessageNotWritableException.class)
  public ResponseEntity<Error> handleHttpMessageNotWritableException(HttpServletRequest request, HttpMessageNotWritableException ex, Locale locale) {
    log.warn("Http Message Mot Writable: {} for {} {}",
      ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MESSAGE_NOT_WRITABLE.getErrMsgKey(),
            ErrorCode.HTTP_MESSAGE_NOT_WRITABLE.getErrCode(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<Error> handleHttpMediaTypeNotAcceptableException(HttpServletRequest request, HttpMediaTypeNotAcceptableException ex, Locale locale) {
    log.warn("Http Media Type Not Acceptable: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrMsgKey(),
            ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrCode(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Error> handleHttpMessageNotReadableException(HttpServletRequest request,HttpMessageNotReadableException ex, Locale locale) {
    log.warn("Http Message Not Readable Exception: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

   Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MESSAGE_NOT_READABLE.getErrMsgKey(),
            ErrorCode.HTTP_MESSAGE_NOT_READABLE.getErrCode(),
            HttpStatus.NOT_ACCEPTABLE.value()).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
  }

  @ExceptionHandler(JsonParseException.class)
  public ResponseEntity<Error> handleJsonParseException(HttpServletRequest request, JsonParseException ex, Locale locale) {
    log.warn("Json Parse Exception: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

   Error error = ErrorUtils
        .createError(ErrorCode.JSON_PARSE_ERROR.getErrMsgKey(),
            ErrorCode.JSON_PARSE_ERROR.getErrCode(),
            HttpStatus.NOT_ACCEPTABLE.value()).setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
  }
}

