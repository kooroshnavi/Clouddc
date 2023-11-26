package com.navi.dcim.security;

import com.navi.dcim.person.Person;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.person.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppAuthentication implements AuthenticationProvider {

    private final PersonService personService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppAuthentication(PersonService personService, PasswordEncoder passwordEncoder) {
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var requestUsername = authentication.getName().toString();
        var requestPassword = authentication.getCredentials().toString();

        Person person = personService.getPerson(requestUsername);

        if (person != null && person.getId() > 0 && passwordEncoder.matches(requestPassword, person.getPassword())) {
            return new UsernamePasswordAuthenticationToken(person.getName(), null, getGrantedAuthority(person.getRole()));
        } else {
            throw new BadCredentialsException("invalid username and/or password");
        }
    }

    private List<GrantedAuthority> getGrantedAuthority(Role roles) {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority(roles.getTitle()));
        return grantedAuthorityList;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }



}
