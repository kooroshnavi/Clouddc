package com.navi.dcim.security;

import com.navi.dcim.otp.OtpService;
import com.navi.dcim.person.AddressRepository;
import com.navi.dcim.person.PersonService;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final OtpService otpService;
    private final PersonService personService;
    private final AddressRepository addressRepository;

    @Autowired
    public OtpAuthenticationProvider(OtpService otpService, PersonService personService, AddressRepository addressRepository) {
        this.otpService = otpService;
        this.personService = personService;
        this.addressRepository = addressRepository;

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var otpUid = authentication.getPrincipal().toString();
        var providedOtp = authentication.getCredentials().toString();
        if (providedOtp.isBlank() || providedOtp.isEmpty() || providedOtp.toCharArray().length < 6) {
            throw new BadCredentialsException("Invalid OTP");
        }

        String result;
        try {
            result = otpService.verifyOtp(otpUid, providedOtp);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (result.equals("0")) {
            throw new CredentialsExpiredException("OTP Expired");
        } else if (result.equals("-1")) {
            throw new BadCredentialsException("Invalid OTP");
        }

        var personAddressObject = addressRepository.findByValue(result);
        var person = personService.getPerson(personAddressObject.get().getId());
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ADMIN"));

        return new UsernamePasswordAuthenticationToken(person.getUsername(), null, authorityList);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
