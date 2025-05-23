package ir.tic.clouddc.security;

import ir.tic.clouddc.otp.OTPService;
import ir.tic.clouddc.person.AddressRepository;
import ir.tic.clouddc.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class OTPAuthenticationProvider implements AuthenticationProvider {

    private final OTPService otpService;

    private final PersonService personService;

    private final AddressRepository addressRepository;

    private static final List<String> ROLES = Arrays.asList("OPERATOR", "SUPERVISOR", "VIEWER", "MANAGER", "ADMIN", "WEBSERVICE");  // char role: 0.1.2.3.4.5

    @Autowired
    public OTPAuthenticationProvider(OTPService otpService, PersonService personService, AddressRepository addressRepository) {
        this.otpService = otpService;
        this.personService = personService;
        this.addressRepository = addressRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var otpUid = authentication.getPrincipal().toString();
        var providedOtp = authentication.getCredentials().toString();
        if (providedOtp.isBlank() || providedOtp.toCharArray().length < 6) {
            throw new BadCredentialsException("Bad Input");
        }

        String result;
        try {
            result = otpService.verifyLoginOTP(otpUid, providedOtp);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (result.equals("0")) {
            throw new CredentialsExpiredException("OTP Expired");
        } else if (result.equals("-1")) {
            throw new BadCredentialsException("Invalid OTP");
        }

        var addressObject = addressRepository.findByValue(result);
        var personObject = personService.getReferencedPerson(addressObject.get().getId());
        var roleId = personObject.getRole();
        List<GrantedAuthority> personRoles = new ArrayList<>();
        switch (roleId) {
            case '0' -> personRoles.add(new SimpleGrantedAuthority(ROLES.get(0))); // OPERATOR
            case '1' -> personRoles.add(new SimpleGrantedAuthority(ROLES.get(1)));
            case '2' -> personRoles.add(new SimpleGrantedAuthority(ROLES.get(2)));
            case '3' -> personRoles.add(new SimpleGrantedAuthority(ROLES.get(3)));
            case '4' -> personRoles.add(new SimpleGrantedAuthority(ROLES.get(4)));
            case '5' -> {
                personRoles.add(new SimpleGrantedAuthority(ROLES.get(1)));
                personRoles.add(new SimpleGrantedAuthority(ROLES.get(3)));
            }
            case '6' -> personRoles.add(new SimpleGrantedAuthority(ROLES.get(5))); // WEBSERVICE
        }
        // map: 01234/5:13/6

        return new UsernamePasswordAuthenticationToken(personObject.getUsername(), null, personRoles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
