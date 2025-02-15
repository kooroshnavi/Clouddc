package ir.tic.clouddc.exception;

import ir.tic.clouddc.rpc.response.ErrorResult;
import ir.tic.clouddc.rpc.response.Response;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.UnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String entityNotFound(EntityNotFoundException entityNotFoundException) {

        return "Not found";
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Response> catchNotSslRecordException(SQLException sqlException) {
        Response response = new Response("Error", "خطا در پردازش اطلاعات", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("پایگاه داده در دسترس نمی باشد")));

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UnavailableException.class)
    public ResponseEntity<Response> catchNotSslRecordException(UnavailableException unavailableException) {
        Response response = new Response("Error", "خطا در دریافت اطلاعات", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("وب سرویس ریموت سامانه مانیتورینگ جهت دریافت اطلاعات در دسترس نمی باشد")));

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> catchAccessDeniedException(AccessDeniedException accessDeniedException) {
        Response errorResponse = new Response("Error", "عدم امکان احراز هویت", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("توکن درخواست ارسال نشده و یا توکن ارسالی اشتباه / منقضی است")));

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Response> notFoundException(NoSuchElementException noSuchElementException) {
        Response errorResponse = new Response("Error", "خطا در ارسال درخواست", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("آدرس درخواست اشتباه است")));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response> noHandlerException(NoHandlerFoundException noHandlerFoundException) {
        Response errorResponse = new Response("Error", "خطا در ارسال درخواست", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("آدرس درخواست اشتباه است")));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
