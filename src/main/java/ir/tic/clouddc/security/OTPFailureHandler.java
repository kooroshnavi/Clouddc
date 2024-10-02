package ir.tic.clouddc.security;

import ir.tic.clouddc.otp.OTPService;
import ir.tic.clouddc.person.PersonService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class OTPFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final PersonService personService;

    private final OTPService otpService;

    @Autowired
    public OTPFailureHandler(PersonService personService, OTPService otpService) {
        this.personService = personService;
        this.otpService = otpService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String redirectURL;
        if (exception.getClass().equals(BadCredentialsException.class)) {
            redirectURL = "/login/otp/verify?otpUid=" + request.getParameter("username");
            try {
                String address = otpService.getPersonAddress(request.getParameter("username"));
                if (address != null) {
                    personService.registerLoginHistory(address, request.getRemoteAddr(), false);
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else if (exception.getClass().equals(CredentialsExpiredException.class)) {
            redirectURL = "/login";
        } else {
            redirectURL = "/login?multiple=true";
        }

        super.setDefaultFailureUrl(redirectURL);
        super.onAuthenticationFailure(request, response, exception);
    }
}
