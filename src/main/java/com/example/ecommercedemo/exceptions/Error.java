package com.example.ecommercedemo.exceptions;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true) // To allow setters being chained
public class Error implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Application error code, which is different from HTTP error code.
   */
  private String errorCode;

  /**
   * Short, human-readable summary of the problem.
   */
  private String message;

  /**
   * HTTP status code for this occurrence of the problem, set by the origin server.
   */
  private Integer status;

  /**
   * Url of request that produced the error.
   */
  private String url = "Not available";

  /**
   * Method of request that produced the error.
   */
  private String reqMethod = "Not available";

  /**
   * Specific setters for Url and method: ignore blank values when
   * setting url/reqMethod. Lombok will generate other getters/setters.
   */
  public Error setUrl(String url) {
    if (Strings.isNotBlank(url)) {
      this.url = url;
    }
    return this;
  }

  public Error setReqMethod(String method) {
    if (Strings.isNotBlank(method)) {
      this.reqMethod = method;
    }
    return this;
  }
}