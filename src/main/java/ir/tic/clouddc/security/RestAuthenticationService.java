package ir.tic.clouddc.security;

import ir.tic.clouddc.rpc.token.TokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

@Slf4j
public class RestAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "AUTH-TOKEN";

    public static Optional<Authentication> authenticate(HttpServletRequest request) {
        String authToken = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        var tokenId = TokenServiceImpl.isValidToken(authToken);
        if (authToken == null || tokenId == -1) {
            log.info("failed authentication");
            return Optional.empty();
        }
        log.info("Successfully authenticated user");
        return Optional.of(new RestTokenAuthenticationObject(List.of(new SimpleGrantedAuthority("API_GET_AUTH")), String.valueOf(tokenId)));
    }
}
