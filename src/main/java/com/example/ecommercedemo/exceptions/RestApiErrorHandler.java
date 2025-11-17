package com.example.ecommercedemo.exceptions;

import com.fasterxml.jackson.core.JsonParseException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

  @ExceptionHandler(CardAlreadyExistsException.class)
  public ResponseEntity<Error> cardAlreadyExistsException(HttpServletRequest request, CardAlreadyExistsException ex, Locale locale) {
    log.warn("Card already exists (404): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        // Use the fields from your exception object
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.CONFLICT.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 409 response
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(CartNotFoundException.class)
  public ResponseEntity<Error> cartNotFoundException(HttpServletRequest request, CartNotFoundException ex, Locale locale) {
    log.warn("Cart Not Found (404): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.NOT_FOUND.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 404 response
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CustomerAlreadyExistsException.class)
  public ResponseEntity<Error> customerAlreadyExistsException(HttpServletRequest request, CustomerAlreadyExistsException ex, Locale locale) {
    log.warn("Customer already exists (404): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        // Use the fields from your exception object
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.CONFLICT.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 409 response
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<Error> customerNotFoundException(HttpServletRequest request, CustomerNotFoundException ex, Locale locale) {
    log.warn("Customer Not Found (404): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        // Use the fields from your exception object
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.NOT_FOUND.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 404 response
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ItemAlreadyExistsException.class)
  public ResponseEntity<Error> itemAlreadyExistsException(HttpServletRequest request, ItemAlreadyExistsException ex, Locale locale) {
    log.warn("Item already exists (409): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        // Use the fields from your exception object
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.CONFLICT.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 404 response
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<Error> itemNotFoundException(HttpServletRequest request, ItemNotFoundException ex, Locale locale) {
    // Log this as a WARN/INFO because it's a client error (404 Not Found), not a server failure (500)
    log.warn("Item Not Found (404): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        // Use the fields from your exception object
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.NOT_FOUND.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 404 response
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Error> productNotFoundException(HttpServletRequest request, ProductNotFoundException ex, Locale locale) {
    // Log this as a WARN/INFO because it's a client error (404 Not Found), not a server failure (500)
    log.warn("Product Not Found (404): {} for {} {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        // Use the fields from your exception object
        .createError(ex.getErrMsgKey(), ex.getErrorCode(),
            HttpStatus.NOT_FOUND.value()) // 404 Not Found
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Explicitly return a 404 response
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Error> constraintViolationException(HttpServletRequest request, ConstraintViolationException ex, Locale locale) {
    log.warn("Constraint violation: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.CONSTRAINT_VIOLATION.getErrMsgKey(),
            ErrorCode.CONSTRAINT_VIOLATION.getErrCode(),
            HttpStatus.BAD_REQUEST.value()) // 400 Bad Request
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Optionally add more detail from the exception
    // error.setErrors(ex.getConstraintViolations()...);

    // Use HttpStatus.BAD_REQUEST for the response
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Error> illegalArgumentException(HttpServletRequest request, IllegalArgumentException ex, Locale locale) {
    // Log this as a 'warn' because it's a client error, not a server failure
    log.warn("Illegal argument: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrMsgKey(),
            ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrCode(),
            HttpStatus.BAD_REQUEST.value()) // 400 Bad Request
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // Make sure to return BAD_REQUEST in the ResponseEntity itself
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  //  This is the exception Spring throws when the request body DTO fails Bean Validation
  //  (due to annotations like @NotNull, @Size, etc. on fields in AddAddressReq).
  //  This is the standard mechanism for handling @Valid on controller method arguments.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Error> methodArgumentNotValidException(
      HttpServletRequest request,
      MethodArgumentNotValidException ex,
      Locale locale) {

    // 1. Log the full details for debugging
    log.warn("Method Argument Not Valid: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    // 2. Extract and simplify field errors
    Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            // Use a cleaner default message or a specific property if available
            FieldError::getDefaultMessage
        ));

    // 3. Determine a clean, user-facing message
    String userFriendlyMessage;
    if (fieldErrors.size() == 1) {
      Map.Entry<String, String> singleError = fieldErrors.entrySet().iterator().next();
      // Example: "Field 'expires' failed validation: must match MM/YY format."
      userFriendlyMessage = String.format("Validation failed for field '%s': %s",
          singleError.getKey(), singleError.getValue());
    } else {
      // Generic message for multiple errors
      userFriendlyMessage = "Input validation failed. See documentation or error details for required fields.";
    }

    // 4. Create the main error object
    Error error = ErrorUtils
        .createError(
            ErrorCode.VALIDATION_ERROR.getErrMsgKey(),
            ErrorCode.VALIDATION_ERROR.getErrCode(),
            HttpStatus.BAD_REQUEST.value())
        .setMessage(userFriendlyMessage)
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    // 5. Optionally, attach the full field map to the error object if supported
    // If your Error class supports a Map<String, String> property:
    // error.setErrors(fieldErrors);
    // For demonstration, we rely on the clean message.

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
  public ResponseEntity<Error> methodArgumentTypeMismatchException(
      HttpServletRequest request,
      MethodArgumentTypeMismatchException ex,
      Locale locale) {

    log.warn("Method argument type mismatch: Failed to convert value '{}' to type '{}' for parameter '{}'. Error: {}",
        ex.getValue(), ex.getRequiredType(), ex.getName(), ex.getMessage());

    String message = String.format("Parameter '%s' has an invalid format. Expected type: %s",
        ex.getName(), Objects.nonNull(ex.getRequiredType()) ? ex.getRequiredType().getSimpleName() : "Unknown");

    Error error = ErrorUtils
        .createError(
            message,
            ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getErrCode(),
            HttpStatus.BAD_REQUEST.value())
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

     return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Error> httpMediaTypeNotSupportedException(HttpServletRequest request, HttpMediaTypeNotSupportedException ex, Locale locale) {
    log.warn("Http Media Type Not Supported: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MEDIATYPE_NOT_SUPPORTED.getErrMsgKey(),
            ErrorCode.HTTP_MEDIATYPE_NOT_SUPPORTED.getErrCode(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpMessageNotWritableException.class)
  public ResponseEntity<Error> httpMessageNotWritableException(HttpServletRequest request, HttpMessageNotWritableException ex, Locale locale) {
    log.warn("Http Message Mot Writable: {} for {} {}",
      ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MESSAGE_NOT_WRITABLE.getErrMsgKey(),
            ErrorCode.HTTP_MESSAGE_NOT_WRITABLE.getErrCode(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<Error> httpMediaTypeNotAcceptableException(HttpServletRequest request, HttpMediaTypeNotAcceptableException ex, Locale locale) {
    log.warn("Http Media Type Not Acceptable: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

    Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrMsgKey(),
            ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.getErrCode(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Error> httpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex, Locale locale) {
    log.warn("Http Message Not Readable Exception: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

   Error error = ErrorUtils
        .createError(ErrorCode.HTTP_MESSAGE_NOT_READABLE.getErrMsgKey(),
            ErrorCode.HTTP_MESSAGE_NOT_READABLE.getErrCode(),
            HttpStatus.NOT_ACCEPTABLE.value())
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
  }

  @ExceptionHandler(JsonParseException.class)
  public ResponseEntity<Error> jsonParseException(HttpServletRequest request, JsonParseException ex, Locale locale) {
    log.warn("Json Parse Exception: {} for {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL());

   Error error = ErrorUtils
        .createError(ErrorCode.JSON_PARSE_ERROR.getErrMsgKey(),
            ErrorCode.JSON_PARSE_ERROR.getErrCode(),
            HttpStatus.NOT_ACCEPTABLE.value())
       .setMessage(ex.getMessage())
       .setUrl(request.getRequestURL().toString())
       .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
  }

  // Generic server error handling
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> otherException(HttpServletRequest request, Exception ex, Locale locale) {
    log.error("Server error: {} for {} {} with stacktrace {}",
        ex.getMessage(), request.getMethod(), request.getRequestURL(), ex.getStackTrace());

    Error error = ErrorUtils
        .createError(ErrorCode.GENERIC_ERROR.getErrMsgKey(), ErrorCode.GENERIC_ERROR.getErrCode(),
            HttpStatus.INTERNAL_SERVER_ERROR.value())
        .setMessage(ex.getMessage())
        .setUrl(request.getRequestURL().toString())
        .setReqMethod(request.getMethod());

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

