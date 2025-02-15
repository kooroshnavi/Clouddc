package ir.tic.clouddc.exception;

import ir.tic.clouddc.api.response.ErrorResult;
import ir.tic.clouddc.api.response.Response;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.UnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final NotificationService notificationService;

    @Autowired
    public GlobalExceptionHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ExceptionHandler({EntityNotFoundException.class, SQLException.class, AccessDeniedException.class, NoSuchElementException.class})
    public ModelAndView entityNotFound(EntityNotFoundException entityNotFoundException, SQLException sqlException, AccessDeniedException accessDeniedException, NoSuchElementException noSuchElementException) {
        log.error("MVC Exception");
        if (sqlException != null) {
            notificationService.sendExceptionMessage(sqlException.getMessage(), LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        }
        return new ModelAndView("404"); // For MVC
    }

    @ExceptionHandler(UnavailableException.class)
    public ResponseEntity<Response> catchUnavailableException(UnavailableException unavailableException) {
        Response response = new Response("Error", "خطا در دریافت اطلاعات", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response> noHandlerException(NoHandlerFoundException noHandlerFoundException) {
        Response errorResponse = new Response("Error", "خطا در ارسال درخواست", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("آدرس درخواست اشتباه است")));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
