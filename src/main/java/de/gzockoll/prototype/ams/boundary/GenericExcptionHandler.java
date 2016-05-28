package de.gzockoll.prototype.ams.boundary;

import de.gzockoll.prototype.ams.services.DuplicateKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

@ControllerAdvice @Slf4j public class GenericExcptionHandler {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    @ExceptionHandler(IllegalArgumentException.class) @ResponseBody String handleIllegalArgument(Exception e) {
        log.warn("Exception mapped to http error 422:" + e.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    @ExceptionHandler({NoSuchElementException.class, FileNotFoundException.class}) @ResponseBody String handleNotFound(
            Exception e) {
        log.warn("Exception mapped to http error 404:" + e.toString());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalStateException.class, ConstraintViolationException.class})
    @ResponseBody
    String handleIllegalState(Exception e) {
        log.warn("Exception mapped to http error 400", e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT) @ExceptionHandler(DuplicateKeyException.class) @ResponseBody
    String handleDuplicateKey(Exception e) {
        log.warn("Exception mapped to http error " + HttpStatus.CONFLICT, e);
        return e.getMessage();
    }
}
