package ru.fenix2k.PhoneBook.Exception;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Operation Not Permited Exception
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Log4j
public class OperationNotPermitedException extends Exception {

  public OperationNotPermitedException(String message) {
    super(message);
    log.info("Message: " + message);
  }

}
