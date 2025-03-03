package ir.tic.clouddc.security;

import ir.tic.clouddc.api.token.TokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;
public class ApiAuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "AUTH-TOKEN";

    public static Optional<Authentication> authenticate(HttpServletRequest request) {
        String authToken = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        var tokenId = TokenServiceImpl.isValidToken(authToken);
        if (authToken == null || tokenId == -1) {
            return Optional.empty();
        }
        return Optional.of(new RestTokenAuthenticationObject(List.of(new SimpleGrantedAuthority("API_GET_AUTH")), String.valueOf(tokenId)));
    }
}
