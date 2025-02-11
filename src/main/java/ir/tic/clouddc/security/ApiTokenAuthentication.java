package ir.tic.clouddc.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiTokenAuthentication extends AbstractAuthenticationToken {

    private final String apiToken;

    public ApiTokenAuthentication(Collection<? extends GrantedAuthority> authorities, String apiToken) {
        super(authorities);
        this.apiToken = apiToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiToken;
    }
}
