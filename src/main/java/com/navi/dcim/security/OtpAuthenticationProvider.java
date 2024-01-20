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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final OtpService otpService;
    private final PersonService personService;
    private final AddressRepository addressRepository;
    private static final List<String> ROLES = Arrays.asList("OPERATOR", "SUPERVISOR", "VIEWER", "MANAGER");

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
        /*
        List<String > roleList = new ArrayList<>();
        roleList.add(ROLES.get(0));
        roleList.add(ROLES.get(1));
        roleList.add(ROLES.get(3));
        person.setRoles(roleList);
        String random = new DecimalFormat("0000")
                .format(new Random().nextInt(9999));
        person.setUsername("user" + random + person.getId());
        personService.updatePerson(person);
*/
        List<GrantedAuthority> authorityList = new ArrayList<>();
        for (String role : person.getRoles()
        ) {
            authorityList.add(new SimpleGrantedAuthority(role));
        }
        return new UsernamePasswordAuthenticationToken(person.getUsername(), null, authorityList);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
