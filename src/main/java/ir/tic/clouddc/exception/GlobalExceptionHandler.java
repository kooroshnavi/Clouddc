package ir.tic.clouddc.exception;

import ir.tic.clouddc.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final NotificationService notificationService;

    @Autowired
    public GlobalExceptionHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(AccessDeniedException accessDeniedException) {
        log.error(accessDeniedException.getMessage());
        return "403";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String noValue(NoSuchElementException noSuchElementException) {
        log.error(noSuchElementException.getMessage());
        return "404";
    }

    @ExceptionHandler(SQLException.class)
    public void sqlException(SQLException sqlException) {
        log.error(sqlException.getSQLState());
        log.error(sqlException.getMessage());
        notificationService.sendExceptionMessage(sqlException.getMessage(), LocalDateTime.now());
    }

}
