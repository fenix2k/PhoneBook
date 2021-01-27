package ru.fenix2k.PhoneBook.Security.Exceptions;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Log4j
public class ResourceNotFoundException extends Exception {

  public ResourceNotFoundException(String message) {
    super(message);
    log.info("Message: " + message);
  }

}
