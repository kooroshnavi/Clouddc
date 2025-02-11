package ir.tic.clouddc.exception;

import ir.tic.clouddc.api.Response;
import ir.tic.clouddc.security.ApiAuthFailureResult;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response unknownException(Exception ex, WebRequest req) {
        return new Response("Error", UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime()), List.of(new ApiAuthFailureResult("خطا")));
    }
}
