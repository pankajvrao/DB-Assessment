package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorResponse {

  private String errorId;
  private String errorMessage;
  private String timestamp;

  public ErrorResponse() {
    
  }

  @JsonCreator
  public ErrorResponse(@JsonProperty("errorId") String errorId,
		  @JsonProperty("errorMessage") String errorMessage,
		  @JsonProperty("timestamp") String timestamp) {
    this.errorId = errorId;
    this.errorMessage = errorMessage;
    this.timestamp = timestamp;
  }
}
