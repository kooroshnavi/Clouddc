package com.navi.dcim.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(AccessDeniedException accessDeniedException) {
        log.error(accessDeniedException.getMessage());
        return "403";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String noValue(NoSuchElementException noSuchElementException){
        log.error(noSuchElementException.getMessage());
        return "404";
    }

}
