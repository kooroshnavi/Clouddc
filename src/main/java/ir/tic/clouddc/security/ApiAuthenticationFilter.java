package ir.tic.clouddc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.tic.clouddc.api.ApiResponseService;
import ir.tic.clouddc.api.Response;
import ir.tic.clouddc.utils.UtilService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ApiAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ApiAuthenticationServiceImpl apiAuthenticationService;

    @Autowired
    private ApiResponseService apiResponseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authentication = apiAuthenticationService.getApiAuthentication(request);
        if (authentication.isPresent()) {
            SecurityContextHolder.getContext().setAuthentication(authentication.get());
            filterChain.doFilter(request, response);
        }
        else {
            try {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                Response errorResponse = new Response("خطای توکن", UtilService.getFormattedPersianDateAndTime(UtilService.getDATE(), UtilService.getTime()), List.of(new ApiAuthFailureResult("توکن درخواست ارسال نشده و یا توکن ارسالی اشتباه / منقضی می باشد.")));
                String jsonError = objectMapper.writeValueAsString(errorResponse);
                response.getWriter().write(jsonError);

            } catch (AccessDeniedException e) {
                log.info(e.toString());
            }
            response.flushBuffer();
            response.getWriter().flush();
            response.getWriter().close();
        }
    }
}
