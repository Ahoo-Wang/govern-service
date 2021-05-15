package me.ahoo.govern.rest.config;

import me.ahoo.govern.rest.dto.ErrorResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ahoo wang
 */
@Component
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler
    @ResponseStatus
    public ErrorResponse handleAll(Exception ex) {
        return ErrorResponse.of(ex.getMessage());
    }
}
