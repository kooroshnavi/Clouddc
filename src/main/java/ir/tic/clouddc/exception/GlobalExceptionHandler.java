package ir.tic.clouddc.exception;

import io.netty.resolver.dns.DnsNameResolverException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import ir.tic.clouddc.api.response.ErrorResult;
import ir.tic.clouddc.api.response.Response;
import ir.tic.clouddc.cloud.CloudService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.NoRouteToHostException;
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

    private final CloudService cloudService;

    @Autowired
    public GlobalExceptionHandler(NotificationService notificationService, CloudService cloudService) {
        this.notificationService = notificationService;
        this.cloudService = cloudService;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView catchEntityNotFound(EntityNotFoundException entityNotFoundException) {
        log.error("EntityNotFoundException: " + entityNotFoundException.getMessage());

        return new ModelAndView("404"); // For MVC
    }

    @ExceptionHandler(SQLException.class)
    public ModelAndView catchSqlException(SQLException sqlException) {
        log.error("SQLException: " + sqlException.getMessage());
        notificationService.sendExceptionMessage(sqlException.getMessage(), LocalDateTime.now());

        return new ModelAndView("500"); // For MVC
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView catchAccessDeniedException(AccessDeniedException accessDeniedException) {
        log.error("AccessDeniedException: " + accessDeniedException.getMessage());

        return new ModelAndView("403"); // For MVC
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ModelAndView catchNoSuchElementException(NoSuchElementException noSuchElementException) {
        log.error("NoSuchElementException: " + noSuchElementException.getMessage());

        return new ModelAndView("404"); // For MVC
    }

    @ExceptionHandler({UnavailableException.class, NoRouteToHostException.class, DnsNameResolverTimeoutException.class, DnsNameResolverException.class, WebClientRequestException.class, WebClientException.class, WebClientResponseException.class})
    public ResponseEntity<Response> catchWebserviceException(HttpServletRequest request) {
        log.warn("Live Response Not Available");
        var requestURI = request.getRequestURI();
        if (requestURI.contains("cluster")) {
            return new ResponseEntity<>(cloudService.getSabzClusterData(), HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(cloudService.getSabzMessengerData(), HttpStatusCode.valueOf(200));
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response> catchNoHandlerException() {
        Response errorResponse = new Response("Error", "خطا در ارسال درخواست", UtilService.getFormattedPersianDateAndTime(LocalDate.now(), LocalTime.now()), List.of(new ErrorResult("آدرس درخواست اشتباه است")));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
