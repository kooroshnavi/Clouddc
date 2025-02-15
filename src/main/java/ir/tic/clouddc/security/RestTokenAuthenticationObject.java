package ir.tic.clouddc.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RestTokenAuthenticationObject extends AbstractAuthenticationToken {
    private final String tokenId;

    public RestTokenAuthenticationObject(Collection<? extends GrantedAuthority> authorities, String tokenId) {
        super(authorities);
        this.tokenId = tokenId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.tokenId;
    }
}
