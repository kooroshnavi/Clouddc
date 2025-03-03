package ir.tic.clouddc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.tic.clouddc.api.response.ErrorResult;
import ir.tic.clouddc.api.response.Response;
import ir.tic.clouddc.utils.UtilService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class RestAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> DEFINED_URLS = List.of("/api/ceph/cluster", "/api/ceph/messenger/usage", "/api/ceph/xas/usage", "/api/ceph/sedad/usage");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var requestURI = request.getRequestURI();
        boolean error = false;
        if (!requestURI.contains("api")) {
            filterChain.doFilter(request, response);
        } else {
            if (checkURLValidity(requestURI)) {
                var authentication = ApiAuthenticationService.authenticate(request);
                if (authentication.isPresent()) {
                    SecurityContextHolder.getContext().setAuthentication(authentication.get());
                    filterChain.doFilter(request, response);
                } else {
                    error = true;
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    Response errorResponse = new Response("Error", "عدم امکان احراز هویت", UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime()), List.of(new ErrorResult("توکن درخواست ارسال نشده و یا توکن ارسالی اشتباه / منقضی است")));
                    String unAuthorizedError = objectMapper.writeValueAsString(errorResponse);
                    response.getWriter().write(unAuthorizedError);
                }
            } else {
                error = true;
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType("application/json");
                Response errorResponse = new Response("Error", "خطا در ارسال درخواست", UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime()), List.of(new ErrorResult("آدرس درخواست اشتباه است")));
                String notFoundError = objectMapper.writeValueAsString(errorResponse);
                response.getWriter().write(notFoundError);
            }
            if (error) {
                response.flushBuffer();
                response.getWriter().flush();
                response.getWriter().close();
            }
        }
    }

    private boolean checkURLValidity(String requestURI) {
        return DEFINED_URLS.contains(requestURI);
    }
}
