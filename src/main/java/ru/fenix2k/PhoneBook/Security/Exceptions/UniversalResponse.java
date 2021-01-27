package ru.fenix2k.PhoneBook.Security.Exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Custom view of exception message
 */
@Data
@NoArgsConstructor
public class UniversalResponse {

  /** HTTP status */
  private int status;
  /** Error message */
  private String message;
  /** Timestamp */
  private  long timeStamp;

  public UniversalResponse(int status, String message, long timeStamp) {
    this.status = status;
    this.message = message;
    this.timeStamp = timeStamp;
  }

  public UniversalResponse(int status, String message) {
    this.status = status;
    this.message = message;
    this.timeStamp = new Timestamp(System.currentTimeMillis()).getTime();
  }

}
