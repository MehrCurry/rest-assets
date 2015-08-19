package de.gzockoll.prototype.assets.boundary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GenericExcptionHandler {
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody String handleIllegalArgument(Exception e) {
        log.debug("Exception mapped to http error 422:" + e.toString());
        return e.getLocalizedMessage();
    }
    @ResponseStatus(HttpStatus.NOT_FOUND) // 401
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody String handleNoSuchElement(Exception e) {
        log.debug("Exception mapped to http error 401:" + e.toString());
        return e.getLocalizedMessage();
    }
}
