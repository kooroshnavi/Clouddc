package ir.tic.clouddc.exception;

import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final NotificationService notificationService;

    @Autowired
    public GlobalExceptionHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ExceptionHandler(SQLException.class)
    public void sqlException(SQLException sqlException) {
        log.error(sqlException.getSQLState());
        log.error(sqlException.getMessage());
        notificationService.sendExceptionMessage(sqlException.getMessage(), LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(AccessDeniedException accessDeniedException) {
        return "403";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String noValue(NoSuchElementException noSuchElementException) {
        return "404";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String noValue(FileNotFoundException fileNotFoundException) {
        log.error(fileNotFoundException.getMessage());
        return "404";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String noValue(EntityNotFoundException entityNotFoundException) {
        log.error(entityNotFoundException.getMessage());
        notificationService.sendExceptionMessage(entityNotFoundException.getMessage(), LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        return "404";
    }

    @ExceptionHandler(DateTimeParseException.class)
    public String dateError(DateTimeParseException dateTimeParseException) {
        log.error(dateTimeParseException.getMessage());
        return "404";
    }

}
