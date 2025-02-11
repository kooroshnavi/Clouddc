package ir.tic.clouddc.security;

import ir.tic.clouddc.api.ApiResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApiAuthenticationServiceImpl {
    private static final String AUTH_TOKEN_HEADER_NAME = "AUTH-TOKEN";
    private ApiResponseService apiResponseService;

    @Autowired
    public ApiAuthenticationServiceImpl(ApiResponseService apiResponseService) {
        this.apiResponseService = apiResponseService;
    }

    public ApiAuthenticationServiceImpl() {
    }

    public Optional<Authentication> getApiAuthentication(HttpServletRequest request) {
        String providedToken = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (providedToken != null) {
            var valid = apiResponseService.isAuthTokenValid(providedToken);
            if (valid) {
                return Optional.of(new ApiTokenAuthentication(List.of(new SimpleGrantedAuthority("API_GET_AUTH")), providedToken));
            }
        }

        return Optional.empty();
    }
}
