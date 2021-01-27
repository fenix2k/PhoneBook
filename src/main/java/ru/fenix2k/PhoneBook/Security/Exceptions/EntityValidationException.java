package ru.fenix2k.PhoneBook.Security.Exceptions;

import lombok.extern.log4j.Log4j;

import java.util.List;

@Log4j
public class EntityValidationException extends Exception {

    private List<String> message;

    public EntityValidationException(String message) {
        this.message.add(message);
        log.info("Validation error: " + message);
    }

    public EntityValidationException(List<String> message) {
        this.message = message;
        log.info("Validation error: " + this.message);
    }

    public String getMessage() {
        return this.message.toString();
    }

    public List<String> getCustomMessage() {
        return this.message;
    }
}
