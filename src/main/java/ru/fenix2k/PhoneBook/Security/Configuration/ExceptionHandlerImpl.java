package ru.fenix2k.PhoneBook.Security.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import ru.fenix2k.PhoneBook.Security.Exceptions.UniversalResponse;

/**
 * Define global exception handler
 */
//@RestControllerAdvice
public class ExceptionHandlerImpl {

  /**
   * Define global exception handler of specified exception
   * @param ex exception instance (autowired)
   * @return HTTP response with error info
   */
  //@ExceptionHandler
  public ResponseEntity<UniversalResponse> handleException(ResponseStatusException ex){
    UniversalResponse errorResponse = new UniversalResponse();
    errorResponse.setStatus(ex.getStatus().value());
    errorResponse.setMessage(ex.getMessage());// Need to change!!!!
    errorResponse.setTimeStamp(System.currentTimeMillis());
    return new ResponseEntity<>(errorResponse, HttpStatus.resolve(ex.getStatus().value()));
  }

}
